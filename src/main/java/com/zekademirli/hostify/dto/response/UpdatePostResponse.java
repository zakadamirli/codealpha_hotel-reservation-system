package com.zekademirli.hostify.dto.response;

import lombok.Builder;

@Builder
public record UpdatePostResponse(Long id, Long userId, Long propertyId) {
}
