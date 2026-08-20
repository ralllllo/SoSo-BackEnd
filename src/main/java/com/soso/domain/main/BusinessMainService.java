package com.soso.domain.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessMainService {

    @Autowired
    private BusinessMainDAO businessMainDAO;

    
    public Map<String, Object> getDashboardData(int storeSeq, long userSeq) {
        Map<String, Object> data = new HashMap<>();

        
        int totalStocks = businessMainDAO.selectTotalStocksCount(storeSeq);
        int lackStocks = businessMainDAO.selectLackStocksCount(storeSeq);
        int expiringSoon = businessMainDAO.selectExpiringSoonCount(storeSeq);
        int activeGroupBuys = businessMainDAO.selectActiveGroupBuysCount();

        data.put("totalStocks", totalStocks);
        data.put("lackStocks", lackStocks);
        data.put("expiringSoon", expiringSoon);
        data.put("activeGroupBuys", activeGroupBuys);

        
        List<Map<String, Object>> stockStatus = businessMainDAO.selectCurrentStockStatus(storeSeq);
        data.put("stockStatus", stockStatus);

        
        List<Map<String, Object>> salesRaw = businessMainDAO.selectMonthlySales(userSeq);
        
        
        Map<String, Integer> mergeSalesMap = new java.util.LinkedHashMap<>();
        java.time.LocalDate now = java.time.LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate targetDate = now.minusMonths(i);
            String monthName = targetDate.getMonthValue() + "월";
            mergeSalesMap.put(monthName, 0);
        }

        if (salesRaw != null) {
            for (Map<String, Object> s : salesRaw) {
                String m = String.valueOf(s.get("month"));
                int amt = s.get("amount") != null ? ((Number) s.get("amount")).intValue() : 0;
                if (mergeSalesMap.containsKey(m)) {
                    mergeSalesMap.put(m, amt);
                }
            }
        }

        List<Map<String, Object>> salesTrendList = new java.util.ArrayList<>();
        for (Map.Entry<String, Integer> entry : mergeSalesMap.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("month", entry.getKey());
            m.put("amount", entry.getValue());
            salesTrendList.add(m);
        }
        data.put("salesTrend", salesTrendList);

        
        Map<String, Object> groupParams = new HashMap<>();
        groupParams.put("userSeq", userSeq);
        List<Map<String, Object>> groupRaw = businessMainDAO.selectGroupOrders(groupParams);
        List<Map<String, Object>> groupOrders = new java.util.ArrayList<>();
        
        if (groupRaw != null) {
            for (Map<String, Object> g : groupRaw) {
                int id = g.get("id") != null ? ((Number) g.get("id")).intValue() : 0;
                String title = String.valueOf(g.get("title"));
                String status = String.valueOf(g.get("status"));
                int target = g.get("targetParticipants") != null ? ((Number) g.get("targetParticipants")).intValue() : 0;
                int current = g.get("currentParticipants") != null ? ((Number) g.get("currentParticipants")).intValue() : 0;
                boolean isJoined = g.get("isJoined") != null && ((Number) g.get("isJoined")).intValue() > 0;

                int progress = 0;
                if (target > 0) {
                    progress = (int) (((double) current / target) * 100);
                }

                String statusKo = "모집 중";
                String btnText = isJoined ? "참여 완료 (상세 보기)" : "참여하기";
                String color = "bg-emerald-500";

                if (progress >= 100) {
                    statusKo = "마감 완료";
                    color = "bg-red-500";
                    btnText = "마감됨";
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
                gItem.put("currentParticipants", current);
                gItem.put("targetParticipants", target);
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
