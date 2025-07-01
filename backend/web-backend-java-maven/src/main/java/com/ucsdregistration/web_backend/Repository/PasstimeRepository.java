package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.Passtime;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PasstimeRepository extends JpaRepository<Passtime, String> {

    @Query("SELECT p FROM Passtime p WHERE p.year = :year AND LOWER(p.quarter) = LOWER(:quarter)")
    List<Passtime> findByYearAndQuarter(@Param("year") Integer year, @Param("quarter") String quarter);

}
