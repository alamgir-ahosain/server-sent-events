package com.alamgir.sse.dto.request;


import com.alamgir.sse.dto.enums.ALERT_TYPE;
import lombok.Data;

@Data
public class AlertCreateRequest {

    private String username;
    private ALERT_TYPE type;
    private String description;
}


/*

package com.abad.service.alert.dtos.request;

import com.abad.service.alert.enums.ALERT_TYPE;
import lombok.Builder;

@Builder
public record AlertCreateRequest(

        Long userId,
        String userName,
        ALERT_TYPE type,
        String description

) {
}
 */