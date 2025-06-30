package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "enrollment_snapshots")
public class EnrollmentSnapshot {
    
    @EmbeddedId
    private EnrollmentSnapshotId id;

    @Column(name = "waitlist")
    private Integer waitlist;

    @Column(name = "enrolled_ct")
    private Integer enrolled;

    @ManyToOne(fetch = FetchType.LAZY)
    // 这是最关键的注解！它告诉JPA：“我这个实体的主键的一部分，要从我关联的 @ManyToOne 实体的主键那里'映射'过来”。
    // 括号里的 "courseOfferingId" 指的是当前实体的主键类 EnrollmentSnapshotId 中的字段名
    @MapsId("courseOfferingId") 
    // 这个注解负责定义数据库层面的外键列。
    // name = "course_offering_id" 指定了在 enrollment_snapshots 这张表中，作为外键的物理列名。
    @JoinColumn(name = "course_offering_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;

    public String getDate() {
        // 确保 id 不为 null，避免 NullPointerException
        return this.id != null ? this.id.getDate() : null;
    }

}
