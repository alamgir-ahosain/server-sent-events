package com.alamgir.sse.service.Abstraction;

import com.alamgir.sse.dto.request.AlertCreateRequest;
import com.alamgir.sse.dto.response.AlertResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface AlertService {

    AlertResponse createAlert(AlertCreateRequest request);
    void deleteAlert(String id);
    AlertResponse uniCastAlert(String email, AlertCreateRequest request);
    AlertResponse broadcastAlert(AlertCreateRequest request);
    AlertResponse getAlertById(String id);
    List<AlertResponse> getAllAlerts();
    SseEmitter subscribeClient();
    SseEmitter subscribeClient(String email);
}
