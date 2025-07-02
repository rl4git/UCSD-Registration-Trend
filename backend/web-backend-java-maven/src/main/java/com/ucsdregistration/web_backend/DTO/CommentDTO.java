package com.ucsdregistration.web_backend.DTO;

import com.ucsdregistration.web_backend.Entity.*;
import lombok.*;

@Data
public class CommentDTO {
    private Long commentId;
    private String courseOfferingId;
    private String profId;
    private String comment;
    private String created_at;
}
