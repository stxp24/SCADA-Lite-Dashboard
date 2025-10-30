package com.scada.monitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scada.monitoring.dto.CreateReadingRequest;
import com.scada.monitoring.dto.SensorReadingResponse;
import com.scada.monitoring.dto.TrendAnalysisResponse;
import com.scada.monitoring.service.SensorReadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorReadingController.class)
class SensorReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SensorReadingService readingService;

    private SensorReadingResponse readingResponse;
    private CreateReadingRequest createRequest;

    @BeforeEach
    void setUp() {
        readingResponse = new SensorReadingResponse();
        readingResponse.setId(1L);
        readingResponse.setSensorId("TEST-001");
        readingResponse.setTemperature(22.5);
        readingResponse.setPressure(15.0);
        readingResponse.setMotorOn(false);
        readingResponse.setTimestamp(LocalDateTime.now());
        readingResponse.setTempWarning(false);
        readingResponse.setPressureWarning(false);

        createRequest = new CreateReadingRequest();
        createRequest.setSensorId("TEST-001");
        createRequest.setTemperature(22.5);
        createRequest.setPressure(15.0);
        createRequest.setMotorOn(false);
    }

    @Test
    void createReading_Success() throws Exception {
        when(readingService.createReading(any(CreateReadingRequest.class))).thenReturn(readingResponse);

        mockMvc.perform(post("/api/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensorId").value("TEST-001"))
                .andExpect(jsonPath("$.temperature").value(22.5));
    }

    @Test
    void createBulkReadings() throws Exception {
        List<CreateReadingRequest> requests = Arrays.asList(createRequest, createRequest);
        List<SensorReadingResponse> responses = Arrays.asList(readingResponse, readingResponse);

        when(readingService.createBulkReadings(any())).thenReturn(responses);

        mockMvc.perform(post("/api/readings/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].sensorId").value("TEST-001"));
    }

    @Test
    void getRecentReadings() throws Exception {
        List<SensorReadingResponse> readings = Arrays.asList(readingResponse);
        when(readingService.getRecentReadings(anyString(), anyInt())).thenReturn(readings);

        mockMvc.perform(get("/api/readings/recent")
                        .param("sensorId", "TEST-001")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorId").value("TEST-001"));
    }

    @Test
    void getAllReadings() throws Exception {
        List<SensorReadingResponse> readings = Arrays.asList(readingResponse);
        when(readingService.getAllReadings(anyString())).thenReturn(readings);

        mockMvc.perform(get("/api/readings")
                        .param("sensorId", "TEST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorId").value("TEST-001"));
    }

    @Test
    void getWarnings() throws Exception {
        readingResponse.setTempWarning(true);
        List<SensorReadingResponse> warnings = Arrays.asList(readingResponse);
        when(readingService.getWarnings(anyString(), anyInt())).thenReturn(warnings);

        mockMvc.perform(get("/api/readings/warnings")
                        .param("sensorId", "TEST-001")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tempWarning").value(true));
    }

    @Test
    void getTrendAnalysis() throws Exception {
        TrendAnalysisResponse trend = new TrendAnalysisResponse();
        trend.setSensorId("TEST-001");
        trend.setAvgTemperature(22.5);
        trend.setMaxTemperature(25.0);
        trend.setMinTemperature(20.0);
        trend.setTotalReadings(10);

        when(readingService.getTrendAnalysis(anyString(), anyInt())).thenReturn(trend);

        mockMvc.perform(get("/api/readings/trends/TEST-001")
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("TEST-001"))
                .andExpect(jsonPath("$.totalReadings").value(10));
    }
}