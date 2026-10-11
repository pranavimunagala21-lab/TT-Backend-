package com.campussphere.dto;

import com.campussphere.entity.enums.Role;
import java.util.List;

public class RankedPersonMatchDto {
    private Long userId;
    private String name;
    private Role role;
    private String department;
    private double score;
    private List<String> reasons;
    public RankedPersonMatchDto(Long userId, String name, Role role, String department, double score, List<String> reasons) { this.userId=userId; this.name=name; this.role=role; this.department=department; this.score=score; this.reasons=reasons; }
    public Long getUserId(){return userId;} public String getName(){return name;} public Role getRole(){return role;} public String getDepartment(){return department;} public double getScore(){return score;} public List<String> getReasons(){return reasons;}
}
