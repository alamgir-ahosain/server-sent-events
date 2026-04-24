package com.alamgir.sse.service.Implementation;


import com.alamgir.sse.dto.request.AlertCreateRequest;
import com.alamgir.sse.dto.response.AlertResponse;
import com.alamgir.sse.entity.Alert;
import com.alamgir.sse.entity.User;
import com.alamgir.sse.exception.IllegalStateException;
import com.alamgir.sse.repository.AlertRepository;
import com.alamgir.sse.repository.UserRepository;
import com.alamgir.sse.service.Abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl {

    Logger logger= LoggerFactory.getLogger(AlertServiceImpl.class);

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Map<String, List<SseEmitter>> uniCastEmitters = new ConcurrentHashMap<>();



    public AlertResponse createAlert(AlertCreateRequest request){

        // Validate user
        User user=userRepository.findByEmail(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found with email: " + request.getUsername()));

        // Create and save alert
        Alert alert=Alert.builder()
                .type(request.getType())
                .description(request.getDescription())
                .user(user)
                .build();
        Alert savedAlert=alertRepository.save(alert);
        logger.info("Alert created successfully: {}", savedAlert.getId());
        return mapToResponse(savedAlert);
    }
    public void deleteAlert(String id) {
        Alert Alert = alertRepository.findById(id).orElseThrow(() -> new IllegalStateException("Alert not found with id: " + id));
        alertRepository.deleteById(id);
    }
    public AlertResponse getAlertById(String id) {
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new IllegalStateException("Alert not found with id: " + id));
        return mapToResponse(alert);
    }
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public SseEmitter subscribeClient(String userName) {
        SseEmitter emitter = new SseEmitter(0L);

        uniCastEmitters.computeIfAbsent(userName, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeUserEmitter(userName, emitter));
        emitter.onTimeout(() -> removeUserEmitter(userName, emitter));
        emitter.onError(e -> removeUserEmitter(userName, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE connection established for: " + userName)
                    .reconnectTime(3000));
        } catch (IOException e) {
            removeUserEmitter(userName, emitter);
        }


        return emitter;
    }
    private void removeUserEmitter(String userName, SseEmitter emitter) {
        List<SseEmitter> emittersList = uniCastEmitters.get(userName);
        if (emittersList != null) {
            emittersList.remove(emitter);
            if (emittersList.isEmpty()) uniCastEmitters.remove(userName);
        }
    }
    public AlertResponse broadcastAlert(AlertCreateRequest request) {

        // 1. Create Alert entity from request
        AlertResponse alertResponse = createAlert(request);

        // 2. Notify all farmers
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-alert")
                        .data(alertResponse)
                        .id(alertResponse.getId()));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }

        return alertResponse;
    }
    public AlertResponse uniCastAlert(String userName, AlertCreateRequest request) {

        AlertResponse response = createAlert(request);

        List<SseEmitter> userEmitters = uniCastEmitters.get(userName);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("new-alert")
                            .data(response)
                            .id(response.getId()));
                } catch (IOException e) {
                    removeUserEmitter(userName, emitter);
                }
            }
        }

        return response;
    }
    private AlertResponse mapToResponse(Alert alert){
        return new AlertResponse(
                alert.getId(),
                alert.getUser().getEmail(),
                alert.getType(),
                alert.getDescription(),
                alert.getCreatedAt()
        );
    }
}
