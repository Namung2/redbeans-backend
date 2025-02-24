package com.redbeans.controller;

import com.redbeans.dto.schedule.NotionEventDto;
import com.redbeans.service.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/notion-events") // 이 엔드포인트로 GET 날리기
@RequiredArgsConstructor

public class ScheduleController {
    //로그좀 잘보면서 살자
    private final NotionService notionService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @GetMapping
    public ResponseEntity<List<NotionEventDto>> getTodayEvents() { //Get 보낸 걸 처리 NotionEventDTo로 받고 일정 조회
        try {
            logger.info("Fetching today's events from Notion");
            List<NotionEventDto> events = notionService.getTodayEvents();
            logger.info("Successfully fetched {} events", events.size());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Error fetching events from Notion", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
