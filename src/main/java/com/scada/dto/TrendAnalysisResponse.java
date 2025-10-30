package com.scada.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendAnalysisResponse {
    private String sensorId;
    private Double avgTemperature;
    private Double maxTemperature;
    private Double minTemperature;
    private Double avgPressure;
    private Double maxPressure;
    private Double minPressure;
    private int totalReadings;
    private int warningCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}