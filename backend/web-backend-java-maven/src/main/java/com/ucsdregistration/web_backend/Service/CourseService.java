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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

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
        
        // 获取所有 passtime 和 passtag，尽量减少数据库交互
        Map<String, List<Passtime>> passtimeCache = new HashMap<>();
        for (Course course : courseList) {
            String cacheKey = course.getYear() + course.getQuarter();
            // 只有当缓存中没有时，才查询数据库
            passtimeCache.computeIfAbsent(cacheKey, k -> 
                passtimeRepository.findByYearAndQuarter(course.getYear(), course.getQuarter())
            );
        }

        // 返回结果
        List<CourseChartDTO> courseCharts = new ArrayList<>();

        // 对每一门课，构建CourseChart
        for (Course course : courseList){
            // 获取这门课的注册数据
            List<EnrollmentSnapshot> relatedSnapshots = snapShotsById.getOrDefault(course.getCourseOfferingId(), Collections.emptyList());
            Map<String, EnrollmentSnapshot> snapshotMap = relatedSnapshots.stream()
                .collect(Collectors.toMap(EnrollmentSnapshot::getDate, snapshot -> snapshot, (s1, s2) -> s1));

            // 获取Passtime数据，需要根据日期进行排序
            // 已经在数据库层面进行了排序
            List<Passtime> passtimes = passtimeCache.get(course.getYear()+course.getQuarter());

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

    /**
     * 这个方法单纯只是为了旧的网页api设计的
     * 这里！我使用的是 CourseChartOldDTO
        这个DTO是为了旧网页适配的
        如果未来打算在新网页使用这个方法
        就去修改mergeCourseGroup的最后，让他返回CourseChartDTO
     * @param department
     * @param courseId
     * @return
     * 
     */
    private record GroupingKey(String quarter, Set<ProfessorDTO> professors) {}
    public List<CourseChartOldDTO> getCourseChartDataOld(String department, String courseId){
        List<CourseChartDTO> originalCourses = this.getCourseChartData(department, courseId);
        
        if (originalCourses == null || originalCourses.isEmpty()){
            return Collections.emptyList();
        }

        // 对于Quarter和professor相同的课程，进行整合（对于数字，平均数处理）
        Map<GroupingKey, List<CourseChartDTO>> groupedCourses = originalCourses.stream()
            .collect(Collectors.groupingBy(
                dto -> new GroupingKey(dto.getQuarter(), dto.getProfessors())
            ));
        
        return groupedCourses.values().stream()
            .map(this::mergeCourseGroup)
            .collect(Collectors.toList());
    }

    // 整合每一门 quarter 和 professor 相同的课程
    private CourseChartOldDTO mergeCourseGroup(List<CourseChartDTO> group){
        if (group == null || group.isEmpty())
            return null;
        // if (group.size() == 1)
        //     return group.get(0);
        
        CourseChartDTO template = group.get(0);
        int groupSize = group.size();

        // 计算平均总size
        int avfTotal = (int) group.stream()
            .mapToInt(CourseChartDTO::getTotal)
            .average()
            .orElse(0.0);
        
        // 计算平均注册数据
        List<Integer> avgAvailable = new ArrayList<>();
        List<Integer> avgEnrolled = new ArrayList<>();
        List<Integer> avgWaitlist = new ArrayList<>();
        
        int listSize = template.getAvailable().size();
        for (int i=0; i<listSize; i++){
            // 对每个位置i，计算所有课程在该位置上的值的总和
            final int index = i;
            double sumAvailable = group.stream().mapToDouble(dto -> dto.getAvailable().get(index)).sum();
            double sumEnrolled = group.stream().mapToDouble(dto -> dto.getEnrolled().get(index)).sum();
            double sumWaitlist = group.stream().mapToDouble(dto -> dto.getWaitlist().get(index)).sum();

            avgAvailable.add((int) Math.round(sumAvailable / groupSize));
            avgEnrolled.add((int) Math.round(sumEnrolled / groupSize));
            avgWaitlist.add((int) Math.round(sumWaitlist / groupSize));
        }

        // 这里！我使用的是 CourseChartOldDTO
        // 这个DTO是为了旧网页适配的
        // 如果未来打算在新网页使用这个方法
        // 就修改这里
        CourseChartOldDTO dto = new CourseChartOldDTO();

        dto.setCourseId(template.getDepartment() + " " + template.getCourseId());
        dto.setAcademicQuarter(template.getQuarter());
        dto.setCourseSize(avfTotal);
        dto.setProfessorFirstName(template.getInstructor());
        dto.setProfessorLastName("");
        dto.setProfessorMiddleName("");
        dto.setEnrolledStudents(avgEnrolled);
        dto.setWaitlistCount(avgWaitlist);
        dto.setAvailableSpots(avgAvailable);

        return dto;
    }
}