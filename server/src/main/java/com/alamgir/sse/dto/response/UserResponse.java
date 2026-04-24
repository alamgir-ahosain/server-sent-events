package com.alamgir.sse.dto.response;

import com.alamgir.sse.dto.enums.ROLE;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private ROLE role;
}