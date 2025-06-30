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
    List<Course> findByDepartmentAndCourseId(String department, String courseId);

    @EntityGraph(attributePaths = "professors") // <-- 告诉JPA在执行查询时，要同时抓取 "professors" 这个属性
    // 下面的 JOIN FETCH 也会有同样的效果
    // @Query("SELECT DISTINCT c FROM Course c JOIN FETCH c.professors p WHERE p.profFirstName = :firstName AND p.profLastName = :lastName")
    List<Course> findByProfessors_ProfFirstNameAndProfessors_ProfLastName(String profFirstName, String profLastName);

    @Query("SELECT DISTINCT c.department FROM Course c")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT c.courseId FROM Course c WHERE c.department = :department")
    List<String> findDistinctCourseIdByDepartment(@Param("department") String department);
}
