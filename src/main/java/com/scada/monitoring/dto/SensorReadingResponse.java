package com.scada.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingResponse {
    private Long id;
    private String sensorId;
    private Double temperature;
    private Double pressure;
    private boolean motorOn;
    private LocalDateTime timestamp;
    private boolean tempWarning;
    private boolean pressureWarning;
    private String warningMessage;
}