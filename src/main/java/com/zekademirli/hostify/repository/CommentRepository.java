package com.zekademirli.hostify.repository;

import com.zekademirli.hostify.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    List<Comment> findAllByUserId(Long userId);
}
