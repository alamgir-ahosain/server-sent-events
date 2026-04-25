package com.alamgir.sse.service.Implementation;


import com.alamgir.sse.dto.request.AlertCreateRequest;
import com.alamgir.sse.dto.response.AlertResponse;
import com.alamgir.sse.entity.Alert;
import com.alamgir.sse.entity.User;
import com.alamgir.sse.exception.NotFoundException;
import com.alamgir.sse.repository.AlertRepository;
import com.alamgir.sse.repository.UserRepository;
import com.alamgir.sse.service.Abstraction.AlertService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> uniCastEmitters = new ConcurrentHashMap<>();


    @Override
    @Transactional
    public AlertResponse createAlert(AlertCreateRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));

        Alert alert = Alert.builder()
                .type(request.getType())
                .description(request.getDescription())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Alert savedAlert = alertRepository.save(alert);
        logger.info("Alert created: id={}, email={}, type={}", savedAlert.getId(), user.getEmail(), savedAlert.getType());
        return mapToResponse(savedAlert);
    }



    @Override
    @Transactional
    public void deleteAlert(String id) {
        alertRepository.findById(id).orElseThrow(() -> new NotFoundException("Alert not found with id: " + id));
        alertRepository.deleteById(id);
    }



    @Override
    public AlertResponse getAlertById(String id) {
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new NotFoundException("Alert not found with id: " + id));
        return mapToResponse(alert);
    }



    @Override
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    @Override
    public SseEmitter subscribeClient() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.addIfAbsent(emitter);

        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError(e -> removeEmitter(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE connection established for all users")
                    .reconnectTime(3000));
        } catch (IOException e) {
            removeEmitter(emitter);
        }
        return emitter;
    }

    @Override
    public SseEmitter subscribeClient(String email) {
        SseEmitter emitter = new SseEmitter(0L);

        registerEmitter(email, emitter);

        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError(e -> removeEmitter(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE connection established for: " + email)
                    .reconnectTime(3000));
        } catch (IOException e) {
            removeEmitter(emitter);
        }

        return emitter;
    }




    private void registerEmitter(String email, SseEmitter emitter) {
        emitters.addIfAbsent(emitter);
        uniCastEmitters.computeIfAbsent(email, key -> new CopyOnWriteArrayList<>()).addIfAbsent(emitter);
    }



    private void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);

        uniCastEmitters.forEach((email, emittersList) -> {
            emittersList.remove(emitter);
            if (emittersList.isEmpty()) {
                uniCastEmitters.remove(email, emittersList);
            }
        });
    }


    @Override
    @Transactional
    public AlertResponse broadcastAlert(AlertCreateRequest request) {

        AlertResponse alertResponse = createAlert(request);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-alert")
                        .data(alertResponse)
                        .id(alertResponse.getId()));
            } catch (IOException e) {
                removeEmitter(emitter);
            }
        }

        return alertResponse;
    }




    @Override
    @Transactional
    public AlertResponse uniCastAlert(String email, AlertCreateRequest request) {

        AlertResponse response = createAlert(request);

        CopyOnWriteArrayList<SseEmitter> userEmitters = uniCastEmitters.get(email);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("new-alert")
                            .data(response)
                            .id(response.getId()));
                } catch (IOException e) {
                    removeEmitter(emitter);
                }
            }
        }

        return response;
    }




    private AlertResponse mapToResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getUser().getEmail(),
                alert.getType(),
                alert.getDescription(),
                alert.getCreatedAt()
        );
    }
}
