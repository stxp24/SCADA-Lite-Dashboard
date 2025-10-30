package com.scada.monitoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings", indexes = {
        @Index(name = "idx_sensor_id", columnList = "sensorId"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double pressure;

    @Column(nullable = false)
    private boolean motorOn;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private boolean tempWarning;

    private boolean pressureWarning;

    @Column(length = 500)
    private String warningMessage;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}