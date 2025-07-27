package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Collections;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/{department}/{courseId}")
    public List<CourseDTO> getCourseDetails(@PathVariable("department") String department, @PathVariable("courseId") String courseId) {
        System.out.println("Controller   --  Received request for course with department: " + department + ", courseId: " + courseId);
        List<CourseDTO> courseDTOs = courseService.getAllCoursesByDepartmentAndCourseId(department, courseId);
        System.out.println("Controller   --  Returning " + (courseDTOs.isEmpty() ? "empty list" : courseDTOs.size() + " course(s)") + " for department: " + department + ", courseId: " + courseId);
        return courseDTOs;
    }

    @GetMapping("/search")
    public List<CourseDTO> getCoursesByProfessorName(@RequestParam String profFirstName, @RequestParam String profLastName) {
        List<CourseDTO> courses = courseService.findCoursesByProfName(profFirstName, profLastName);
        return courses;
    }

    @GetMapping("/departments")
    public List<String> getAlldepartments() {
        return courseService.getAllDepartments();
    }

    @GetMapping("/{department}/ids")
    public List<String> getCourseIdByDepartment(@PathVariable("department") String department) {
        return courseService.getCourseIdByDepartment(department);
    }

    @GetMapping("/{department}/{courseId}/chart")
    public List<CourseChartDTO> getCourseChartByDepartmentAndCourseId(@PathVariable("department") String department, @PathVariable("courseId") String courseId) {
        return courseService.getCourseChartData(department, courseId);
    }
    
    @GetMapping("/{department}/{courseId}/chart/old")
    public List<CourseChartOldDTO> getCourseChartByDepartmentAndCourseIdOld(@PathVariable("department") String department, @PathVariable("courseId") String courseId) {
        return courseService.getCourseChartDataOld(department, courseId);
    }

}
