package com.redbeans.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.redbeans.dto.schedule.NotionEventDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class NotionService {

    private final RestTemplate notionRestTemplate; //노션 api URL에 GET 요청 template
    private final String baseUrl;
    private final String databaseId;
    private final ObjectMapper objectMapper;
    private final ZoneId KOREA_TIMEZONE = ZoneId.of("Asia/Seoul");

    public NotionService(
            RestTemplate notionRestTemplate,
            @Value("${notion.api.base-url}") String baseUrl,
            @Value("${notion.database.id}") String databaseId,
            ObjectMapper objectMapper
    ) {
        this.notionRestTemplate = notionRestTemplate;
        this.baseUrl = baseUrl;
        this.databaseId = databaseId;
        this.objectMapper = objectMapper;
    }

    private ObjectNode createDateRangeFilter(LocalDateTime start, LocalDateTime end) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        ObjectNode filter = objectMapper.createObjectNode();
        ObjectNode dateFilter = objectMapper.createObjectNode();

        dateFilter.put("on_or_after", start.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateFilter.put("on_or_before", end.format(DateTimeFormatter.ISO_LOCAL_DATE));

        filter.put("property", "이벤트 시간");
        filter.set("date", dateFilter);

        requestBody.set("filter", filter);

        return requestBody;
    }

    public List<NotionEventDto> getTodayEvents() {
        String url = baseUrl + "/databases/" + databaseId + "/query"; // 노션 쿼리 URL

        // 오늘 날짜 구하기
        LocalDate today = LocalDate.now(KOREA_TIMEZONE);
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // 노션 API 필터 구조 생성 createDateRangeFilter 메서드
        ObjectNode requestBody = createDateRangeFilter(startOfDay, endOfDay);
        log.debug("Querying events with filter: {}", requestBody);

        try {//API 호출 POST 보내기
            ResponseEntity<JsonNode> response = notionRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody),
                    JsonNode.class
            );
            //log.debug("Response: {}", response); 더 깔끔하게 만들고 싶으면 log 찍어서 고쳐주세요..
            //response 파싱
            Set<NotionEventDto> events = new LinkedHashSet<>();
            if (response.getBody() != null && response.getBody().has("results")) {
                JsonNode results = response.getBody().get("results");
                for (JsonNode result : results) {
                    try{
                        NotionEventDto event = parseNotionPage(result);
                        // 유효한 이벤트만 리스트에 추가
                        if (isValidEvent(event)) {
                            events.add(event);
                        }
                    }catch (Exception e){
                        log.error("Error parsing Notion page:{}", e.getMessage());
                    }
                }
            }
            // Set을 List로 변환하여 반환
            return new ArrayList<>(events);
        } catch (Exception e) {
            log.error("Error fetching events from Notion:{}", e.getMessage());
            throw new RuntimeException("Failed to fetch event from Notion: " ,e);
        }
    }

    //유효 이벤트인지 판단
    private boolean isValidEvent(NotionEventDto event) {
        return event.getTitle() != null && !event.getTitle().isEmpty() &&
                event.getStartTime() != null;
    }

    private NotionEventDto parseNotionPage(JsonNode page) { //DTO로 파싱
        NotionEventDto event = new NotionEventDto();// DTO 객체
        // 내 노션 구조에 맞게
        JsonNode properties = page.get("properties");

        try{
            //이름(title) 파싱
            JsonNode titleNode = properties.get("이름").get("title");
            if(!titleNode.isEmpty()){
                event.setTitle(titleNode.get(0).get("text").get("content").asText());
            }

            //이벤트 시간 파싱
            JsonNode dateNode = properties.get("이벤트 시간").get("date");
            if(dateNode != null && dateNode.has("start")){
                String startDateTime = dateNode.get("start").asText();

                try {
                    // UTC 시간을 KST로 변환
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(startDateTime);
                    ZonedDateTime kstTime = zonedDateTime.withZoneSameInstant(KOREA_TIMEZONE);
                    // 날짜와 시간 분리하여 저장
                    event.setEventDate(kstTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    event.setStartTime(kstTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                    //종료 시간이 있는경우 파싱
                    if(dateNode.has("end")){
                        String endDateTime = dateNode.get("end").asText();
                        ZonedDateTime endZonedTime = ZonedDateTime.parse(endDateTime);
                        ZonedDateTime endKstTime = endZonedTime.withZoneSameInstant(KOREA_TIMEZONE);
                        event.setEndTime(endKstTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    }
                } catch (Exception e) {
                    log.error("Failed to parse datetime: {}", startDateTime);
                    throw e;
                }


            }

            //회의 유형 파싱
            JsonNode typeNode = properties.get("유형").get("select");
            if (typeNode != null && !typeNode.isNull()) {
                String category = typeNode.get("name").asText().toLowerCase();
                if (category.contains("회의") || category.contains("미팅")) {
                    event.setCategory("meeting");
                } else if (category.contains("스터디") || category.contains("학습")) {
                    event.setCategory("study");
                } else {
                    event.setCategory("other");
                }
            } else {
                event.setCategory("other");
            }
            
            //참석자 목록 people 추가
            StringBuilder location = new StringBuilder();
            JsonNode peopleNode = properties.get("참석자").get("people");
            if(peopleNode != null && peopleNode.isArray()){
                for(int i=0; i<peopleNode.size(); i++){
                    if(i>0){location.append(", ");}
                    location.append(peopleNode.get(i).get("name").asText());
                }
            }
            event.setPeoples(location.toString());
        }
        catch(Exception e){
            log.error("Failed to parse Notion page(파싱실패): {}", e.getMessage());
            throw e;
        }

        return event;
    }
}