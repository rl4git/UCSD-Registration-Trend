package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        System.out.println("Service      --  Fetching all courses.");
        List<Course> courses = courseRepository.findAll();
        System.out.println("Service      --  Retrieved " + courses.size() + " courses.");
        return courses;
    }

    public List<CourseDTO> getAllCoursesByDepartmentAndCourseId(String department, String courseId) {
        System.out.println("Service      --  Fetching courses with department: " + department + ", courseId: " + courseId);
        List<Course> courses = courseRepository.findByDepartmentAndCourseId(department, courseId);
        List<CourseDTO> courseDTOs = courses.stream()
                                            .map(CourseDTO::new)
                                            .collect(Collectors.toList());
        System.out.println("Service      --  Retrieved " + (courses.isEmpty() ? "empty list" : courses.size() + " course(s)") + " for department: " + department + ", courseId: " + courseId);
        return courseDTOs;
    }

    public List<CourseDTO> findCoursesByProfName(String profFirstName, String profLastNameString){
        List<Course> courses = courseRepository.findByProfessorsName(profFirstName, profLastNameString);
        List<CourseDTO> result = courses.stream()
                                        .map(CourseDTO::new)
                                        .collect(Collectors.toList());
        return result;
    }

    // return all distinct departments
    public List<String> getAllDepartments() {
        return courseRepository.findDistinctDepartments();
    }

    // return all distinct course id in given department
    public List<String> getCourseIdByDepartment(String department){
        return courseRepository.findDistinctCourseIdByDepartment(department);
    }

}
