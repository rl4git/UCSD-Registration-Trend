package com.ucsdregistration.web_backend.DTO;

import com.ucsdregistration.web_backend.Entity.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long commentId;
    private String courseOfferingId;
    private String profId;
    private String comment;
    private LocalDateTime createdAt;

    public CommentDTO(Comment comment){
        this.commentId = comment.getCommentId();
        this.courseOfferingId = comment.getCourseOfferingId();
        this.profId = comment.getProfId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }
}
