package com.redbeans.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String year;         // "신입생" or "고학년"
    private String title;
    private String description;
    private int members;
    private List<String> achievements;
    private String category;     // Used for icon type
    private String color;        // CSS color class

    // Validation method
    public void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (year == null || (!year.equals("신입생") && !year.equals("고학년"))) {
            throw new IllegalArgumentException("Year must be either '신입생' or '고학년'");
        }

        if (members <= 0) {
            throw new IllegalArgumentException("Number of members must be greater than zero");
        }

        if (achievements == null || achievements.isEmpty()) {
            throw new IllegalArgumentException("At least one achievement must be specified");
        }

        // Normalize category and set default color if not provided
        normalizeCategory();
    }

    private void normalizeCategory() {
        if (category == null || category.trim().isEmpty()) {
            // Default to "study" if no category
            this.category = "study";
        }

        if (color == null || color.trim().isEmpty()) {
            // Set default color based on category
            switch (this.category.toLowerCase()) {
                case "study":
                    this.color = "bg-green-100 text-green-800";
                    break;
                case "code":
                    this.color = "bg-blue-100 text-blue-800";
                    break;
                case "document":
                    this.color = "bg-purple-100 text-purple-800";
                    break;
                default:
                    this.color = "bg-yellow-100 text-yellow-800";
            }
        }
    }
}