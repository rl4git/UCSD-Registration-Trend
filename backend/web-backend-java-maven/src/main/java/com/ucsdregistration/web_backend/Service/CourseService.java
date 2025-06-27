package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.Course;
import com.ucsdregistration.web_backend.Repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<Course> getAllCoursesByDepartmentAndCourseId(String department, String courseId) {
        System.out.println("Service      --  Fetching courses with department: " + department + ", courseId: " + courseId);
        List<Course> courses = courseRepository.findByDepartmentAndCourseId(department, courseId);
        System.out.println("Service      --  Retrieved " + (courses.isEmpty() ? "empty list" : courses.size() + " course(s)") + " for department: " + department + ", courseId: " + courseId);
        return courses;
    }

    public List<Course> findTopTenCourseByYear() {
        System.out.println("Service      --  Get course top ten request.");
        List<Course> result = courseRepository.findTop10ByOrderByYearAsc();
        System.out.println("Service      --  Retrieved " + (result.isEmpty() ? "empty list" : result.size() + " course(s)") + " for top ten request.");
        if (!result.isEmpty()) {
            System.out.println("Service      --  First course: " + result.get(0));
        }
        return result;
    }
}