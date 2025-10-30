package com.scada.monitoring.service;

import com.scada.monitoring.dto.CreateSensorRequest;
import com.scada.monitoring.dto.SensorResponse;
import com.scada.monitoring.dto.UpdateSensorThresholdsRequest;
import com.scada.monitoring.entity.Sensor;
import com.scada.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorService sensorService;

    private Sensor testSensor;
    private CreateSensorRequest createRequest;

    @BeforeEach
    void setUp() {
        testSensor = new Sensor();
        testSensor.setId(1L);
        testSensor.setSensorId("TEST-001");
        testSensor.setName("Test Sensor");
        testSensor.setTempThreshold(25.0);
        testSensor.setPressureThreshold(25.0);
        testSensor.setMotorOn(false);

        createRequest = new CreateSensorRequest();
        createRequest.setSensorId("TEST-001");
        createRequest.setName("Test Sensor");
        createRequest.setTempThreshold(25.0);
        createRequest.setPressureThreshold(25.0);
        createRequest.setMotorOn(false);
    }

    @Test
    void createSensor_Success() {
        when(sensorRepository.existsBySensorId(anyString())).thenReturn(false);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);

        SensorResponse response = sensorService.createSensor(createRequest);

        assertNotNull(response);
        assertEquals("TEST-001", response.getSensorId());
        assertEquals("Test Sensor", response.getName());
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void createSensor_AlreadyExists_ThrowsException() {
        when(sensorRepository.existsBySensorId(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            sensorService.createSensor(createRequest);
        });

        verify(sensorRepository, never()).save(any(Sensor.class));
    }

    @Test
    void getSensor_Found() {
        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));

        SensorResponse response = sensorService.getSensor("TEST-001");

        assertNotNull(response);
        assertEquals("TEST-001", response.getSensorId());
    }

    @Test
    void getSensor_NotFound_ThrowsException() {
        when(sensorRepository.findBySensorId("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            sensorService.getSensor("NONEXISTENT");
        });
    }

    @Test
    void getAllSensors() {
        List<Sensor> sensors = Arrays.asList(testSensor);
        when(sensorRepository.findAll()).thenReturn(sensors);

        List<SensorResponse> responses = sensorService.getAllSensors();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("TEST-001", responses.get(0).getSensorId());
    }

    @Test
    void updateMotorState() {
        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);

        SensorResponse response = sensorService.updateMotorState("TEST-001", true);

        assertNotNull(response);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void updateThresholds() {
        UpdateSensorThresholdsRequest request = new UpdateSensorThresholdsRequest();
        request.setTempThreshold(30.0);
        request.setPressureThreshold(28.0);

        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);

        SensorResponse response = sensorService.updateThresholds("TEST-001", request);

        assertNotNull(response);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void deleteSensor() {
        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        doNothing().when(sensorRepository).delete(any(Sensor.class));

        sensorService.deleteSensor("TEST-001");

        verify(sensorRepository, times(1)).delete(any(Sensor.class));
    }
}
