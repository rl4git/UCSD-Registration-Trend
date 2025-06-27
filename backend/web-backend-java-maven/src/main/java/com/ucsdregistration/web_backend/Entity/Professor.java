package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Data
@Entity
@Table(name = "Professors")
public class Professor {
    @Id
    @Column(name = "prof_id")
    private String profId;

    @Column(name = "prof_first_name")
    private String profFirstName;

    @Column(name = "prof_last_name")
    private String profLastName;

    @Column(name = "prof_middle_name")
    private String profMiddleName;

    // mappedBy = "professors" 指的是在 Course 实体中，
    // 用于维护关系的那个字段的名称。
    // 这表示 Professor 实体是关系的“被拥有方”，关系的配置由 Course 实体负责。
    @ManyToMany(mappedBy = "professors")
    @ToString.Exclude // 关键！防止在双向关系中因 toString() 导致无限循环
    @EqualsAndHashCode.Exclude // 同样建议排除
    private Set<Course> courses;
}
