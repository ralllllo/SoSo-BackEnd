package com.soso.domain.product.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soso.domain.product.dao.StockHistoryDAO;
import com.soso.domain.product.dto.StockHistoryDTO;

@Service
public class StockHistoryService {

    @Autowired
    private StockHistoryDAO stockHistoryDAO;

    
    public List<StockHistoryDTO> getDashboardHistory(int userSeq, Integer storeSeq) {
        return stockHistoryDAO.getTop5StockHistory(userSeq, storeSeq);
    }

    
    public Map<String, Object> getModalHistory(int page, int size, int userSeq, Integer storeSeq, Integer stockSeq, String transactionType, String startDate, String endDate, String keyword) {
        int offset = (page - 1) * size;

        List<StockHistoryDTO> historyList = stockHistoryDAO.getStockHistoryWithPaging(offset, size, userSeq, storeSeq, stockSeq, transactionType, startDate, endDate, keyword);
        int totalCount = stockHistoryDAO.getTotalHistoryCount(userSeq, storeSeq, stockSeq, transactionType, startDate, endDate, keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("historyList", historyList);
        response.put("totalCount", totalCount);
        response.put("currentPage", page);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));
        
        return response;
    }

    
    @Transactional
    public void recordHistory(StockHistoryDTO history) {
        stockHistoryDAO.insertStockHistory(history);
    }
}
