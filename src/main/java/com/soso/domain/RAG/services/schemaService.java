package com.soso.domain.RAG.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class schemaService {
	
	 public String loadSchema() {
	        try {
	            ClassPathResource resource = new ClassPathResource("chatbot/schema.md");

	            return StreamUtils.copyToString(
	                resource.getInputStream(),
	                StandardCharsets.UTF_8
	            );

	        } catch (IOException e) {
	            throw new RuntimeException("schema.md 파일을 읽는 중 오류가 발생했습니다.", e);
	        }
	    }

}
