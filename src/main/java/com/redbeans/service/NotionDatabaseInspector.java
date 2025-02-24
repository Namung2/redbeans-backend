package com.redbeans.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@Slf4j
@Service

//노션 데이터 포맷 확인하는 코드입니다
public class NotionDatabaseInspector {
    private final RestTemplate notionRestTemplate;
    private final String baseUrl;
    private final String databaseId;

    public NotionDatabaseInspector(
            RestTemplate notionRestTemplate,
            @Value("${notion.api.base-url}") String baseUrl,
            @Value("${notion.database.id}") String databaseId
    ) {
        this.notionRestTemplate = notionRestTemplate;
        this.baseUrl = baseUrl;
        this.databaseId = databaseId;
    }

    public void inspectDatabaseStructure() {
        // 데이터베이스 구조를 조회하는 API 엔드포인트
        String url = baseUrl + "/databases/" + databaseId;

        try {
            ResponseEntity<JsonNode> response = notionRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    JsonNode.class
            );

            if (response.getBody() != null) {
                JsonNode database = response.getBody();
                JsonNode properties = database.get("properties");

                log.info("=== Notion Database Structure ===");
                properties.fields().forEachRemaining(field -> {
                    String propertyName = field.getKey();
                    String propertyType = field.getValue().get("type").asText();

                    log.info("Field Name: {}", propertyName);
                    log.info("Field Type: {}", propertyType);
                    log.info("------------------------");
                });
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP error occurred while accessing Notion API: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to access Notion API: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Network error occurred while accessing Notion API", e);
            throw new RuntimeException("Network error occurred: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while inspecting database", e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
