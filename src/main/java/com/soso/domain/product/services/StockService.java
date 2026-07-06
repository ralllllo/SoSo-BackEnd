package com.soso.domain.product.services;

import com.soso.domain.product.dao.StockDAO;
import com.soso.domain.product.dao.StockHistoryDAO;
import com.soso.domain.product.dto.*;
import com.soso.domain.notification.events.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class StockService {

    @Autowired
    private StockDAO stockDAO;

    @Autowired
    private StockHistoryDAO stockHistoryDAO;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private com.soso.domain.notification.dao.NotificationDAO notificationDAO;

    public List<StockDTO> getStockList(Map<String, Object> filters) {
        return stockDAO.selectStockList(filters);
    }

    public void createStock(int storeSeq,StockDTO stock) {
        // 동일한 품목명(stock_name) 중복 검사 로직 추가
        int count = stockDAO.checkDuplicateStockName(storeSeq, stock.getStockName());
        if (count > 0) {
            throw new IllegalArgumentException("이미 존재하는 품목명입니다.");
        }

        stockDAO.insertStock(storeSeq,stock);
        
        StockDTO savedStock = stockDAO.selectStockBySeq(stock.getStockSeq(), storeSeq);
        upsertStockRag(savedStock, storeSeq);
    }

    @Transactional
    public void processIncoming(StockIncomingDTO incoming, int storeSeq) {
        StockDTO master = stockDAO.selectStockBySeq(incoming.getStockSeq(), storeSeq);
        if (master == null) {
            throw new RuntimeException("존재하지 않는 품목입니다.");
        }
        
        // 1. 유통기한 자동 계산
        LocalDate expDate = incoming.getExpirationDate();
        if (expDate == null) {
            expDate = LocalDate.now().plusDays(master.getDefaultExpiryDays());
        }

        // 2. 로트 번호 생성 (LOT-YYYYMMDD-[stockSeq]-[랜덤3자리])
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String lotNumber = String.format("LOT-%s-%d-%03d", today, master.getStockSeq(), new Random().nextInt(1000));

        // 3. 배치 등록
        StockBatchDTO batch = new StockBatchDTO();
        batch.setStockSeq(incoming.getStockSeq());
        batch.setStoreSeq(storeSeq);
        batch.setLotNumber(lotNumber);
        batch.setDetailStockName(incoming.getDetailStockName());
        batch.setInitialQuantity(incoming.getQuantity());
        batch.setCurrentQuantity(incoming.getQuantity());
        batch.setIncomingPrice(incoming.getIncomingPrice());
        batch.setExpirationDate(expDate);
        stockDAO.insertBatch(batch);

        // 4. 마스터 총재고 업데이트
        updateMasterStock(incoming.getStockSeq(), incoming.getQuantity(), storeSeq);

        // 5. 이력 기록
        StockDTO updatedMaster = stockDAO.selectStockBySeq(incoming.getStockSeq(), storeSeq);
        StockHistoryDTO history = new StockHistoryDTO();
        history.setStockSeq(incoming.getStockSeq());
        history.setStoreSeq(storeSeq);
        history.setBatchSeq(batch.getBatchSeq());
        history.setTransactionType("INCOMING");
        history.setChangeQuantity(incoming.getQuantity());
        history.setCurrentTotalStock(updatedMaster.getCurrentStock());
        history.setDetailStockName(incoming.getDetailStockName());
        history.setPrice(incoming.getIncomingPrice());
        history.setExpirationDate(expDate);
        history.setReason(incoming.getReason());
        history.setMemo(incoming.getMemo());
        stockDAO.insertHistory(history);
        // 재고 이력 RAG upsert
        upsertStockHistoryRag(history, updatedMaster, storeSeq);

        // 현재 재고 RAG upsert
        upsertStockRag(updatedMaster, storeSeq);
    }

    @Transactional
    public void processOutbound(StockOutboundRequest request, int storeSeq) {
        int remainingToOut = request.getQuantity();
        
        // 1. FIFO 기준 배치 조회 (유통기한 임박순)
        List<StockBatchDTO> batches = stockDAO.selectAvailableBatches(request.getStockSeq(), storeSeq);
        
        if (batches.isEmpty() || batches.stream().mapToInt(StockBatchDTO::getCurrentQuantity).sum() < remainingToOut) {
            throw new RuntimeException("재고가 부족하여 출고를 완료할 수 없습니다.");
        }

        for (StockBatchDTO batch : batches) {
            if (remainingToOut <= 0) break;

            int deductQty = Math.min(batch.getCurrentQuantity(), remainingToOut);
            
            // 2. 배치 수량 차감
            batch.setCurrentQuantity(batch.getCurrentQuantity() - deductQty);
            stockDAO.updateBatchQuantity(batch);
            
            remainingToOut -= deductQty;
            
            // 3. 마스터 총재고 차감
            updateMasterStock(request.getStockSeq(), -deductQty, storeSeq);
            
            // 4. 이력 기록
            StockDTO currentMaster = stockDAO.selectStockBySeq(request.getStockSeq(), storeSeq);
            StockHistoryDTO history = new StockHistoryDTO();
            history.setStockSeq(request.getStockSeq());
            history.setStoreSeq(storeSeq);
            history.setBatchSeq(batch.getBatchSeq());
            history.setTransactionType("OUTBOUND");
            history.setChangeQuantity(-deductQty);
            history.setCurrentTotalStock(currentMaster.getCurrentStock());
            history.setReason(request.getReason());
            history.setMemo(request.getMemo());
            history.setDetailStockName(batch.getDetailStockName()); 
            history.setExpirationDate(batch.getExpirationDate());
            history.setPrice(batch.getIncomingPrice());
            stockDAO.insertHistory(history);
            // 재고 이력 RAG upsert
            upsertStockHistoryRag(history, currentMaster, storeSeq);
        }

        // 5. 안전재고 미달 체크 (ALERT) 및 실시간 알림 발행
        StockDTO finalMaster = stockDAO.selectStockBySeq(request.getStockSeq(), storeSeq);
        if (finalMaster != null && finalMaster.getCurrentStock() < finalMaster.getSafetyStock()) {
            StockHistoryDTO alertHistory = new StockHistoryDTO();
            alertHistory.setStockSeq(request.getStockSeq());
            alertHistory.setStoreSeq(storeSeq);
            alertHistory.setTransactionType("ALERT");
            alertHistory.setChangeQuantity(0);
            alertHistory.setCurrentTotalStock(finalMaster.getCurrentStock());
            alertHistory.setReason("안전재고 미달");
            alertHistory.setDetailStockName(finalMaster.getStockName());
            alertHistory.setMemo("현재 " + finalMaster.getCurrentStock() + "개 - 안전 재고 미만");
            stockHistoryDAO.insertStockHistory(alertHistory);

            // 알림 이벤트 발행
            eventPublisher.publishEvent(new NotificationEvent(
                this, 
                storeSeq, 
                "SAFETY_LACK", 
                "안전재고 부족 알림", 
                String.format("[%s] 재고가 안전 기준(%d개) 미만으로 떨어졌습니다. (현재: %d개)", 
                    finalMaster.getStockName(), finalMaster.getSafetyStock(), finalMaster.getCurrentStock())
            ));

            // 파트너(거래처)로도 알림 발행
            publishPartnerSafetyAlert(storeSeq, finalMaster);
        }
        upsertStockRag(finalMaster, storeSeq);
    }

    @Transactional
    public void processAdjust(StockAdjustRequest request, int storeSeq) {
        // 1. 특정 배치 조정 시
        if (request.getBatchSeq() != null && request.getBatchSeq() > 0) {
            List<StockBatchDTO> batches = stockDAO.selectBatchesByStockSeq(request.getStockSeq(), storeSeq);
            StockBatchDTO targetBatch = batches.stream()
                .filter(b -> b.getBatchSeq() == request.getBatchSeq())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 배치를 찾을 수 없습니다."));
            
            targetBatch.setCurrentQuantity(targetBatch.getCurrentQuantity() + request.getChangeQuantity());
            if (targetBatch.getCurrentQuantity() < 0) {
                throw new RuntimeException("조정 후 배치 재고가 0보다 작을 수 없습니다.");
            }
            stockDAO.updateBatchQuantity(targetBatch);
        }

        // 2. 마스터 업데이트
        updateMasterStock(request.getStockSeq(), request.getChangeQuantity(), storeSeq);
        
        // 3. 이력 기록
        StockDTO currentMaster = stockDAO.selectStockBySeq(request.getStockSeq(), storeSeq);
        if (currentMaster.getCurrentStock() < 0) {
             throw new RuntimeException("조정 후 총 재고가 0보다 작을 수 없습니다.");
        }
        
        StockHistoryDTO history = new StockHistoryDTO();
        history.setStockSeq(request.getStockSeq());
        history.setStoreSeq(storeSeq);
        history.setBatchSeq(request.getBatchSeq());
        history.setTransactionType("ADJUST");
        history.setChangeQuantity(request.getChangeQuantity());
        history.setCurrentTotalStock(currentMaster.getCurrentStock());
        history.setReason(request.getReason());
        history.setMemo(request.getMemo());
        stockDAO.insertHistory(history);
        // 재고 이력 RAG upsert
        upsertStockHistoryRag(history, currentMaster, storeSeq);

        // 안전재고 미달 체크 및 실시간 알림 발행
        if (currentMaster != null && currentMaster.getCurrentStock() < currentMaster.getSafetyStock()) {
            eventPublisher.publishEvent(new NotificationEvent(
                this, 
                storeSeq, 
                "SAFETY_LACK", 
                "안전재고 부족 알림", 
                String.format("[%s] 재고 조정 후 재고가 안전 기준(%d개) 미만으로 떨어졌습니다. (현재: %d개)", 
                    currentMaster.getStockName(), currentMaster.getSafetyStock(), currentMaster.getCurrentStock())
            ));

            // 파트너(거래처)로도 알림 발행
            publishPartnerSafetyAlert(storeSeq, currentMaster);
        }
        upsertStockRag(currentMaster, storeSeq);
    }

    private void updateMasterStock(int stockSeq, int changeQty, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        params.put("changeQuantity", changeQty);
        stockDAO.updateCurrentStock(params);
    }

    public List<StockBatchDTO> getBatches(int stockSeq, int storeSeq) {
        return stockDAO.selectBatchesByStockSeq(stockSeq, storeSeq);
    }

    /**
     * [초보자 가이드 - 파트너 안전재고 부족 알림 발행]
     * 기능: 가맹점의 재고가 안전재고 수량 미만으로 떨어졌을 때, 가맹점과 거래 중인 파트너(거래처)들의 대시보드로
     *      실시간 "안전재고 부족" 알림(SAFETY_LACK)을 동시에 발행해주는 헬퍼 메소드입니다.
     * @param businessStoreSeq 재고가 부족해진 소상공인 가맹점 매장 번호 (stores.store_seq)
     * @param master 재고 부족이 발생한 품목의 마스터 DTO 객체 (품목명, 현재고 등)
     */
    private void publishPartnerSafetyAlert(int businessStoreSeq, StockDTO master) {
        try {
            // 1. 재고가 부족한 가맹점 매장의 상호명(예: 교촌치킨 옥길점)을 조회
            String companyName = notificationDAO.selectCompanyNameByStoreSeq(businessStoreSeq);
            if (companyName == null || companyName.isEmpty()) {
                companyName = "가맹점";
            }
            
            // 2. 가맹점과 거래 관계가 맺어진 파트너(거래처)들의 매장 일련번호 목록을 조회
            List<Integer> partnerSeqs = notificationDAO.selectPartnerStoreSeqsByBusinessSeq(businessStoreSeq);
            if (partnerSeqs != null) {
                // 3. 각 파트너(거래처)들을 순회하며 각각의 알림 채널로 실시간 알림 이벤트를 전송
                for (Integer partnerStoreSeq : partnerSeqs) {
                    eventPublisher.publishEvent(new NotificationEvent(
                        this,
                        partnerStoreSeq,
                        "SAFETY_LACK",
                        "가맹점 안전재고 부족",
                        String.format("[%s]의 [%s] 품목이 안전재고 미달 상태입니다. (현재: %d개)", 
                            companyName, master.getStockName(), master.getCurrentStock())
                    ));
                }
            }
        } catch (Exception e) {
        }
    }

    public List<StockHistoryDTO> getHistories(int stockSeq, int storeSeq) {
        return stockDAO.selectHistoriesByStockSeq(stockSeq, storeSeq);
    }

    public void updateStockInfo(StockDTO stock) {
        stockDAO.updateStock(stock);
        
        StockDTO updatedStock = stockDAO.selectStockBySeq(stock.getStockSeq(), stock.getStoreSeq());
        upsertStockRag(updatedStock, stock.getStoreSeq());
    }

    @Transactional
    public void deleteStock(int stockSeq, int storeSeq) {
        // 1. 연관된 이력 삭제
        stockDAO.deleteHistoryByStockSeq(stockSeq, storeSeq);
        // 2. 연관된 배치 삭제
        stockDAO.deleteBatchesByStockSeq(stockSeq, storeSeq);
        // 3. 품목 마스터 삭제
        stockDAO.deleteStock(stockSeq, storeSeq);
    }
    public int getcountExpiringSoon(Map<String, Object> filters) {
        return stockDAO.getcountExpiringSoon(filters);
    }
    public int getcountExpiringSoon(int storeSeq) {
        return stockDAO.selectgetcountExpiringSoon(storeSeq);
    }
    
    // 재고 RAG upsert
    private void upsertStockRag(StockDTO stock, int storeSeq) {
        try {
            if (stock == null) {
                return;
            }

            String unit = stock.getUnit() == null || stock.getUnit().isBlank()
                    ? "개"
                    : stock.getUnit();

            String displayUnit = "개";

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("stockSeq", stock.getStockSeq());
            metadata.put("storeSeq", storeSeq);
            metadata.put("stockName", stock.getStockName());
            metadata.put("category", stock.getCategory());
            metadata.put("unit", unit);
            metadata.put("displayUnit", displayUnit);
            metadata.put("currentStock", stock.getCurrentStock());
            metadata.put("safetyStock", stock.getSafetyStock());
            metadata.put("defaultExpiryDays", stock.getDefaultExpiryDays());

            String text = String.format(
                    "[재고 정보] 매장번호: %s, 재고번호: %s, 재고명: %s, 카테고리: %s, 현재 보유 재고: %s개, 안전재고 기준: %s개, 기본 유통기한: %s일.",
                    storeSeq,
                    stock.getStockSeq(),
                    stock.getStockName(),
                    stock.getCategory(),
                    stock.getCurrentStock(),
                    stock.getSafetyStock(),
                    stock.getDefaultExpiryDays()
            );

        } catch (Exception e) {
        }
    }
    
    // rag 단위 정리 메서드 
    private String normalizeUnitForRag(String unit) {
        if (unit == null || unit.isBlank()) {
            return "개";
        }

        String trimmed = unit.trim();

        // 1개, 1판, 1박스 같은 단위는 챗봇 문장에서는 개/판/박스로 표현
        if (trimmed.startsWith("1") && trimmed.length() > 1) {
            char secondChar = trimmed.charAt(1);

            // 10kg, 100g 같은 단위는 자르면 안 되므로 두 번째 글자가 숫자면 그대로 사용
            if (!Character.isDigit(secondChar)) {
                return trimmed.substring(1);
            }
        }

        return trimmed;
    }
    
    
    
    // 재고 이력 RAG upsert
    private void upsertStockHistoryRag(StockHistoryDTO history, StockDTO stock, int storeSeq) {
        try {
            if (history == null) {
                return;
            }

            if (history.getHistorySeq() == 0) {
                return;
            }

            String stockName = "정보 없음";
            String category = "정보 없음";
            String unit = "개";

            if (stock != null) {
                stockName = stock.getStockName() == null || stock.getStockName().isBlank()
                        ? "정보 없음"
                        : stock.getStockName();

                category = stock.getCategory() == null || stock.getCategory().isBlank()
                        ? "정보 없음"
                        : stock.getCategory();

                unit = stock.getUnit() == null || stock.getUnit().isBlank()
                        ? "개"
                        : stock.getUnit();
            }

            String displayUnit = "개";

            String transactionTypeName = convertTransactionType(history.getTransactionType());

            String reason = history.getReason() == null || history.getReason().isBlank()
                    ? "없음"
                    : history.getReason();

            String memo = history.getMemo() == null || history.getMemo().isBlank()
                    ? "없음"
                    : history.getMemo();

            String detailStockName = history.getDetailStockName() == null || history.getDetailStockName().isBlank()
                    ? stockName
                    : history.getDetailStockName();

            Object priceObj = history.getPrice();
            String price = priceObj == null ? "0" : String.valueOf(priceObj);

            Object expirationDateObj = history.getExpirationDate();
            String expirationDate = expirationDateObj == null ? "정보 없음" : String.valueOf(expirationDateObj);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("historySeq", history.getHistorySeq());
            metadata.put("stockSeq", history.getStockSeq());
            metadata.put("storeSeq", storeSeq);
            metadata.put("stockName", stockName);
            metadata.put("category", category);
            metadata.put("unit", unit);
            metadata.put("displayUnit", displayUnit);
            metadata.put("transactionType", history.getTransactionType());
            metadata.put("transactionTypeName", transactionTypeName);
            metadata.put("changeQuantity", history.getChangeQuantity());
            metadata.put("currentTotalStock", history.getCurrentTotalStock());
            metadata.put("detailStockName", detailStockName);
            metadata.put("price", price);
            metadata.put("expirationDate", expirationDate);
            metadata.put("reason", reason);
            metadata.put("memo", memo);

            String text = String.format(
                    "[재고 이력 정보] 이력번호: %s, 매장번호: %s, 재고번호: %s, 재고명: %s, 카테고리: %s, 처리유형: %s, 변경수량: %s개, 처리 후 총재고: %s개, 상세품목명: %s, 가격: %s원, 유통기한: %s, 사유: %s, 메모: %s.",
                    history.getHistorySeq(),
                    storeSeq,
                    history.getStockSeq(),
                    stockName,
                    category,
                    transactionTypeName,
                    history.getChangeQuantity(),
                    history.getCurrentTotalStock(),
                    detailStockName,
                    price,
                    expirationDate,
                    reason,
                    memo
            );

        } catch (Exception e) {
        }
    }
    private String convertTransactionType(String transactionType) {
        if (transactionType == null) {
            return "정보 없음";
        }

        switch (transactionType) {
            case "INCOMING":
                return "입고";
            case "OUTBOUND":
                return "출고";
            case "ADJUST":
                return "재고 조정";
            case "ALERT":
                return "안전재고 알림";
            default:
                return transactionType;
        }
    }
}
