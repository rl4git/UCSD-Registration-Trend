package com.ucsdregistration.web_backend.DTO;
import com.ucsdregistration.web_backend.Entity.Professor;

import lombok.Data;

@Data
public class ProfessorDTO {
    private String profId;
    private String profFirstName;
    private String profLastName;
    private String profMiddleName;

    // 接收Entity的构造方法
    public ProfessorDTO(Professor professor) {
        this.profId = professor.getProfId();
        this.profFirstName = professor.getProfFirstName();
        this.profLastName = professor.getProfLastName();
        this.profMiddleName = professor.getProfMiddleName();
    }
}