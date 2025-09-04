package com.zekademirli.hostify.dto.request;

public record AddCommentRequest(Long propertyId, Long userId, Long postId, String content) {
}