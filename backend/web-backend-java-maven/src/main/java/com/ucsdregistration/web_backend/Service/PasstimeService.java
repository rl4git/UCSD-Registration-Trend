package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Repository.EnrollmentSnapshotRepository;
import com.ucsdregistration.web_backend.Repository.PasstimeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasstimeService {

    @Autowired
    private PasstimeRepository passtimeRepository;

    public List<PasstimeDTO> getPasstagByYearAndQuarter(String year, String quarter){
        List<Passtime> passtimes = passtimeRepository.findByYearAndQuarter(Integer.parseInt(year), quarter);
        List<PasstimeDTO> result = passtimes.stream()
                                            .map(PasstimeDTO::new)
                                            .collect(Collectors.toList());
        return result;
    }

}
