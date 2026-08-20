package com.soso.domain.RAG.tools;

import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBTool {

    @Autowired
    private JdbcTemplate jdbc;

    @Tool(description = """
            SoSo 챗봇용 MySQL VIEW에 SELECT 쿼리를 실행합니다.
            반드시 SELECT 문만 실행해야 합니다.
            실제 원본 테이블은 조회하지 않고 schema.md에 정의된 view_ 로 시작하는 VIEW만 조회해야 합니다.
            """)
    public String executeDBQuery(String sql) {

        System.out.println("AI 생성 SQL : " + sql);

        sql = sql.trim();

        
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }

        String checkSql = sql.toLowerCase();

        
        if (!checkSql.startsWith("select")) {
            return "오류: SELECT 문만 실행할 수 있습니다.";
        }

        
        if (checkSql.contains(";")) {
            return "오류: 세미콜론을 포함한 다중 SQL은 실행할 수 없습니다.";
        }

        
        if (checkSql.contains("--") || checkSql.contains("/*") || checkSql.contains("*/")) {
            return "오류: SQL 주석은 사용할 수 없습니다.";
        }

        
        
        if (checkSql.matches(".*\\b(insert|update|delete|drop|create|alter|truncate)\\b.*")) {
            return "오류: 데이터 변경 SQL은 실행할 수 없습니다.";
        }

        
        if (!checkSql.contains("view_")) {
            return "오류: 챗봇용 VIEW만 조회할 수 있습니다.";
        }

        
        if (checkSql.contains("billing_key")) {
            return "오류: billing_key는 조회할 수 없습니다.";
        }

        
        if (!checkSql.contains("limit")) {
            sql = sql + " LIMIT 30";
        }

        try {
            List<Map<String, Object>> result = jdbc.queryForList(sql);

            System.out.println("쿼리 실행 결과 : " + result);

            if (result == null || result.isEmpty()) {
                return "조회된 데이터가 없습니다.";
            }

            String resultText = result.toString();

            if (resultText.length() > 4000) {
                resultText = resultText.substring(0, 4000)
                        + "\n... 조회 결과가 많아 일부만 전달했습니다. 필요한 내용만 요약해서 답변하세요.";
            }

            return resultText;

        } catch (Exception e) {
            e.printStackTrace();

            return "Query문 오류 발생 : " + e.getMessage()
                    + "\n위 오류를 참고해서 SQL문을 수정한 후 다시 쿼리를 실행하세요.";
        }
    }
}