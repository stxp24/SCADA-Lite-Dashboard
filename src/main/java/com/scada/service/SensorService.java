package com.scada.monitoring.service;

import com.scada.monitoring.dto.*;
import com.scada.monitoring.entity.Sensor;
import com.scada.monitoring.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {

    private final SensorRepository sensorRepository;

    @Transactional
    public SensorResponse createSensor(CreateSensorRequest request) {
        log.info("Creating new sensor: {}", request.getSensorId());

        if (sensorRepository.existsBySensorId(request.getSensorId())) {
            throw new IllegalArgumentException("Sensor with ID " + request.getSensorId() + " already exists");
        }

        Sensor sensor = new Sensor();
        sensor.setSensorId(request.getSensorId());
        sensor.setName(request.getName());
        sensor.setMotorOn(request.getMotorOn() != null ? request.getMotorOn() : false);
        sensor.setTempThreshold(request.getTempThreshold());
        sensor.setPressureThreshold(request.getPressureThreshold());

        Sensor savedSensor = sensorRepository.save(sensor);
        log.info("Sensor created successfully: {}", savedSensor.getSensorId());

        return mapToResponse(savedSensor);
    }

    @Transactional(readOnly = true)
    public SensorResponse getSensor(String sensorId) {
        log.debug("Fetching sensor: {}", sensorId);
        Sensor sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));
        return mapToResponse(sensor);
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> getAllSensors() {
        log.debug("Fetching all sensors");
        return sensorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SensorResponse updateMotorState(String sensorId, boolean motorOn) {
        log.info("Updating motor state for sensor {}: {}", sensorId, motorOn);
        Sensor sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));

        sensor.setMotorOn(motorOn);
        Sensor updatedSensor = sensorRepository.save(sensor);

        log.info("Motor state updated successfully for sensor: {}", sensorId);
        return mapToResponse(updatedSensor);
    }

    @Transactional
    public SensorResponse updateThresholds(String sensorId, UpdateSensorThresholdsRequest request) {
        log.info("Updating thresholds for sensor: {}", sensorId);
        Sensor sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));

        if (request.getTempThreshold() != null) {
            sensor.setTempThreshold(request.getTempThreshold());
        }
        if (request.getPressureThreshold() != null) {
            sensor.setPressureThreshold(request.getPressureThreshold());
        }

        Sensor updatedSensor = sensorRepository.save(sensor);
        log.info("Thresholds updated successfully for sensor: {}", sensorId);

        return mapToResponse(updatedSensor);
    }

    @Transactional
    public void deleteSensor(String sensorId) {
        log.info("Deleting sensor: {}", sensorId);
        Sensor sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));
        sensorRepository.delete(sensor);
        log.info("Sensor deleted successfully: {}", sensorId);
    }

    private SensorResponse mapToResponse(Sensor sensor) {
        SensorResponse response = new SensorResponse();
        response.setId(sensor.getId());
        response.setSensorId(sensor.getSensorId());
        response.setName(sensor.getName());
        response.setMotorOn(sensor.isMotorOn());
        response.setTempThreshold(sensor.getTempThreshold());
        response.setPressureThreshold(sensor.getPressureThreshold());
        response.setCreatedAt(sensor.getCreatedAt());
        response.setUpdatedAt(sensor.getUpdatedAt());
        return response;
    }
}