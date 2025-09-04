package com.zekademirli.hostify.dto.response;

import lombok.Builder;

@Builder
public record AddPostResponse(Long id, Long userId, Long propertyId) {
}
