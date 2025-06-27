package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // JpaRepository<实体类型，主键类型>
    
    @Query("SELECT c FROM Course c WHERE c.department = :department AND c.courseId = :courseId")
    List<Course> findByDepartmentAndCourseId(@Param("department") String department, @Param("courseId") String courseId);

    List<Course> findTop10ByOrderByYearAsc();
}
