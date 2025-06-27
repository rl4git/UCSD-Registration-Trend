package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @Column(name = "course_offering_id")
    private String courseOfferingId;

    @Column(name = "department")
    private String department;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "instructor")
    private String instructor;

    @Column(name = "year")
    private Integer year;

    @Column(name = "quarter")
    private String quarter;

    @Column(name = "total")
    private Integer total;

    @Override
    public String toString() {
        return "Course{courseOfferingId='" + courseOfferingId + "', department='" + department +
               "', courseId='" + courseId + "', year=" + year + "}";
    }
}