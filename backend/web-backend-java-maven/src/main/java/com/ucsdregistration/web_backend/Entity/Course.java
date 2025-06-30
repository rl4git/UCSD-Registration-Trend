package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @Column(name = "course_offering_id")
    private String courseOfferingId;

    @Column(name = "department")
    private String department;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "instructor")
    private String instructor;

    @Column(name = "year")
    private Integer year;

    @Column(name = "quarter")
    private String quarter;

    @Column(name = "total")
    private Integer total;

    /**
        @ManyToMany: 声明这是一个多对多关系。
        fetch = FetchType.LAZY: 这是一个性能优化。
        它告诉Hibernate只有在你显式访问 course.getProfessors() 时，才去数据库加载关联的教授信息。否则，每次查询课程都会附带一堆不必要的教授信息。

        @JoinTable: 这是配置多对多关系的核心。我们用它来告诉Hibernate关于中间表的一切。
        - name = "courses_professors": 中间表的名字。
        - joinColumns = @JoinColumn(name = "course_offering_id"): 指向拥有方实体（Course）在中间表中的外键列。
        - inverseJoinColumns = @JoinColumn(name = "prof_id"): 指向被拥有方实体（Professor）在中间表中的外键列。

        @ToString.Exclude 和 @EqualsAndHashCode.Exclude: 这是绝对必要的！ 
        Lombok的 @Data 注解会自动生成 toString(), equals() 和 hashCode() 方法。
        在一个双向关系中，如果两个实体都试图打印对方，course.toString() 会调用 professor.toString()，
        而 professor.toString() 又会调用 course.toString()，导致 StackOverflowError。通过排除集合字段，可以避免这个问题。
    */
    // --- 关键的多对多关系映射 ---
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(
        name = "courses_professors", // 指定中间表的名字
        joinColumns = @JoinColumn(name = "course_offering_id"), // 定义本实体(Course)在中间表的外键
        inverseJoinColumns = @JoinColumn(name = "prof_id") // 定义关联实体(Professor)在中间表的外键
    )
    @ToString.Exclude // 关键！防止无限循环
    @EqualsAndHashCode.Exclude // 同样建议排除
    private Set<Professor> professors;

    // 与Enrollement_Snapshots的链接
    @OneToMany(
        mappedBy = "course",    // 关键：指明这个关系是由 EnrollmentSnapshot 实体中的 "course" 字段来维护的
        cascade = CascadeType.ALL,
         orphanRemoval = true,
        fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EnrollmentSnapshot> enrollmentSnapshots;

    // 你的自定义 toString 很好，因为它没有引用 professors 集合，所以不会引起循环。
    // 如果你移除了它，并依赖 Lombok 的 @Data，那么上面的 @ToString.Exclude 就至关重要。
    @Override
    public String toString() {
        return "Course{courseOfferingId='" + courseOfferingId + "', department='" + department +
                "', courseId='" + courseId + "', year=" + year + "}";
    }
}