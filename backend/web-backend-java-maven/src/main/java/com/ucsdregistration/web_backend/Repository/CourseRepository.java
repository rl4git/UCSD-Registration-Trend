package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.Course;
import com.ucsdregistration.web_backend.Entity.EnrollmentSnapshot;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // JpaRepository<实体类型，主键类型>
    
    @EntityGraph(attributePaths = "professors")
    @Query("SELECT c FROM Course c WHERE LOWER(c.department) = LOWER(:department) AND LOWER(c.courseId) = LOWER(:courseId)")
    List<Course> findByDepartmentAndCourseId(@Param("department") String department, @Param("courseId") String courseId);

    // 忽略大小写的检索版本
    @Query("SELECT DISTINCT c FROM Course c JOIN FETCH c.professors p WHERE LOWER(p.profFirstName) = LOWER(:firstName) AND LOWER(p.profLastName) = LOWER(:lastName)")
    List<Course> findByProfessorsName(@Param("firstName") String firstName, @Param("lastName") String lastName);


    @Query("SELECT DISTINCT c.department FROM Course c")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT c.courseId FROM Course c WHERE LOWER(c.department) = LOWER(:department)")
    List<String> findDistinctCourseIdByDepartment(@Param("department") String department);

}
