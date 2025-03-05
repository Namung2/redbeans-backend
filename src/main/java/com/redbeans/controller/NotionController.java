package com.redbeans.controller;
import com.redbeans.dto.common.ApiResponse;
import com.redbeans.dto.schedule.NotionEventDto;
import com.redbeans.service.NotionDatabaseInspector;
import com.redbeans.service.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notion")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://warm-blini-39cdcb.netlify.app")  // React 프론트엔드를 위한 CORS 설정 https://warm-blini-39cdcb.netlify.app
public class NotionController {

/*노션 api에서 데이터 포맷 확인해주는 엔드포인트 안지울테이까 확인 한번씩 해주시고
마려운 부분들은 자신감 있게 고쳐주세요 저도 대충 테스트 했습니다.
postman이나 브라우저로 GET 날리면 됩니다.
http://localhost:8080/api/notion/inspect

    private final NotionDatabaseInspector notionDatabaseInspector;
    @GetMapping("/inspect") //노션 api 데이터 포맷 확인해주는 엔드포인트
    public ResponseEntity<String> inspectDatabase() {
        try {
            notionDatabaseInspector.inspectDatabaseStructure();
            return ResponseEntity.ok("Database structure inspection completed. Check the logs for details.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error inspecting database: " + e.getMessage());
        }
    }

*/

    private final NotionService notionService; // 진짜 api 서비스 오늘 회의 있는지 확인
    @GetMapping("/events/today")
    public ResponseEntity<ApiResponse<List<NotionEventDto>>> getTodayEvents() {
        try{
            List<NotionEventDto> events = notionService.getTodayEvents();

            //각 이벤트에 대해 유효성 검증
            events.forEach(event -> {
                try{
                    event.validate();
                }catch( IllegalArgumentException e){
                    //log.warn("Event validation failed: " + event.getTitle(), e);
                }
            });
            return ResponseEntity.ok(ApiResponse.success(events));
        }catch ( Exception e){
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch today's events:"+ e.getMessage()));
        }
    }



}
