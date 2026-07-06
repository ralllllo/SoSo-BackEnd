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
	
	// 지출 비용 등록
	public int insertCategory(ExpensesDTO dto) {

    // 1. 먼저 MySQL expenses 테이블에 지출 등록
    int result = dao.insertCategory(dto);

    // 2. DB 등록이 성공했을 때만 RAG upsert 실행
    if (result > 0) {

        // 3. useGeneratedKeys로 들어온 expenseSeq 확인

        // 4. Qdrant에 지출 문서 저장 또는 갱신
        upsertExpenseRag(dto);
    }

    // 5. 기존처럼 등록 결과 반환
    return result;
	}
//	public int insertCategory(ExpensesDTO dto) {
//		return dao.insertCategory(dto);
//	}
	
	// 월별 지출 비용 출력
	public Long monthlyTotal(Integer storeSeq, String month) {
		return dao.monthlyTotal(storeSeq, month);
	}
	
	// 월별 카테고리별 지출 출력
	public List<Map<String, Object>> categoryCost(Integer storeSeq, String month) {
		return dao.categoryCost(storeSeq, month);
	}
	
	// 카테고리별 월 지출 세부내역 출력
	public List<ExpensesDTO> expenseDetails(int storeSeq, String month, int categorySeq) {
	    return dao.expenseDetails(storeSeq, month, categorySeq);
	}
	
	// 비용 카테고리 - 식자재비 - 일반 발주 목록 조회
	public List<Map<String, Object>> generalOrdersForExpense(int storeSeq, int partnerStoreSeq) {
	    return dao.generalOrdersForExpense(storeSeq, partnerStoreSeq);
	}
	
	// 지출 데이터를 RAG 문서로 만들어 Qdrant에 저장 또는 갱신하는 메소드
	private void upsertExpenseRag(ExpensesDTO dto) {

	    // dto가 없으면 RAG 처리하지 않음
	    if (dto == null) {
	        return;
	    }

	    // Qdrant payload에 같이 저장할 부가 정보
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

	    // 챗봇이 검색해서 읽을 실제 문장
	 // 챗봇이 검색해서 읽을 실제 지출 문장
	 // 답변에서 세부정보가 빠지지 않도록 필드명을 명확하게 넣는다.
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
	
	// 지출 내역 메모 수정
		public int updateExpenseMemo(Long storeSeq, Long expenseSeq, String memo) {
		    if (storeSeq == null || storeSeq == 0) {
		        throw new RuntimeException("사업장 정보가 없습니다.");
		    }

		    if (expenseSeq == null || expenseSeq == 0) {
		        throw new RuntimeException("지출 번호가 없습니다.");
		    }

		    return dao.updateExpenseMemo(storeSeq, expenseSeq, memo);
		}

		
		// 지출 내역 삭제
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
