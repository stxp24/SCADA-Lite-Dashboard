package com.scada.monitoring.controller;

import com.scada.monitoring.dto.*;
import com.scada.monitoring.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensor Management", description = "APIs for managing sensors and their configurations")
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    @Operation(summary = "Create a new sensor", description = "Register a new sensor with configurable thresholds")
    public ResponseEntity<SensorResponse> createSensor(@Valid @RequestBody CreateSensorRequest request) {
        log.info("POST /api/sensors - Creating sensor: {}", request.getSensorId());
        SensorResponse response = sensorService.createSensor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all sensors", description = "Retrieve a list of all registered sensors")
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        log.info("GET /api/sensors - Fetching all sensors");
        List<SensorResponse> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/{sensorId}")
    @Operation(summary = "Get sensor by ID", description = "Retrieve detailed information about a specific sensor")
    public ResponseEntity<SensorResponse> getSensor(@PathVariable String sensorId) {
        log.info("GET /api/sensors/{} - Fetching sensor", sensorId);
        SensorResponse response = sensorService.getSensor(sensorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sensorId}/motor")
    @Operation(summary = "Control motor state", description = "Turn motor on or off for a specific sensor")
    public ResponseEntity<SensorResponse> controlMotor(
            @PathVariable String sensorId,
            @Valid @RequestBody MotorControlRequest request) {
        log.info("PUT /api/sensors/{}/motor - Setting motor to: {}", sensorId, request.getMotorOn());
        SensorResponse response = sensorService.updateMotorState(sensorId, request.getMotorOn());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sensorId}/thresholds")
    @Operation(summary = "Update sensor thresholds", description = "Configure temperature and pressure warning thresholds")
    public ResponseEntity<SensorResponse> updateThresholds(
            @PathVariable String sensorId,
            @Valid @RequestBody UpdateSensorThresholdsRequest request) {
        log.info("PUT /api/sensors/{}/thresholds - Updating thresholds", sensorId);
        SensorResponse response = sensorService.updateThresholds(sensorId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sensorId}")
    @Operation(summary = "Delete sensor", description = "Remove a sensor from the system")
    public ResponseEntity<Void> deleteSensor(@PathVariable String sensorId) {
        log.info("DELETE /api/sensors/{} - Deleting sensor", sensorId);
        sensorService.deleteSensor(sensorId);
        return ResponseEntity.noContent().build();
    }
}
