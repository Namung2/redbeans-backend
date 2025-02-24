package com.redbeans.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;

@Slf4j
@Configuration
public class NotionConfig {

    @Value("${notion.api.key}")
    private String notionApiKey;

    @Value("${notion.api.version}")
    private String notionVersion;

    @Bean //스프링 Bean에 주입
    public RestTemplate notionRestTemplate() {
        return new RestTemplateBuilder()
                .additionalInterceptors((request, body, execution) -> {
                    // API 요청 전에 로그 기록
                    log.debug("Making request to Notion API: {}", request.getURI());

                    // Notion API 필수 헤더 설정
                    request.getHeaders().set("Authorization", "Bearer " + notionApiKey);
                    request.getHeaders().set("Notion-Version", notionVersion);
                    request.getHeaders().set("Content-Type", "application/json");

                    // 설정된 헤더 로깅 (API 키는 제외)
                    log.debug("Request headers: Notion-Version={}, Content-Type={}",
                            request.getHeaders().get("Notion-Version"),
                            request.getHeaders().get("Content-Type"));

                    // 요청 실행 및 응답 받기
                    ClientHttpResponse response = execution.execute(request, body);

                    // 응답 상태 코드 로깅
                    log.debug("Response status code: {}", response.getStatusCode());

                    return response;
                })
                .build();
    }
}