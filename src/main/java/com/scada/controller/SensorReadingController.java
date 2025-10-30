package com.scada.monitoring.controller;

import com.scada.monitoring.dto.*;
import com.scada.monitoring.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensor Readings", description = "APIs for submitting and retrieving sensor readings")
public class SensorReadingController {

    private final SensorReadingService readingService;

    @PostMapping
    @Operation(summary = "Submit sensor reading", description = "Post a new sensor reading with automatic threshold checking")
    public ResponseEntity<SensorReadingResponse> createReading(@Valid @RequestBody CreateReadingRequest request) {
        log.info("POST /api/readings - Creating reading for sensor: {}", request.getSensorId());
        SensorReadingResponse response = readingService.createReading(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Submit multiple readings", description = "Post multiple sensor readings simultaneously for concurrent sensor support")
    public ResponseEntity<List<SensorReadingResponse>> createBulkReadings(@Valid @RequestBody List<CreateReadingRequest> requests) {
        log.info("POST /api/readings/bulk - Creating {} readings", requests.size());
        List<SensorReadingResponse> responses = readingService.createBulkReadings(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping
    @Operation(summary = "Get all readings", description = "Retrieve all sensor readings, optionally filtered by sensor ID")
    public ResponseEntity<List<SensorReadingResponse>> getAllReadings(
            @Parameter(description = "Optional sensor ID to filter readings")
            @RequestParam(required = false) String sensorId) {
        log.info("GET /api/readings - Fetching readings for sensor: {}", sensorId);
        List<SensorReadingResponse> readings = readingService.getAllReadings(sensorId);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent readings", description = "Retrieve the most recent N readings for trend analysis")
    public ResponseEntity<List<SensorReadingResponse>> getRecentReadings(
            @Parameter(description = "Sensor ID", required = true)
            @RequestParam String sensorId,
            @Parameter(description = "Number of readings to retrieve (default: 10)")
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.info("GET /api/readings/recent - Fetching {} recent readings for sensor: {}", limit, sensorId);
        List<SensorReadingResponse> readings = readingService.getRecentReadings(sensorId, limit);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/range")
    @Operation(summary = "Get readings by time range", description = "Retrieve readings within a specific time period")
    public ResponseEntity<List<SensorReadingResponse>> getReadingsByTimeRange(
            @Parameter(description = "Sensor ID", required = true)
            @RequestParam String sensorId,
            @Parameter(description = "Start time (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("GET /api/readings/range - Fetching readings for sensor {} between {} and {}", sensorId, start, end);
        List<SensorReadingResponse> readings = readingService.getReadingsByTimeRange(sensorId, start, end);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/warnings")
    @Operation(summary = "Get threshold warnings", description = "Retrieve all readings that triggered threshold warnings")
    public ResponseEntity<List<SensorReadingResponse>> getWarnings(
            @Parameter(description = "Optional sensor ID to filter warnings")
            @RequestParam(required = false) String sensorId,
            @Parameter(description = "Number of warnings to retrieve (default: 50)")
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        log.info("GET /api/readings/warnings - Fetching warnings for sensor: {}", sensorId);
        List<SensorReadingResponse> warnings = readingService.getWarnings(sensorId, limit);
        return ResponseEntity.ok(warnings);
    }

    @GetMapping("/trends/{sensorId}")
    @Operation(summary = "Get trend analysis", description = "Retrieve statistical analysis of sensor readings for trend monitoring")
    public ResponseEntity<TrendAnalysisResponse> getTrendAnalysis(
            @PathVariable String sensorId,
            @Parameter(description = "Number of readings to analyze (default: 100)")
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        log.info("GET /api/readings/trends/{} - Generating trend analysis", sensorId);
        TrendAnalysisResponse analysis = readingService.getTrendAnalysis(sensorId, limit);
        return ResponseEntity.ok(analysis);
    }
}