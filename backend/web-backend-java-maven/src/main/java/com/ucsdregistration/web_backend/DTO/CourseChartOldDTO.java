package com.ucsdregistration.web_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseChartOldDTO {
// {
//     "courseId": "CSE 12",
//     "academicQuarter": "FA25",
//     "courseSize": 200,
//     "professorFirstName": "Gary",
//     "professorMiddleName": "W",
//     "professorLastName": "Gillespie",
//     "availableSpots":   [200, 150, 110, 80, 40, 10, 5, 0, 0, 0, 0],
//     "waitlistCount":    [0, 0, 0, 5, 15, 30, 45, 55, 60, 60, 62],
//     "enrolledStudents": [0, 50, 90, 120, 160, 190, 195, 200, 200, 200, 200]
// }

    private String courseId;
    private String academicQuarter;
    private Integer courseSize;
    private String professorFirstName;
    private String professorMiddleName;
    private String professorLastName;
    private List<Integer> availableSpots;
    private List<Integer> waitlistCount;
    private List<Integer> enrolledStudents;
  
}
