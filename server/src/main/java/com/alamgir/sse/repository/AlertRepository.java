package com.alamgir.sse.repository;

import com.alamgir.sse.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert,String> {
}
