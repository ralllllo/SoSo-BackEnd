package com.soso.domain.order.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.soso.domain.order.dao.PartnerOrderDAO;
import com.soso.domain.order.dao.OrderDAO;
import com.soso.domain.order.dto.PartnerOrderDetailDTO;
import com.soso.domain.order.dto.PartnerOrderListDTO;
import com.soso.domain.notification.events.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;


@Service
public class PartnerOrderService {

    @Autowired
    private PartnerOrderDAO partnerOrderDAO; 

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; 

    
    public List<PartnerOrderListDTO> getOrderList(Long sellerSeq, String keyword, String status) {
        return partnerOrderDAO.selectOrderList(sellerSeq, keyword, status);
    }

    
    public List<PartnerOrderDetailDTO> getOrderDetail(Long orderSeq) {
        return partnerOrderDAO.selectOrderDetail(orderSeq);
    }

    
    public void updateOrderStatus(Long orderSeq, String status) {
        
        partnerOrderDAO.updateOrderStatus(orderSeq, status);

        
        Long buyerSeq = partnerOrderDAO.selectBuyerSeq(orderSeq);

        
        Map<String, Object> message = new HashMap<>();
        message.put("orderSeq", orderSeq);
        message.put("status", status);
        message.put("message", getStatusMessage(status));

        
        messagingTemplate.convertAndSend("/sub/order/" + buyerSeq, (Object) message);

        
        try {
            Map<String, Object> orderInfo = orderDAO.findOrderInfoBySeq(orderSeq);
            if (orderInfo != null && buyerSeq != null) {
                String orderNo = String.valueOf(orderInfo.get("orderNo"));
                eventPublisher.publishEvent(new NotificationEvent(
                    this,
                    buyerSeq.intValue(),
                    "ORDER_STATUS",
                    "발주 상태 변경",
                    String.format("발주번호 [%s]의 %s", orderNo, getStatusMessage(status))
                ));
            }
        } catch (Exception e) {
        }
    }

    
    private String getStatusMessage(String status) {
        switch (status) {
            case "ACCEPTED": return "발주가 접수완료되었습니다.";
            case "PREPARING": return "상품준비가 시작되었습니다.";
            case "SHIPPING": return "상품이 배송중입니다.";
            case "DELIVERED": return "배송이 완료되었습니다.";
            default: return "발주 상태가 변경되었습니다.";
        }
    }

    
    public Map<String, Object> getDashboardData(Long sellerSeq) {
        Map<String, Object> data = new HashMap<>();

        
        
        
        
        int todayNewOrders = partnerOrderDAO.selectTodayNewOrdersCount(sellerSeq);
        int shippingOrders = partnerOrderDAO.selectShippingOrdersCount(sellerSeq);
        int waitingPayments = partnerOrderDAO.selectWaitingPaymentsAmount(sellerSeq);

        data.put("todayNewOrders", todayNewOrders);
        data.put("shippingOrders", shippingOrders);
        data.put("waitingPayments", waitingPayments);

        
        
        
        List<Map<String, Object>> salesRaw = partnerOrderDAO.selectMonthlySales(sellerSeq);
        List<Map<String, Object>> collectionsRaw = partnerOrderDAO.selectMonthlyCollections(sellerSeq);

        
        
        
        Map<String, Map<String, Object>> mergeMap = new java.util.LinkedHashMap<>();
        
        java.time.LocalDate now = java.time.LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate targetDate = now.minusMonths(i);
            String monthName = targetDate.getMonthValue() + "월";
            Map<String, Object> mData = new HashMap<>();
            mData.put("month", monthName);
            mData.put("sales", 0);
            mData.put("collection", 0);
            mergeMap.put(monthName, mData);
        }

        
        if (salesRaw != null) {
            for (Map<String, Object> s : salesRaw) {
                String m = String.valueOf(s.get("month"));
                int amt = s.get("amount") != null ? ((Number) s.get("amount")).intValue() : 0;
                if (mergeMap.containsKey(m)) {
                    mergeMap.get(m).put("sales", amt);
                } else {
                    Map<String, Object> mData = new HashMap<>();
                    mData.put("month", m);
                    mData.put("sales", amt);
                    mData.put("collection", 0);
                    mergeMap.put(m, mData);
                }
            }
        }

        
        if (collectionsRaw != null) {
            for (Map<String, Object> c : collectionsRaw) {
                String m = String.valueOf(c.get("month"));
                int amt = c.get("amount") != null ? ((Number) c.get("amount")).intValue() : 0;
                if (mergeMap.containsKey(m)) {
                    mergeMap.get(m).put("collection", amt);
                } else {
                    Map<String, Object> mData = new HashMap<>();
                    mData.put("month", m);
                    mData.put("sales", 0);
                    mData.put("collection", amt);
                    mergeMap.put(m, mData);
                }
            }
        }

