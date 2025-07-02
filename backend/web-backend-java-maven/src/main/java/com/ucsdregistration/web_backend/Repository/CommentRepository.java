package com.ucsdregistration.web_backend.Repository;

import com.ucsdregistration.web_backend.Entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // get comment by id, this is a method provided by Jpa
    // Optional<Comment> findById(Long ommentId);

    // get comment by id
    List<Comment> findByCommentId(Long commentId);

    // get all comments for a specific prof_id
    List<Comment> findByProfId(String profId);

    // get all comments for a specific course_offering_id
    List<Comment> findByCourseOfferingId(String courseOfferingId);

    // get all comments for a specific course_offering_id and prof_id
    List<Comment> findByCourseOfferingIdAndProfId(String courseOfferingId, String profId);

    // increase like
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likeCt = c.likeCt + 1 WHERE c.commentId = :commentId")
    void incrementLikeCount(Long commentId);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.dislikeCt = c.dislikeCt + 1 WHERE c.commentId = :commentId")
    void decrementLikeCount(Long CommentId);

}
