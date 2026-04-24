package com.alamgir.sse.dto.enums;

public enum ROLE {
    FARMER,   // can subscribe to SSE and read own alerts
    ADMIN      // can broadcast or unicast alerts to farmers
}
