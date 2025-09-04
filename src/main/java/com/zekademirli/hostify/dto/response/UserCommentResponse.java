package com.zekademirli.hostify.dto.response;

public record UserCommentResponse(
        Long commentId,
        String content,
        Long postId) {
}
