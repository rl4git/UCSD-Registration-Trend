package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.EnrollmentSnapshot;
import com.ucsdregistration.web_backend.Entity.EnrollmentSnapshotId;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentSnapshotRepository extends JpaRepository<EnrollmentSnapshot, EnrollmentSnapshotId> {
    List<EnrollmentSnapshot> findById_CourseOfferingId(String courseOfferingId);

    // 它会根据一个课程ID列表，一次性查询出所有相关的 EnrollmentSnapshot 记录
    List<EnrollmentSnapshot> findById_CourseOfferingIdIn(List<String> courseOfferingIds);
}
