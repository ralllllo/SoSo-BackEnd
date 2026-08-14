package com.soso.domain.payment.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soso.domain.payment.dao.ExpenseCategoryDAO;
import com.soso.domain.payment.dto.ExpensesDTO;

import java.util.HashMap;

@Service
public class ExpenseCategoryService {
	
	@Autowired
	private ExpenseCategoryDAO dao;
	
	
	public int insertCategory(ExpensesDTO dto) {

    
    int result = dao.insertCategory(dto);

    
    if (result > 0) {

        

        
        upsertExpenseRag(dto);
    }

    
    return result;
	}



	
	
	public Long monthlyTotal(Integer storeSeq, String month) {
		return dao.monthlyTotal(storeSeq, month);
	}
	
	
	public List<Map<String, Object>> categoryCost(Integer storeSeq, String month) {
		return dao.categoryCost(storeSeq, month);
	}
	
	
	public List<ExpensesDTO> expenseDetails(int storeSeq, String month, int categorySeq) {
	    return dao.expenseDetails(storeSeq, month, categorySeq);
	}
	
	
	public List<Map<String, Object>> generalOrdersForExpense(int storeSeq, int partnerStoreSeq) {
	    return dao.generalOrdersForExpense(storeSeq, partnerStoreSeq);
	}
	
	
	private void upsertExpenseRag(ExpensesDTO dto) {

	    
	    if (dto == null) {
	        return;
	    }

	    
	    Map<String, Object> metadata = new HashMap<>();
	    metadata.put("expenseSeq", dto.getExpenseSeq());
	    metadata.put("storeSeq", dto.getStoreSeq());
	    metadata.put("categorySeq", dto.getCategorySeq());
	    metadata.put("expenseDate", dto.getExpenseDate());
	    metadata.put("title", dto.getTitle());
	    metadata.put("amount", dto.getAmount());
	    metadata.put("paymentMethod", dto.getPaymentMethod());
	    metadata.put("supplierName", dto.getSupplierName());
	    metadata.put("refType", dto.getRefType());
	    metadata.put("refSeq", dto.getRefSeq());

	    
	 
	 
	 String text = String.format(
	         "[지출 상세 정보] " +
	         "지출번호: %s, " +
	         "매장번호: %s, " +
	         "카테고리번호: %s, " +
	         "지출일자: %s, " +
	         "지출제목: %s, " +
	         "지출금액: %s원, " +
	         "결제수단: %s, " +
	         "거래처명: %s, " +
	         "메모: %s, " +
	         "참조유형: %s, " +
	         "참조번호: %s. " +
	         "사용자가 지출 내역을 물어보면 지출일자, 제목, 금액, 결제수단, 거래처명, 메모를 모두 포함해서 답변해야 합니다.",
	         dto.getExpenseSeq(),
	         dto.getStoreSeq(),
	         dto.getCategorySeq(),
	         dto.getExpenseDate(),
	         dto.getTitle(),
	         dto.getAmount(),
	         dto.getPaymentMethod(),
	         dto.getSupplierName(),
	         dto.getMemo(),
	         dto.getRefType(),
	         dto.getRefSeq()
	 );
	}
	
	
		public int updateExpenseMemo(Long storeSeq, Long expenseSeq, String memo) {
		    if (storeSeq == null || storeSeq == 0) {
		        throw new RuntimeException("사업장 정보가 없습니다.");
		    }

		    if (expenseSeq == null || expenseSeq == 0) {
		        throw new RuntimeException("지출 번호가 없습니다.");
		    }

		    return dao.updateExpenseMemo(storeSeq, expenseSeq, memo);
		}

		
		
		public int deleteExpense(Long storeSeq, Long expenseSeq) {
		    if (storeSeq == null || storeSeq == 0) {
		        throw new RuntimeException("사업장 정보가 없습니다.");
		    }

		    if (expenseSeq == null || expenseSeq == 0) {
		        throw new RuntimeException("지출 번호가 없습니다.");
		    }

		    return dao.deleteExpense(storeSeq, expenseSeq);
		}

}
