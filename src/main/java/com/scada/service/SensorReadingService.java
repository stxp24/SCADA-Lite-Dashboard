package com.scada.monitoring.service;

import com.scada.monitoring.dto.*;
import com.scada.monitoring.entity.Sensor;
import com.scada.monitoring.entity.SensorReading;
import com.scada.monitoring.repository.SensorReadingRepository;
import com.scada.monitoring.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorReadingService {

    private final SensorReadingRepository readingRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public SensorReadingResponse createReading(CreateReadingRequest request) {
        log.debug("Creating reading for sensor: {}", request.getSensorId());

        Sensor sensor = sensorRepository.findBySensorId(request.getSensorId())
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + request.getSensorId()));

        SensorReading reading = new SensorReading();
        reading.setSensorId(request.getSensorId());
        reading.setTemperature(request.getTemperature());
        reading.setPressure(request.getPressure());
        reading.setMotorOn(request.getMotorOn());
        reading.setTimestamp(LocalDateTime.now());

        List<String> warnings = new ArrayList<>();
        if (request.getTemperature() >= sensor.getTempThreshold()) {
            reading.setTempWarning(true);
            warnings.add("HIGH TEMPERATURE WARNING: " + request.getTemperature() + "°C >= " + sensor.getTempThreshold() + "°C");
            log.warn("Temperature threshold exceeded for sensor {}: {}°C", request.getSensorId(), request.getTemperature());
        }

        if (request.getPressure() >= sensor.getPressureThreshold()) {
            reading.setPressureWarning(true);
            warnings.add("HIGH PRESSURE WARNING: " + request.getPressure() + " PSI >= " + sensor.getPressureThreshold() + " PSI");
            log.warn("Pressure threshold exceeded for sensor {}: {} PSI", request.getSensorId(), request.getPressure());
        }

        if (!warnings.isEmpty()) {
            reading.setWarningMessage(String.join(" | ", warnings));
        }

        SensorReading savedReading = readingRepository.save(reading);
        log.debug("Reading saved for sensor: {}", request.getSensorId());

        return mapToResponse(savedReading);
    }

    @Transactional
    public List<SensorReadingResponse> createBulkReadings(List<CreateReadingRequest> requests) {
        log.info("Creating {} bulk readings", requests.size());
        return requests.stream()
                .map(this::createReading)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> getRecentReadings(String sensorId, Integer limit) {
        log.debug("Fetching recent readings for sensor: {} (limit: {})", sensorId, limit);
        int pageSize = (limit != null && limit > 0) ? limit : 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<SensorReading> readings = readingRepository.findBySensorIdOrderByTimestampDesc(sensorId, pageable);
        return readings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> getAllReadings(String sensorId) {
        log.debug("Fetching all readings for sensor: {}", sensorId);
        List<SensorReading> readings;

        if (sensorId != null && !sensorId.isEmpty()) {
            readings = readingRepository.findBySensorIdOrderByTimestampDesc(sensorId, Pageable.unpaged());
        } else {
            readings = readingRepository.findAll();
        }

        return readings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> getReadingsByTimeRange(String sensorId, LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching readings for sensor {} between {} and {}", sensorId, start, end);
        List<SensorReading> readings = readingRepository.findBySensorIdAndTimestampBetweenOrderByTimestampDesc(
                sensorId, start, end);

        return readings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> getWarnings(String sensorId, Integer limit) {
        log.debug("Fetching warnings for sensor: {}", sensorId);
        int pageSize = (limit != null && limit > 0) ? limit : 50;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<SensorReading> warnings;
        if (sensorId != null && !sensorId.isEmpty()) {
            warnings = readingRepository.findWarningsBySensorId(sensorId, pageable);
        } else {
            warnings = readingRepository.findAllWarnings(pageable);
        }

        return warnings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrendAnalysisResponse getTrendAnalysis(String sensorId, Integer limit) {
        log.debug("Generating trend analysis for sensor: {}", sensorId);
        int analysisLimit = (limit != null && limit > 0) ? limit : 100;
        Pageable pageable = PageRequest.of(0, analysisLimit);

        List<SensorReading> readings = readingRepository.findBySensorIdOrderByTimestampDesc(sensorId, pageable);

        if (readings.isEmpty()) {
            throw new IllegalArgumentException("No readings found for sensor: " + sensorId);
        }

        TrendAnalysisResponse analysis = new TrendAnalysisResponse();
        analysis.setSensorId(sensorId);
        analysis.setTotalReadings(readings.size());

        double sumTemp = 0, sumPressure = 0;
        double maxTemp = Double.MIN_VALUE, minTemp = Double.MAX_VALUE;
        double maxPressure = Double.MIN_VALUE, minPressure = Double.MAX_VALUE;
        int warningCount = 0;

        LocalDateTime earliest = readings.get(readings.size() - 1).getTimestamp();
        LocalDateTime latest = readings.get(0).getTimestamp();

        for (SensorReading reading : readings) {
            sumTemp += reading.getTemperature();
            sumPressure += reading.getPressure();

            maxTemp = Math.max(maxTemp, reading.getTemperature());
            minTemp = Math.min(minTemp, reading.getTemperature());

            maxPressure = Math.max(maxPressure, reading.getPressure());
            minPressure = Math.min(minPressure, reading.getPressure());

            if (reading.isTempWarning() || reading.isPressureWarning()) {
                warningCount++;
            }
        }

        analysis.setAvgTemperature(sumTemp / readings.size());
        analysis.setMaxTemperature(maxTemp);
        analysis.setMinTemperature(minTemp);
        analysis.setAvgPressure(sumPressure / readings.size());
        analysis.setMaxPressure(maxPressure);
        analysis.setMinPressure(minPressure);
        analysis.setWarningCount(warningCount);
        analysis.setStartTime(earliest);
        analysis.setEndTime(latest);

        return analysis;
    }

    private SensorReadingResponse mapToResponse(SensorReading reading) {
        SensorReadingResponse response = new SensorReadingResponse();
        response.setId(reading.getId());
        response.setSensorId(reading.getSensorId());
        response.setTemperature(reading.getTemperature());
        response.setPressure(reading.getPressure());
        response.setMotorOn(reading.isMotorOn());
        response.setTimestamp(reading.getTimestamp());
        response.setTempWarning(reading.isTempWarning());
        response.setPressureWarning(reading.isPressureWarning());
        response.setWarningMessage(reading.getWarningMessage());
        return response;
    }
}