package com.zekademirli.hostify.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long postId,
        String username,
        LocalDateTime createdAt) {
}
