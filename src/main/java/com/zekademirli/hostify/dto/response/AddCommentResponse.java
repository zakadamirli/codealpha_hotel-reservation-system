package com.zekademirli.hostify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddCommentResponse {
    private Long commentId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}
