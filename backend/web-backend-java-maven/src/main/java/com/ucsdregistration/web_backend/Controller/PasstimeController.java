package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Service.CourseService;
import com.ucsdregistration.web_backend.Service.PasstimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/passtimes")
public class PasstimeController {
    @Autowired
    private PasstimeService passtimeService;

    @GetMapping("/by-year-quarter")
    public List<PasstimeDTO> getPasstimeByYearAndQuarter(@RequestParam String year, @RequestParam String quarter) {
        return passtimeService.getPasstagByYearAndQuarter(year, quarter);
    }
}
