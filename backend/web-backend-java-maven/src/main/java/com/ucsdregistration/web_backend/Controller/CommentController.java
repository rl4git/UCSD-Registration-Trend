package com.ucsdregistration.web_backend.Controller;

import com.ucsdregistration.web_backend.Entity.*;
import com.ucsdregistration.web_backend.DTO.*;
import com.ucsdregistration.web_backend.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable("id") Long commentId){
        CommentDTO commentDTO = commentService.getByCommentId(commentId);
        if(commentDTO != null){
            return ResponseEntity.ok(commentDTO);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API 端点：根据课程或课程与教授组合获取评论列表
     * HTTP 方法：GET
     * URL：/api/comments?courseOfferingId=...&profId=... (profId可选)
     * @param courseOfferingId 课程提供ID (必须)
     * @param profId 教授ID (可选)
     * @return 返回评论DTO列表和 200 OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<CommentDTO>> getCommentByCourse(
            @RequestParam String courseOfferingId,
            @RequestParam(required = false) String profId){
            
        List<CommentDTO> comments;
        if (profId != null && !profId.isEmpty()) {
            comments = commentService.getByCourseOfferingIdAndProfId(courseOfferingId, profId);
        } else {
            comments = commentService.getByCourseOfferingId(courseOfferingId);
        }
        return ResponseEntity.ok(comments);
    }

    /**
     * API 端点：上传新评论
     * HTTP 方法：POST
     * URL：/api/comments
     * 请求体 (Request Body)：包含 courseOfferingId, profId, commentContent 的 JSON 对象
     * @param creationDTO 包含评论所需信息的DTO
     * @return 创建成功后返回新的评论实体和 201 Created 状态码
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentCreationDTO creationDTO){
        CommentDTO newCommentDTO = commentService.uploadComment(
            creationDTO.getCourseOfferingId(),
            creationDTO.getProfId(),
            creationDTO.getCommentContent()
        );
        return new ResponseEntity<>(newCommentDTO, HttpStatus.CREATED);
    }

    /**
     * API 端点：给评论点赞
     * HTTP 方法：PUT
     * URL：/api/comments/{id}/like
     * @param id 评论的ID
     * @return 如果成功，返回 200 OK；如果评论不存在，返回 404 Not Found
     */
    @PutMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable("id") Long id){
        boolean success = commentService.likeComment(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API 端点：给评论点踩
     * HTTP 方法：PUT
     * URL：/api/comments/{id}/dislike
     * @param id 评论的ID
     * @return 如果成功，返回 200 OK；如果评论不存在，返回 404 Not Found
     */
    @PutMapping("/{id}/dislike")
    public ResponseEntity<Void> dislikeComment(@PathVariable("id") Long id){
        boolean success = commentService.dislikeComment(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


}
