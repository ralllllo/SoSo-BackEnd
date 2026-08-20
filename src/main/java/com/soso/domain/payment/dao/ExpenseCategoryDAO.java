package com.soso.domain.payment.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.payment.dto.ExpensesDTO;

@Repository
public class ExpenseCategoryDAO {
	
	@Autowired
	private SqlSessionTemplate mybatis;
	
	
	public int insertCategory(ExpensesDTO dto) {
		return mybatis.insert("expense.insert", dto);
	}
	
	
	public Long monthlyTotal(Integer storeSeq, String month) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("storeSeq", storeSeq);
		params.put("month", month);
		
		return mybatis.selectOne("expense.monthlyTotal", params);
	}
	
	
	public List<Map<String, Object>> categoryCost(Integer storeSeq, String month) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("storeSeq", storeSeq);
		params.put("month", month);
		
		return mybatis.selectList("expense.monthlyTotalByCategory", params);
	}
	
	
	public List<ExpensesDTO> expenseDetails(int storeSeq, String month, int categorySeq) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);
	    params.put("month", month);
	    params.put("categorySeq", categorySeq);

	    return mybatis.selectList("expense.expenseDetails", params);
	}
	
	
	public List<Map<String, Object>> generalOrdersForExpense(int storeSeq, int partnerStoreSeq) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("storeSeq", storeSeq);
	    param.put("partnerStoreSeq", partnerStoreSeq);

	    return mybatis.selectList("expense.generalOrdersForExpense", param);
	}
	
	
		public int updateExpenseMemo(Long storeSeq, Long expenseSeq, String memo) {
		    Map<String, Object> params = new HashMap<>();
		    params.put("storeSeq", storeSeq);
		    params.put("expenseSeq", expenseSeq);
		    params.put("memo", memo);

		    return mybatis.update("expense.updateExpenseMemo", params);
		}

		
		public int deleteExpense(Long storeSeq, Long expenseSeq) {
		    Map<String, Object> params = new HashMap<>();
		    params.put("storeSeq", storeSeq);
		    params.put("expenseSeq", expenseSeq);

		    return mybatis.delete("expense.deleteExpense", params);
		}

}
