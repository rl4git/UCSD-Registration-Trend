package com.ucsdregistration.web_backend.DTO;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

import com.ucsdregistration.web_backend.Entity.Course;


// 此方法暂时用不到
// Course 相关信息由 CourseDTO返回
// 注册相关信息由 EnrollmentSnapshotDTO返回

@Data
public class CourseEnrollmentSnapshotDTO {
    private String courseOfferingId;
    private String department;
    private String courseId;
    private String instructor;
    private Integer year;
    private String quarter;
    private Integer total;
    private List<EnrollmentSnapshotDTO> enrollmentSnapshots;

    public CourseEnrollmentSnapshotDTO(Course course){
      this.courseOfferingId = course.getCourseOfferingId();
        this.department = course.getDepartment();
        this.courseId = course.getCourseId();
        this.instructor = course.getInstructor();
        this.year = course.getYear();
        this.quarter = course.getQuarter();
        this.total = course.getTotal();

        this.enrollmentSnapshots = course.getEnrollmentSnapshots().stream()
                                        .map(EnrollmentSnapshotDTO::new)
                                        .collect(Collectors.toList());
    }
}
