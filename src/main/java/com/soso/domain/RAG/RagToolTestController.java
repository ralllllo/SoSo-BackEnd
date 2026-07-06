package com.soso.domain.RAG;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.ZoneId;

import com.soso.domain.RAG.services.schemaService;
import com.soso.domain.RAG.tools.DBTool;

@RestController
public class RagToolTestController {

    private final ChatClient gemini;
    private final schemaService schemaService;
    private final DBTool dbTool;

    public RagToolTestController(
            ChatClient.Builder builder,
            schemaService schemaService,
            DBTool dbTool
    ) {
        this.gemini = builder.build();
        this.schemaService = schemaService;
        this.dbTool = dbTool;
    }

    @GetMapping("/ai/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestParam String message,
            @RequestParam int storeSeq
    ) {

        String schema = schemaService.loadSchema();
        
        System.out.println("챗봇 질문 message = " + message);
        System.out.println("챗봇 조회 storeSeq = " + storeSeq);

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String currentYear = String.valueOf(today.getYear());
        String currentMonth = String.format("%04d-%02d", today.getYear(), today.getMonthValue());

        String systemPrompt = """
                너는 SoSo 서비스의 MySQL VIEW 조회 챗봇이다.

                반드시 아래 규칙을 지켜라.

                1. SQL은 반드시 SELECT 문만 생성한다.
                2. INSERT, UPDATE, DELETE, DROP, CREATE, ALTER, TRUNCATE 문은 절대 생성하지 않는다.
                3. 실제 원본 테이블은 직접 조회하지 않는다.
                4. schema.md에 정의된 view_ 로 시작하는 VIEW만 조회한다.
                5. 사용자의 매장 데이터는 반드시 store_seq 조건으로 필터링한다.
                6. 현재 선택된 매장 번호는 %d 이다.
                7. SQL을 생성할 때 반드시 WHERE store_seq = %d 조건을 포함한다.
                8. 현재 날짜는 %s 이다.
                9. 사용자가 연도 없이 "6월", "7월"처럼 월만 말하면 현재 연도 기준으로 해석한다.
                   예: 현재 연도가 %s이면 "6월 지출"은 '%s-06'으로 조회한다.
                10. 사용자가 "이번 달"이라고 말하면 현재 월인 '%s' 기준으로 조회한다.
                11. 사용자가 "오늘"이라고 말하면 현재 날짜인 '%s' 기준으로 조회한다.
                12. 카드 관련 조회에서 billing_key는 절대 조회하지 않는다.
                13. 필요한 경우 DBTool을 호출해 SELECT 결과를 확인한 뒤 사용자에게 자연어로 답변한다.
                14. DBTool 호출은 최대 3회까지만 한다.
                15. 답변은 한국어로 쉽고 간단하게 정리한다.
                16. 사용자가 "이번 달 지출", "6월 지출", "6월 동안 지출"처럼 월별 지출을 물어보면
				    전체 목록을 먼저 조회하지 말고 SUM(amount), COUNT(*)를 사용해 총 지출 금액과 건수를 먼저 조회한다.
				17. 월별 지출 답변에는 총 지출 금액, 지출 건수, 주요 지출 항목을 간단히 정리한다.
				18. 지출 목록이 많으면 전체를 다 보여주지 말고 최근 5건만 요약한다.
				19. SQL 끝에는 세미콜론(;)을 붙이지 않는다.
				20. 사용자가 "부족한 재고"를 물어보면 현재 수량이 안전 재고보다 낮은 항목을 우선 조회한다.
        		21. 답변에는 SQL이나 VIEW 이름을 직접 보여주지 말고, 사용자가 이해하기 쉬운 문장으로 정리한다.
        		22. 목록 조회 SQL은 LIMIT 10 이하로 생성한다.
				23. 월별 지출처럼 집계가 가능한 질문은 목록 전체를 조회하지 말고 SUM, COUNT를 먼저 사용한다.
				24. 조회 결과가 많으면 전체 목록을 나열하지 말고 요약해서 답변한다.
				25. SUM(amount) 결과가 null이면 0원으로 해석해서 답변한다.
        		26. COUNT 결과가 0이면 "조회된 지출 내역이 없습니다"라고 답변한다.
        		27. 합계 조회 시 SUM(amount)는 COALESCE(SUM(amount), 0) 형태로 작성한다.
        		28. 발주 관련 VIEW인 view_order_summary, view_order_detail은 매장 기준 필터 컬럼이 store_seq가 아니라 buyer_seq이면 buyer_seq = 현재 선택 매장 번호 조건을 사용한다.
        		29. 그 외 store_seq 컬럼이 있는 VIEW는 반드시 store_seq = 현재 선택 매장 번호 조건을 사용한다.
        		30. 조회 결과가 없으면 오류처럼 말하지 말고, "조회된 내역이 없습니다"라고 자연스럽게 답변한다.

                ### schema.md
                %s
                """.formatted(
                        storeSeq,
                        storeSeq,
                        today,
                        currentYear,
                        currentYear,
                        currentMonth,
                        today,
                        schema
                );

        try {
            ChatResponse resp = gemini.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .tools(dbTool)
                    .call()
                    .chatResponse();

            String answer = resp.getResult().getOutput().getText();

            return ResponseEntity.ok(Map.of("answer", answer));

        } catch (Exception e) {
            e.printStackTrace();

            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            return ResponseEntity.status(500).body(
                    Map.of("answer", "Gemini 호출 중 오류가 발생했습니다: " + root.getMessage())
            );
        }
    }
}