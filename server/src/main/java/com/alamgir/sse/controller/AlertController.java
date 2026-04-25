package com.alamgir.sse.controller;


import com.alamgir.sse.dto.request.AlertCreateRequest;
import com.alamgir.sse.dto.response.AlertResponse;
import com.alamgir.sse.service.Abstraction.AlertService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {

    Logger logger= LoggerFactory.getLogger(AlertController.class);

    private  final AlertService alertService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AlertController is working!");
    }
    @PostMapping("/create")
    public ResponseEntity<AlertResponse> create(@Valid @RequestBody AlertCreateRequest request){
        logger.info("AlertController: POST /api/alert/create called");
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request));
    }

    @GetMapping(  "/{id}")
    public ResponseEntity<AlertResponse> getById(@PathVariable String id){
        logger.info("AlertController: GET /api/alert/{id} called");
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @GetMapping(  "/all")
    public ResponseEntity<List<AlertResponse>> getAll(){
        logger.info("AlertController: GET /api/alert/all called");
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        logger.info("AlertController: DELETE /api/alert/{id} called");
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return alertService.subscribeClient();
    }

    // Open SSE stream - browser keeps this connection open
    @GetMapping(value = "/subscribe/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable String email) {
        logger.info("AlertController: GET /api/alert/subscribe called");
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .body(alertService.subscribeClient(email));
    }


    @PostMapping("/broadcast")
    public ResponseEntity<AlertResponse> broadcast(@Valid @RequestBody AlertCreateRequest request){
        logger.info("AlertController: POST /api/alert/broadcast called");
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.broadcastAlert(request));
    }



    @PostMapping("/unicast/{email}")
    public ResponseEntity<AlertResponse> uniCast(@PathVariable String email,@Valid @RequestBody AlertCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.uniCastAlert(email, request));
    }

}
