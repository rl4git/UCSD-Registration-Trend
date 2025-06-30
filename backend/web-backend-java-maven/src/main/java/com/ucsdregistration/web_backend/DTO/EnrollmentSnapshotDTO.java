package com.ucsdregistration.web_backend.DTO;

import lombok.Data;

import com.ucsdregistration.web_backend.Entity.EnrollmentSnapshot;

@Data
public class EnrollmentSnapshotDTO {
    private String date;
    private Integer enrolled;
    private Integer waitlist;

    public EnrollmentSnapshotDTO(EnrollmentSnapshot enrollmentSnapshot){
        this.date = enrollmentSnapshot.getDate();
        this.enrolled = enrollmentSnapshot.getEnrolled();
        this.waitlist = enrollmentSnapshot.getWaitlist();
    }
}
