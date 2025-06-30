package com.ucsdregistration.web_backend.DTO;

import lombok.Data;
import java.util.Set;
import java.util.stream.Collectors;

import com.ucsdregistration.web_backend.Entity.Course;

@Data
public class CourseDTO {
    private String courseOfferingId;
    private String department;
    private String courseId;
    private String instructor;
    private Integer year;
    private String quarter;
    private Integer total;
    private Set<ProfessorDTO> professors;

    public CourseDTO(Course course){
        this.courseOfferingId = course.getCourseOfferingId();
        this.department = course.getDepartment();
        this.courseId = course.getCourseId();
        this.instructor = course.getInstructor(); // 假设你保留了这个字段
        this.year = course.getYear();
        this.quarter = course.getQuarter();
        this.total = course.getTotal();

        if (course.getProfessors() != null) {
            this.professors = course.getProfessors().stream()
                                    .map(ProfessorDTO::new)
                                    .collect(Collectors.toSet());
        }
    }
}