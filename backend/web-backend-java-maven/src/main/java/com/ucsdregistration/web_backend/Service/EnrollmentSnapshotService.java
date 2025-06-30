package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Repository.EnrollmentSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentSnapshotService {
    @Autowired
    private EnrollmentSnapshotRepository esRepository;

    public List<EnrollmentSnapshotDTO> getEnrollmentSnapshots(String courseOfferingId){
        List<EnrollmentSnapshot> snapshots = esRepository.findById_CourseOfferingId(courseOfferingId);

        return snapshots.stream()
                .map(EnrollmentSnapshotDTO::new)
                .collect(Collectors.toList());
    }
}
