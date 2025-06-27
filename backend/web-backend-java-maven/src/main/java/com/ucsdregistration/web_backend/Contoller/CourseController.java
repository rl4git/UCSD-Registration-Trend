package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.Entity.Course;
import com.ucsdregistration.web_backend.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/course")
    public List<Course> getCourseDetails(@RequestParam String department, @RequestParam String courseId) {
        System.out.println("Controller   --  Received request for course with department: " + department + ", courseId: " + courseId);
        List<Course> courses = courseService.getAllCoursesByDepartmentAndCourseId(department, courseId);
        System.out.println("Controller   --  Returning " + (courses.isEmpty() ? "empty list" : courses.size() + " course(s)") + " for department: " + department + ", courseId: " + courseId);
        return courses;
    }

    @GetMapping("/topten")
    public List<Course> getTopTenCourses() {
        System.out.println("Controller   --  Get course top ten request.");
        List<Course> courses = courseService.findTopTenCourseByYear();
        System.out.println("Controller   --  Returning " + (courses.isEmpty() ? "empty list" : courses.size() + " course(s)") + " for top ten request.");
        return courses;
    }
}