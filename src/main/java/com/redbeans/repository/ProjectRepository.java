package com.redbeans.repository;

import com.redbeans.model.Project;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton repository for managing projects
 * This implementation uses in-memory storage.
 * In a production environment, you would typically use a database.
 */
@Repository
public class ProjectRepository {
    // Singleton instance
    private static ProjectRepository instance;

    // Thread-safe collections and ID generator
    private final Map<Long, Project> projects = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Private constructor to prevent direct instantiation
    private ProjectRepository() {
        // Initialize with some sample data
        initializeSampleData();
    }

    // Static method to get the singleton instance
    public static synchronized ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    // Create a new project
    public Project save(Project project) {
        // Set ID if not already set
        if (project.getId() == null) {
            project.setId(idCounter.getAndIncrement());
        }

        // Save project to repository
        projects.put(project.getId(), project);
        return project;
    }

    // Get all projects
    public List<Project> findAll() {
        return new ArrayList<>(projects.values());
    }

    // Get project by ID
    public Project findById(Long id) {
        return projects.get(id);
    }

    // Delete project by ID
    public void deleteById(Long id) {
        projects.remove(id);
    }

    // Update a project
    public Project update(Project project) {
        if (project.getId() == null || !projects.containsKey(project.getId())) {
            throw new IllegalArgumentException("Cannot update non-existent project");
        }

        projects.put(project.getId(), project);
        return project;
    }

    // Initialize with sample data
    private void initializeSampleData() {
        List<String> achievements1 = new ArrayList<>();
        achievements1.add("solved.ac Class 3 달성");
        achievements1.add("주 2회 스터디 진행");
        achievements1.add("알고리즘 기초 완성");

        List<String> achievements2 = new ArrayList<>();
        achievements2.add("React/Spring 기반 풀스택 개발");
        achievements2.add("깃허브 협업 경험");
        achievements2.add("포트폴리오 작품 제작");

        List<String> achievements3 = new ArrayList<>();
        achievements3.add("주요 AI 논문 리뷰");
        achievements3.add("모델 구현 실습");
        achievements3.add("연구 방법론 학습");

        save(Project.builder()
                .year("신입생")
                .title("백준 알고리즘 스터디")
                .description("코딩 테스트 대비를 위한 백준 알고리즘 문제 풀이와 스터디 활동")
                .members(12)
                .achievements(achievements1)
                .category("study")
                .color("bg-green-100 text-green-800")
                .build());

        save(Project.builder()
                .year("고학년")
                .title("웹서비스 프로젝트")
                .description("실제 사용 가능한 웹 서비스를 기획부터 배포까지 경험하는 팀 프로젝트")
                .members(8)
                .achievements(achievements2)
                .category("code")
                .color("bg-blue-100 text-blue-800")
                .build());

        save(Project.builder()
                .year("고학년")
                .title("AI 논문 스터디")
                .description("최신 AI 기술 트렌드를 파악하고 논문을 함께 리뷰하는 심화 스터디")
                .members(5)
                .achievements(achievements3)
                .category("document")
                .color("bg-purple-100 text-purple-800")
                .build());
    }
}