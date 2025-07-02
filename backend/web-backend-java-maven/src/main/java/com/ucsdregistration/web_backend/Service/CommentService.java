package com.ucsdregistration.web_backend.Service;

import com.ucsdregistration.web_backend.Entity.Comment;
import com.ucsdregistration.web_backend.DTO.CommentDTO;
import com.ucsdregistration.web_backend.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    
    @Autowired
    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    /**
     * 上传新评论
     * 前端只提供 course_offering_id, prof_id, comment
     * created_at 和 updated_at 由数据库自动生成
     * like_ct 和 dislike_ct 默认为 0
     * @param courseOfferingId 课程提供ID
     * @param profId 教授ID
     * @param commentContent 评论内容
     * @return 保存后的评论实体
     */
    @Transactional
    public Comment uploadComment(String courseOfferingId, String profId, String commentContent) {
        Comment newComment = new Comment(courseOfferingId, profId, commentContent);
        return commentRepository.save(newComment);
    }

    /**
     * 点赞功能
     * @param commentId 评论ID
     * @return true 如果成功，false 如果评论不存在
     */
    @Transactional
    public boolean likeComment(Long commentId){
        Optional<Comment> existingComment = commentRepository.findBy(commentId);
        if (existingComment.isPresent()){
            commentRepository.incrementLikeCount(commendId);
            return true;
        }
        return false; // comment not exist
    }

    /**
     * 点踩功能
     * @param commentId 评论ID
     * @return true 如果成功，false 如果评论不存在
     */
    public boolean dislikeComment(Long commentId){
        Optional<Comment> existingComment = commentRepository.findBy(commentId);
        if(existingComment.isPresent()){
            commentRepository.incrementDislikeCount(commentId);
            return true;
        }
        return false;
    }
    
    public List<CommentDTO> getByCourseOfferingId(String courseOfferingId){}

    public List<CommentDTO> getByCourseOfferingIdAndProfId(String courseOfferingId, String profId){}

    public 

}
