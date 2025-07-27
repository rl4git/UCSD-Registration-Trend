package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Repository.CourseRepository;
import com.ucsdregistration.web_backend.Repository.EnrollmentSnapshotRepository;
import com.ucsdregistration.web_backend.Repository.PasstimeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentSnapshotRepository enrollmentSnapshotRepository;

    @Autowired
    private PasstimeRepository passtimeRepository;

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

    // 这个方法会返回一个包含绝大部分必须信息的对象，即CourseChart
    // 这主要是为了老的网站接口
    // 但是由于其便利性，或许对新网站也很有用

    public List<CourseChartDTO> getCourseChartData(String department, String courseId){
        // 获取所有课程信息
        List<Course> courseList = courseRepository.findByDepartmentAndCourseId(department, courseId);
        // 获取所有 courseOfferingId，然后根据courseOfferingId从抓取所有注册数据
        // 然后根据courseOfferingId分组，方便后续对于每一门课进行过滤
        List<String> courseOfferingIds = courseList.stream()
            .map(Course::getCourseOfferingId)
            .collect(Collectors.toList());
        List<EnrollmentSnapshot> allSnapshots = enrollmentSnapshotRepository.findById_CourseOfferingIdIn(courseOfferingIds);
        Map<String, List<EnrollmentSnapshot>> snapShotsById = allSnapshots.stream()
            .collect(Collectors.groupingBy(snapshot -> snapshot.getId().getCourseOfferingId()));
        
        List<CourseChartDTO> courseCharts = new ArrayList<>();

        // 对每一门课，构建CourseChart
        for (Course course : courseList){
            // 获取这门课的注册数据
            List<EnrollmentSnapshot> relatedSnapshots = snapShotsById.getOrDefault(course.getCourseOfferingId(), Collections.emptyList());
            Map<String, EnrollmentSnapshot> snapshotMap = relatedSnapshots.stream()
                .collect(Collectors.toMap(EnrollmentSnapshot::getDate, snapshot -> snapshot, (s1, s2) -> s1));

            // 获取Passtime数据，需要根据日期进行排序
            // 已经在数据库层面进行了排序
            List<Passtime> passtimes = passtimeRepository.findByYearAndQuarter(course.getYear(), course.getQuarter());

            CourseChartDTO dto = new CourseChartDTO();
            dto.setDepartment(department);
            dto.setCourseId(courseId);
            dto.setYear(course.getYear());
            dto.setQuarter(course.getQuarter());
            dto.setTotal(course.getTotal());
            dto.setInstructor(course.getInstructor());
            if (course.getProfessors() != null && !course.getProfessors().isEmpty()) {
                Set<ProfessorDTO> professors = course.getProfessors().stream()
                        .map(ProfessorDTO::new)
                        .collect(Collectors.toSet());
                dto.setProfessors(professors);
            } else {
                dto.setProfessors(new HashSet<>());
            }

            // 填充注册数据数组
            int courseSize = course.getTotal();
            List<Integer> waitlistSpots = new ArrayList<>();
            List<Integer> enrolledSpots = new ArrayList<>();
            List<Integer> availableSpots = new ArrayList<>();
            
            for (Passtime passtime : passtimes){
                EnrollmentSnapshot snapshot = snapshotMap.getOrDefault(passtime.getPasstime(), null);
                if(snapshot != null){
                    int enrolled = snapshot.getEnrolled();
                    waitlistSpots.add(snapshot.getWaitlist());
                    enrolledSpots.add(enrolled);
                    availableSpots.add(courseSize - enrolled);
                }else{
                    waitlistSpots.add(0);
                    enrolledSpots.add(0);
                    availableSpots.add(courseSize);
                }
            }

            dto.setWaitlist(waitlistSpots);
            dto.setEnrolled(enrolledSpots);
            dto.setAvailable(availableSpots);

            courseCharts.add(dto);
        }
        
        return courseCharts;
    }
}