package com.scada.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResponse {
    private Long id;
    private String sensorId;
    private String name;
    private boolean motorOn;
    private Double tempThreshold;
    private Double pressureThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}