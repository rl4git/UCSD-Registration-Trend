package com.ucsdregistration.web_backend.DTO;

import com.ucsdregistration.web_backend.Entity.*;
import lombok.Data;

@Data
public class PasstimeDTO {
    private Integer year;
    private String quarter;
    private String passtime;
    private String passtag;

    public PasstimeDTO(Passtime passtime){
        this.year = passtime.getYear();
        this.quarter = passtime.getQuarter();
        this.passtime = passtime.getPasstime();
        this.passtag = passtime.getPassstag();
    }
}
