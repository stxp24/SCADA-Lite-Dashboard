package com.scada.monitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scada.monitoring.dto.CreateSensorRequest;
import com.scada.monitoring.dto.MotorControlRequest;
import com.scada.monitoring.dto.SensorResponse;
import com.scada.monitoring.dto.UpdateSensorThresholdsRequest;
import com.scada.monitoring.service.SensorService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SensorService sensorService;

    private SensorResponse sensorResponse;
    private CreateSensorRequest createRequest;

    @BeforeEach
    void setUp() {
        sensorResponse = new SensorResponse();
        sensorResponse.setId(1L);
        sensorResponse.setSensorId("TEST-001");
        sensorResponse.setName("Test Sensor");
        sensorResponse.setTempThreshold(25.0);
        sensorResponse.setPressureThreshold(25.0);
        sensorResponse.setMotorOn(false);
        sensorResponse.setCreatedAt(LocalDateTime.now());
        sensorResponse.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateSensorRequest();
        createRequest.setSensorId("TEST-001");
        createRequest.setName("Test Sensor");
        createRequest.setTempThreshold(25.0);
        createRequest.setPressureThreshold(25.0);
        createRequest.setMotorOn(false);
    }

    @Test
    void createSensor_Success() throws Exception {
        when(sensorService.createSensor(any(CreateSensorRequest.class))).thenReturn(sensorResponse);

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensorId").value("TEST-001"))
                .andExpect(jsonPath("$.name").value("Test Sensor"));
    }

    @Test
    void getAllSensors() throws Exception {
        List<SensorResponse> sensors = Arrays.asList(sensorResponse);
        when(sensorService.getAllSensors()).thenReturn(sensors);

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorId").value("TEST-001"));
    }

    @Test
    void getSensor() throws Exception {
        when(sensorService.getSensor("TEST-001")).thenReturn(sensorResponse);

        mockMvc.perform(get("/api/sensors/TEST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("TEST-001"));
    }

    @Test
    void controlMotor() throws Exception {
        MotorControlRequest motorRequest = new MotorControlRequest();
        motorRequest.setMotorOn(true);

        sensorResponse.setMotorOn(true);
        when(sensorService.updateMotorState(eq("TEST-001"), eq(true))).thenReturn(sensorResponse);

        mockMvc.perform(put("/api/sensors/TEST-001/motor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorOn").value(true));
    }

    @Test
    void updateThresholds() throws Exception {
        UpdateSensorThresholdsRequest request = new UpdateSensorThresholdsRequest();
        request.setTempThreshold(30.0);
        request.setPressureThreshold(28.0);

        when(sensorService.updateThresholds(eq("TEST-001"), any(UpdateSensorThresholdsRequest.class)))
                .thenReturn(sensorResponse);

        mockMvc.perform(put("/api/sensors/TEST-001/thresholds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSensor() throws Exception {
        mockMvc.perform(delete("/api/sensors/TEST-001"))
                .andExpect(status().isNoContent());
    }
}