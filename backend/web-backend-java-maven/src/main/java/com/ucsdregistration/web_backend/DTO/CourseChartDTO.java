package com.ucsdregistration.web_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// {
//     "department": "CSE" // 旧的对象里没有 department 字段，courseId 包含了 CSE 120
//     "courseId": "12",
//     "academicQuarter": "FA25",
//     "courseSize": 200,
//     "professorFirstName": "Gary",
//     "professorMiddleName": "W",
//     "professorLastName": "Gillespie",
//     "availableSpots":   [200, 150, 110, 80, 40, 10, 5, 0, 0, 0, 0],
//     "waitlistCount":    [0, 0, 0, 5, 15, 30, 45, 55, 60, 60, 62],
//     "enrolledStudents": [0, 50, 90, 120, 160, 190, 195, 200, 200, 200, 200]
// }
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseChartDTO {
    private String department;
    private String courseId;
    private Integer year;
    private String quarter;
    private Integer total;
    // private String professorFirstName;
    // private String professorMiddleName;
    // private String professorLastName;
    private String instructor;
    private Set<ProfessorDTO> professors;
    private List<Integer> available;
    private List<Integer> waitlist;
    private List<Integer> enrolled;
}
