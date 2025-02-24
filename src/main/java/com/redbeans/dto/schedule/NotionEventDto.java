package com.redbeans.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotionEventDto {

        //선언
        private String title;
        private String startTime;
        private String endTime;
        private String category;
        private String eventDate;
        private String peoples;

        // 전체 일정 시간을 반환하는 메서드 (프론트엔드 정렬에 활용)
        public String getFullStartDateTime() {
                if (eventDate == null || startTime == null) {
                        return null;
                }
                return eventDate + "T" + startTime;
        }
        // 오늘 일정인
        // 지 확인하는 메서드
        public boolean isToday() {
                if (eventDate == null) {
                        return false;
                }
                LocalDate today = LocalDate.now();
                LocalDate eventLocalDate = LocalDate.parse(eventDate);
                return eventLocalDate.equals(today);
        }

        // 프론트에 맞게 정규화
        public void normalizeCategory() {
                if (this.category != null) {
                        switch (this.category.toLowerCase()) {
                                case "스터디":
                                case "study":
                                        this.category = "study";
                                        break;
                                case "브레인스토밍":
                                        this.category = "브레인스토밍";
                                        break;
                                case "팀 주간 회의":
                                case "레드빈즈 정기회의":
                                case "미팅":
                                case "회의":
                                case "meeting":
                                        this.category = "meeting";
                                        break;
                                default:
                                        this.category = "other";
                        }
                }
        }
        //시간 형식 검증 및 정규화
        public void normalizeTime() {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm");

                try{
                        if(startTime != null){
                                //시간 정규화 ~~
                                LocalDateTime startTimeLDT = LocalDateTime.parse(startTime, inputFormatter);
                                this.startTime = startTimeLDT.format(inputFormatter);}
                        if(endTime != null){
                                LocalDateTime endTimeLDT = LocalDateTime.parse(endTime, inputFormatter);
                                this.endTime = endTimeLDT.format(inputFormatter);}
                }
                catch(DateTimeParseException e){
                        throw new IllegalArgumentException("Invalid time format 시간 포맷 달라요~: HH:mm");
                }
        }

        // 데이터 유효성을 검증하는 메서드
        public void validate() {
                if (title == null || title.trim().isEmpty()) {
                        throw new IllegalArgumentException("Title cannot be empty");
                }

                if (startTime == null || startTime.trim().isEmpty()) {
                        throw new IllegalArgumentException("Start time cannot be empty");
                }

                if (category == null || category.trim().isEmpty()) {
                        this.category = "other";
                }

                // 시간 형식 정규화 및 검증
                normalizeTime();
                // 카테고리 정규화
                normalizeCategory();
        }

        // 참석자 목록을 콤마로 구분된 문자열로 변환하는 메서드
        public void setParticipants(String[] participants) {
                if (participants != null && participants.length > 0) {
                        this.peoples = String.join(", ", participants);
                }
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                NotionEventDto that = (NotionEventDto) o;
                return Objects.equals(title, that.title) &&
                        Objects.equals(startTime, that.startTime) &&
                        Objects.equals(endTime, that.endTime) &&
                        Objects.equals(category, that.category) &&
                        Objects.equals(peoples, that.peoples);
        }

        @Override
        public int hashCode() {
                return Objects.hash(title, startTime, endTime, category, peoples);
        }
        
}
