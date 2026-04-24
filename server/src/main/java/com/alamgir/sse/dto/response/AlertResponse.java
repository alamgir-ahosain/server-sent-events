package com.alamgir.sse.dto.response;

import com.alamgir.sse.dto.enums.ALERT_TYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse{
    private String id;
    private String username;
    private ALERT_TYPE type;
    private String description;
    private LocalDateTime createdAt;
}


/*

@Builder
public record AlertResponse(
        Long id,
        ALERT_TYPE type,
        String description,
        Long userId,
        String userName
) {
}Long userId,
        String userName
) {
}
 */