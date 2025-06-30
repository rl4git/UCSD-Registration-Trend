package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.Repository.EnrollmentSnapshotRepository;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Service.EnrollmentSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentSnapshotService esService;

    @GetMapping("/")
    public List<EnrollmentSnapshotDTO> getEnrollmentSnapshotsByCourseOfferingId(@RequestParam String courseOfferingId) {
        return esService.getEnrollmentSnapshots(courseOfferingId);
    }
}
