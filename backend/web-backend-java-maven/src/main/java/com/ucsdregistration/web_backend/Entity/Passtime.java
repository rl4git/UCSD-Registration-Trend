package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "passtimes", uniqueConstraints = {
    // 添加一个唯一约束，确保四列的组合在数据库层面也是唯一的，防止插入重复数据
    @UniqueConstraint(columnNames = {"passtag", "passtime", "year", "quarter"})
})
public class Passtime {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "quarter")
    private String quarter;

    @Column(name = "passtime")
    private String passtime;

    @Column(name = "passtag")
    private String passstag;
}
