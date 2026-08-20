package com.soso.domain.product.dao;

import com.soso.domain.product.dto.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StockDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "com.soso.mappers.product.StockMapper.";

    public List<StockDTO> selectStockList(Map<String, Object> filters) {
        return sqlSession.selectList(NAMESPACE + "selectStockList", filters);
    }

    public void insertStock(int storeSeq, StockDTO stock) {
    	
    	stock.setStoreSeq(storeSeq); 
    	
        sqlSession.insert(NAMESPACE + "insertStock", stock);
    }

    public int checkDuplicateStockName(int storeSeq, String stockName) {
        Map<String, Object> params = new HashMap<>();
        params.put("storeSeq", storeSeq);
        params.put("stockName", stockName);
        return sqlSession.selectOne(NAMESPACE + "checkDuplicateStockName", params);
    }

    public StockDTO selectStockBySeq(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectOne(NAMESPACE + "selectStockBySeq", params);
    }

    public void updateCurrentStock(Map<String, Object> params) {
        sqlSession.update(NAMESPACE + "updateCurrentStock", params);
    }

    public void insertBatch(StockBatchDTO batch) {
        sqlSession.insert(NAMESPACE + "insertBatch", batch);
    }

    public List<StockBatchDTO> selectAvailableBatches(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectList(NAMESPACE + "selectAvailableBatches", params);
    }

    public void updateBatchQuantity(StockBatchDTO batch) {
        sqlSession.update(NAMESPACE + "updateBatchQuantity", batch);
    }

    public void insertHistory(StockHistoryDTO history) {
        sqlSession.insert(NAMESPACE + "insertHistory", history);
    }

    public List<StockBatchDTO> selectBatchesByStockSeq(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectList(NAMESPACE + "selectBatchesByStockSeq", params);
    }

    public List<StockHistoryDTO> selectHistoriesByStockSeq(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectList(NAMESPACE + "selectHistoriesByStockSeq", params);
    }

    public void updateStock(StockDTO stock) {
        sqlSession.update(NAMESPACE + "updateStock", stock);
    }

    public void deleteStock(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        sqlSession.delete(NAMESPACE + "deleteStock", params);
    }

    public void deleteBatchesByStockSeq(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        sqlSession.delete(NAMESPACE + "deleteBatchesByStockSeq", params);
    }

    public void deleteHistoryByStockSeq(int stockSeq, int storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("stockSeq", stockSeq);
        params.put("storeSeq", storeSeq);
        sqlSession.delete(NAMESPACE + "deleteHistoryByStockSeq", params);
    }
    public int getcountExpiringSoon(Map<String, Object> filters) {
        return sqlSession.selectOne(NAMESPACE + "selectgetcountExpiringSoon", filters);
    }
    public int selectgetcountExpiringSoon(int storeSeq) {
        return sqlSession.selectOne(NAMESPACE + "selectgetcountExpiringSoon", storeSeq);
    }

    public List<StockBatchDTO> selectExpiringBatches() {
        return sqlSession.selectList(NAMESPACE + "selectExpiringBatches");
    }
}
