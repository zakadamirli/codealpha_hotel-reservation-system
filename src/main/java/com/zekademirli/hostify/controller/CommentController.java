package com.zekademirli.hostify.controller;

import com.zekademirli.hostify.dto.request.AddCommentRequest;
import com.zekademirli.hostify.dto.response.AddCommentResponse;
import com.zekademirli.hostify.dto.response.CommentResponse;
import com.zekademirli.hostify.dto.response.UserCommentResponse;
import com.zekademirli.hostify.services.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public AddCommentResponse addComment(@RequestBody AddCommentRequest addCommentRequest) {
        return commentService.addComment(addCommentRequest);
    }

    @GetMapping
    public List<CommentResponse> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{commentId}")
    public CommentResponse getOneComment(@PathVariable Long commentId) {
        return commentService.getOneComment(commentId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/show-all-comments-by-post-id/{postId}")
    public List<CommentResponse> getComment(@PathVariable Long postId) {
        return commentService.getAllCommentsByPostId(postId);
    }

    @GetMapping("/show-all-comments-by-user-id/{userId}")
    public List<UserCommentResponse> getAllCommentsByUserId(@PathVariable Long userId) {
        return commentService.getAllCommentsByUserId(userId);
    }
}
