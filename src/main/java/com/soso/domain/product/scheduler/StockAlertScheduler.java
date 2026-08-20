package com.soso.domain.product.scheduler;

import com.soso.domain.product.dao.StockDAO;
import com.soso.domain.product.dao.StockHistoryDAO;
import com.soso.domain.product.dto.StockBatchDTO;
import com.soso.domain.product.dto.StockDTO;
import com.soso.domain.product.dto.StockHistoryDTO;
import com.soso.domain.notification.events.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class StockAlertScheduler {

    @Autowired
    private StockDAO stockDAO;

    @Autowired
    private StockHistoryDAO stockHistoryDAO;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkExpiringStock() {
        
        List<StockBatchDTO> expiringBatches = stockDAO.selectExpiringBatches();

        for (StockBatchDTO batch : expiringBatches) {
            
            
            StockDTO master = stockDAO.selectStockBySeq(batch.getStockSeq(), batch.getStoreSeq());
            
            if (master != null) {
                
                StockHistoryDTO alertHistory = new StockHistoryDTO();
                alertHistory.setStockSeq(batch.getStockSeq());
                alertHistory.setStoreSeq(batch.getStoreSeq()); 
                alertHistory.setBatchSeq(batch.getBatchSeq());
                alertHistory.setTransactionType("ALERT");
                alertHistory.setChangeQuantity(0);
                alertHistory.setCurrentTotalStock(master.getCurrentStock());
                alertHistory.setDetailStockName(batch.getDetailStockName());
                alertHistory.setPrice(batch.getIncomingPrice());
                alertHistory.setExpirationDate(batch.getExpirationDate());
                alertHistory.setReason("유통기한 임박");
                alertHistory.setMemo("만료 예정일: " + batch.getExpirationDate() + " 확인 필요");
                
                stockHistoryDAO.insertStockHistory(alertHistory);

                
                eventPublisher.publishEvent(new NotificationEvent(
                    this, 
                    batch.getStoreSeq(), 
                    "EXPIRY_IMMINENT", 
                    "유통기한 임박 알림", 
                    String.format("[%s] 품목의 배치(유통기한: %s)가 만료 예정입니다.", 
                        batch.getDetailStockName(), batch.getExpirationDate())
                ));
            }
        }
    }
}
