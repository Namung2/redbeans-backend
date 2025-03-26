package com.redbeans.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Long id;
    private String year;         // "신입생" or "고학년"
    private String title;
    private String description;
    private int members;
    private List<String> achievements;
    private String category;     // Can be used for icon type: "study", "code", "document"
    private String color;        // For color styling
}