        List<Map<String, Object>> trendList = new java.util.ArrayList<>(mergeMap.values());
        data.put("monthlySalesAndCollections", trendList);

        
        String currentMonthName = now.getMonthValue() + "월";
        int thisMonthSales = 0;
        int thisMonthCollections = 0;
        if (mergeMap.containsKey(currentMonthName)) {
            thisMonthSales = (int) mergeMap.get(currentMonthName).get("sales");
            thisMonthCollections = (int) mergeMap.get(currentMonthName).get("collection");
        }
        data.put("thisMonthSales", thisMonthSales);
        data.put("thisMonthCollections", thisMonthCollections);

        
        int totalReceivables = waitingPayments; 
        data.put("totalReceivables", totalReceivables);

        
        List<Map<String, Object>> receivableTrendList = new java.util.ArrayList<>();
        int accumSales = 0;
        int accumCollections = 0;
        for (Map<String, Object> m : trendList) {
            accumSales += (int) m.get("sales");
            accumCollections += (int) m.get("collection");
            int diff = accumSales - accumCollections;
            if (diff < 0) diff = 0; 

            Map<String, Object> trendItem = new HashMap<>();
            trendItem.put("month", m.get("month"));
            trendItem.put("amount", diff);
            receivableTrendList.add(trendItem);
        }
        data.put("receivableTrend", receivableTrendList);

        
        List<Map<String, Object>> bizSummaryRaw = partnerOrderDAO.selectBusinessSalesSummary(sellerSeq);
        List<Map<String, Object>> businessSales = new java.util.ArrayList<>();
        if (bizSummaryRaw != null) {
            for (Map<String, Object> b : bizSummaryRaw) {
                String name = String.valueOf(b.get("companyName"));
                int oCount = b.get("orderCount") != null ? ((Number) b.get("orderCount")).intValue() : 0;
                int totalSales = b.get("totalSales") != null ? ((Number) b.get("totalSales")).intValue() : 0;
                int receivable = b.get("receivableAmount") != null ? ((Number) b.get("receivableAmount")).intValue() : 0;
                
                int collected = totalSales - receivable;
                int progress = 100;
                if (totalSales > 0) {
                    progress = (int) (((double) collected / totalSales) * 100);
                }
                
                String desc = String.format("이번 달 %d건, 미수금 %s원", oCount, String.format("%,d", receivable));
                String amountStr = String.format("%,d원", totalSales);
                
                String color = "bg-emerald-500";
                String trend = "↑ 정상";
                if (progress < 40) {
                    color = "bg-red-500";
                    trend = "연체 위험";
                } else if (progress < 80) {
                    color = "bg-orange-400";
                    trend = "입금 필요";
                }

                Map<String, Object> bItem = new HashMap<>();
                bItem.put("name", name);
                bItem.put("desc", desc);
                bItem.put("amount", amountStr);
                bItem.put("trend", trend);
                bItem.put("progress", progress);
                bItem.put("color", color);
                businessSales.add(bItem);
            }
        }
        data.put("businessSales", businessSales);

        
        List<Map<String, Object>> groupRaw = partnerOrderDAO.selectGroupOrders(sellerSeq);
        List<Map<String, Object>> groupOrders = new java.util.ArrayList<>();
        if (groupRaw != null) {
            for (Map<String, Object> g : groupRaw) {
                int id = g.get("id") != null ? ((Number) g.get("id")).intValue() : 0;
                String title = String.valueOf(g.get("title"));
                String status = String.valueOf(g.get("status"));
                int target = g.get("targetParticipants") != null ? ((Number) g.get("targetParticipants")).intValue() : 0;
                int current = g.get("currentParticipants") != null ? ((Number) g.get("currentParticipants")).intValue() : 0;
                
                int progress = 0;
                if (target > 0) {
                    progress = (int) (((double) current / target) * 100);
                }

                String statusKo = "모집 중";
                String btnText = "참여하기";
                String color = "bg-emerald-500";
                
                if ("PREPARING".equals(status)) {
                    statusKo = "발주 완료";
                    btnText = "상세 보기";
                    color = "bg-blue-500";
                } else if ("SHIPPING".equals(status)) {
                    statusKo = "배송 중";
                    btnText = "배송 조회";
                    color = "bg-violet-500";
                } else if ("DELIVERED".equals(status)) {
                    statusKo = "완료";
                    btnText = "이력 보기";
                    color = "bg-gray-500";
                } else if ("CANCELED".equals(status)) {
                    statusKo = "취소됨";
                    btnText = "확인";
                    color = "bg-red-500";
                }

                String dDayStr = "모집 중";
                if (g.get("endDate") != null) {
                    java.time.LocalDateTime endDate = (java.time.LocalDateTime) g.get("endDate");
                    long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), endDate.toLocalDate());
                    if (days < 0) {
                        dDayStr = "마감 완료";
                    } else if (days == 0) {
                        dDayStr = "마감 오늘";
                    } else {
                        dDayStr = "마감 D-" + days;
                    }
                }

                Map<String, Object> gItem = new HashMap<>();
                gItem.put("id", id);
                gItem.put("title", title);
                gItem.put("status", statusKo);
                gItem.put("progress", progress);
                gItem.put("color", color);
                gItem.put("dDay", dDayStr);
                gItem.put("btn", btnText);
                groupOrders.add(gItem);
            }
        }
        data.put("groupOrders", groupOrders);

        return data;
    }
}
