package com.soso.domain.product.dao;

import com.soso.domain.product.dto.StockHistoryDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StockHistoryDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "com.soso.mappers.product.StockHistoryMapper.";

    
    public int insertStockHistory(StockHistoryDTO history) {
        return sqlSession.insert(NAMESPACE + "insertStockHistory", history);
    }

    
    public List<StockHistoryDTO> getTop5StockHistory(int userSeq, Integer storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("userSeq", userSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectList(NAMESPACE + "getTop5StockHistory", params);
    }

    
    public List<StockHistoryDTO> getStockHistoryWithPaging(int offset, int size, int userSeq, Integer storeSeq, Integer stockSeq, String transactionType, String startDate, String endDate, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);
        params.put("userSeq", userSeq);
        params.put("storeSeq", storeSeq);
        params.put("stockSeq", stockSeq);
        params.put("transactionType", transactionType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("keyword", keyword);
        return sqlSession.selectList(NAMESPACE + "getStockHistoryWithPaging", params);
    }

    
    public int getTotalHistoryCount(int userSeq, Integer storeSeq, Integer stockSeq, String transactionType, String startDate, String endDate, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("userSeq", userSeq);
        params.put("storeSeq", storeSeq);
        params.put("stockSeq", stockSeq);
        params.put("transactionType", transactionType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("keyword", keyword);
        return sqlSession.selectOne(NAMESPACE + "getTotalHistoryCount", params);
    }
    public int getTotalHistoryCount(int storeSeq) {
        return sqlSession.selectOne(NAMESPACE + "getTotalHistoryCount", storeSeq);
    }
}
