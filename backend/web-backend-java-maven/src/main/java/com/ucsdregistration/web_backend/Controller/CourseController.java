package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/by-department-courseid")
    public List<CourseDTO> getCourseDetails(@RequestParam String department, @RequestParam String courseId) {
        System.out.println("Controller   --  Received request for course with department: " + department + ", courseId: " + courseId);
        List<CourseDTO> courseDTOs = courseService.getAllCoursesByDepartmentAndCourseId(department, courseId);
        System.out.println("Controller   --  Returning " + (courseDTOs.isEmpty() ? "empty list" : courseDTOs.size() + " course(s)") + " for department: " + department + ", courseId: " + courseId);
        return courseDTOs;
    }

    @GetMapping("/by-prof-name")
    public List<CourseDTO> getMethodName(@RequestParam String profFirstName, @RequestParam String profLastName) {
        List<CourseDTO> courses = courseService.findCoursesByProfName(profFirstName, profLastName);
        return courses;
    }

    @GetMapping("/departments")
    public List<String> getMethodName() {
        return courseService.getAllDepartments();
    }

    @GetMapping("/courseId/by-department")
    public List<String> getMethodName(@RequestParam String department) {
        return courseService.getCourseIdByDepartment(department);
    }
    
}