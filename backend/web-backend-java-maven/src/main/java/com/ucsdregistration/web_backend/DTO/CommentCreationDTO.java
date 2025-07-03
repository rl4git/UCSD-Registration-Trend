package com.ucsdregistration.web_backend.DTO;

import lombok.*;

@Data
public class CommentCreationDTO {
    private String courseOfferingId;
    private String profId;
    private String commentContent;
}
