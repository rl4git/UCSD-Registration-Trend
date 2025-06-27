package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.Course;

import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = "professors") // <-- 告诉JPA在执行查询时，要同时抓取 "professors" 这个属性
    // 下面的 JOIN FETCH 也会有同样的效果
    // @Query("SELECT DISTINCT c FROM Course c JOIN FETCH c.professors p WHERE p.profFirstName = :firstName AND p.profLastName = :lastName")
    List<Course> findByProfessors_ProfFirstNameAndProfessors_ProfLastName(String profFirstName, String profLastName);
}
