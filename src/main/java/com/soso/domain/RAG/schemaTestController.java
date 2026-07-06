package com.soso.domain.RAG;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.RAG.services.schemaService;

@RestController
public class schemaTestController {

	private final schemaService schemaService;

    public schemaTestController(schemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping("/ai/schema-test")
    public String schemaTest() {
    	 System.out.println("schema-test 요청 들어옴");
        return schemaService.loadSchema();
    }
}
