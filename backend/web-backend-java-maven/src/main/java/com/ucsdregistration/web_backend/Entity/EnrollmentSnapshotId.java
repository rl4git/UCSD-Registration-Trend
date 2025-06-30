package com.ucsdregistration.web_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data // Lombok 注解，生成 getter, setter, equals, hashCode
@NoArgsConstructor // Lombok 注解，生成无参构造函数
@AllArgsConstructor // Lombok 注解，生成全参构造函数
@Embeddable // 标记这个类是一个可嵌入的类，可以作为另一个实体的主键
public class EnrollmentSnapshotId implements Serializable {
    @Column(name = "course_offering_id")
    private String courseOfferingId;
    
    @Column(name = "date")
    private String date;
}
