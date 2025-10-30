package com.scada.monitoring.service;

import com.scada.monitoring.dto.CreateReadingRequest;
import com.scada.monitoring.dto.SensorReadingResponse;
import com.scada.monitoring.entity.Sensor;
import com.scada.monitoring.entity.SensorReading;
import com.scada.monitoring.repository.SensorReadingRepository;
import com.scada.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceTest {

    @Mock
    private SensorReadingRepository readingRepository;

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorReadingService readingService;

    private Sensor testSensor;
    private SensorReading testReading;
    private CreateReadingRequest createRequest;

    @BeforeEach
    void setUp() {
        testSensor = new Sensor();
        testSensor.setId(1L);
        testSensor.setSensorId("TEST-001");
        testSensor.setName("Test Sensor");
        testSensor.setTempThreshold(25.0);
        testSensor.setPressureThreshold(25.0);
        testSensor.setMotorOn(false);

        testReading = new SensorReading();
        testReading.setId(1L);
        testReading.setSensorId("TEST-001");
        testReading.setTemperature(22.5);
        testReading.setPressure(15.0);
        testReading.setMotorOn(false);
        testReading.setTimestamp(LocalDateTime.now());
        testReading.setTempWarning(false);
        testReading.setPressureWarning(false);

        createRequest = new CreateReadingRequest();
        createRequest.setSensorId("TEST-001");
        createRequest.setTemperature(22.5);
        createRequest.setPressure(15.0);
        createRequest.setMotorOn(false);
    }

    @Test
    void createReading_NoWarnings() {
        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(readingRepository.save(any(SensorReading.class))).thenReturn(testReading);

        SensorReadingResponse response = readingService.createReading(createRequest);

        assertNotNull(response);
        assertEquals("TEST-001", response.getSensorId());
        assertEquals(22.5, response.getTemperature());
        assertFalse(response.isTempWarning());
        assertFalse(response.isPressureWarning());
        verify(readingRepository, times(1)).save(any(SensorReading.class));
    }

    @Test
    void createReading_WithTempWarning() {
        createRequest.setTemperature(26.0); // Above threshold

        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(readingRepository.save(any(SensorReading.class))).thenAnswer(invocation -> {
            SensorReading reading = invocation.getArgument(0);
            reading.setId(1L);
            return reading;
        });

        SensorReadingResponse response = readingService.createReading(createRequest);

        assertNotNull(response);
        assertTrue(response.isTempWarning());
        assertNotNull(response.getWarningMessage());
    }

    @Test
    void createReading_WithPressureWarning() {
        createRequest.setPressure(26.0); // Above threshold

        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(readingRepository.save(any(SensorReading.class))).thenAnswer(invocation -> {
            SensorReading reading = invocation.getArgument(0);
            reading.setId(1L);
            return reading;
        });

        SensorReadingResponse response = readingService.createReading(createRequest);

        assertNotNull(response);
        assertTrue(response.isPressureWarning());
        assertNotNull(response.getWarningMessage());
    }

    @Test
    void createReading_SensorNotFound_ThrowsException() {
        when(sensorRepository.findBySensorId("NONEXISTENT")).thenReturn(Optional.empty());

        createRequest.setSensorId("NONEXISTENT");

        assertThrows(IllegalArgumentException.class, () -> {
            readingService.createReading(createRequest);
        });

        verify(readingRepository, never()).save(any(SensorReading.class));
    }

    @Test
    void getRecentReadings() {
        List<SensorReading> readings = Arrays.asList(testReading);
        when(readingRepository.findBySensorIdOrderByTimestampDesc(anyString(), any(Pageable.class)))
                .thenReturn(readings);

        List<SensorReadingResponse> responses = readingService.getRecentReadings("TEST-001", 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("TEST-001", responses.get(0).getSensorId());
    }

    @Test
    void createBulkReadings() {
        List<CreateReadingRequest> requests = Arrays.asList(createRequest, createRequest);

        when(sensorRepository.findBySensorId("TEST-001")).thenReturn(Optional.of(testSensor));
        when(readingRepository.save(any(SensorReading.class))).thenReturn(testReading);

        List<SensorReadingResponse> responses = readingService.createBulkReadings(requests);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(readingRepository, times(2)).save(any(SensorReading.class));
    }
}
