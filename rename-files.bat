@echo off
REM Windows Batch Script to Rename All Files
REM Save this as rename-files.bat and run it from project root

echo ========================================
echo SCADA Project File Renamer
echo ========================================
echo.

cd /d C:\SCADA-Spring-Boot\src\main\java\com\scada

echo Renaming Main Application...
cd monitoring
if exist ScadaMonitoringApp.java (
    ren ScadaMonitoringApp.java ScadaMonitoringApplication.java
    echo [OK] ScadaMonitoringApp.java renamed to ScadaMonitoringApplication.java
) else (
    echo [SKIP] ScadaMonitoringApp.java not found
)
cd ..

echo.
echo Renaming Config Files...
cd config
if exist DataInit.java (
    ren DataInit.java DataInitializer.java
    echo [OK] DataInit.java renamed to DataInitializer.java
) else (
    echo [SKIP] DataInit.java not found
)

if exist OpenAPIConfig.java (
    ren OpenAPIConfig.java OpenApiConfig.java
    echo [OK] OpenAPIConfig.java renamed to OpenApiConfig.java
) else (
    echo [SKIP] OpenAPIConfig.java not found
)
cd ..

echo.
echo Renaming DTO Files...
cd dto
if exist CreateSensorReq.java (
    ren CreateSensorReq.java CreateSensorRequest.java
    echo [OK] CreateSensorReq.java renamed to CreateSensorRequest.java
) else (
    echo [SKIP] CreateSensorReq.java not found
)

if exist CreateReadingReq.java (
    ren CreateReadingReq.java CreateReadingRequest.java
    echo [OK] CreateReadingReq.java renamed to CreateReadingRequest.java
) else (
    echo [SKIP] CreateReadingReq.java not found
)

if exist UpdateSensorThreshReq.java (
    ren UpdateSensorThreshReq.java UpdateSensorThresholdsRequest.java
    echo [OK] UpdateSensorThreshReq.java renamed to UpdateSensorThresholdsRequest.java
) else (
    echo [SKIP] UpdateSensorThreshReq.java not found
)

if exist MotorControlReq.java (
    ren MotorControlReq.java MotorControlRequest.java
    echo [OK] MotorControlReq.java renamed to MotorControlRequest.java
) else (
    echo [SKIP] MotorControlReq.java not found
)
cd ..

echo.
echo Renaming Repository Files...
cd repository
if exist SensorRepo.java (
    ren SensorRepo.java SensorRepository.java
    echo [OK] SensorRepo.java renamed to SensorRepository.java
) else (
    echo [SKIP] SensorRepo.java not found
)

if exist SensorReadingRepo.java (
    ren SensorReadingRepo.java SensorReadingRepository.java
    echo [OK] SensorReadingRepo.java renamed to SensorReadingRepository.java
) else (
    echo [SKIP] SensorReadingRepo.java not found
)
cd ..

echo.
echo Renaming Exception Handler...
cd exception
if exist GlobalExceptionHandling.java (
    ren GlobalExceptionHandling.java GlobalExceptionHandler.java
    echo [OK] GlobalExceptionHandling.java renamed to GlobalExceptionHandler.java
) else (
    echo [SKIP] GlobalExceptionHandling.java not found
)
cd ..

echo.
echo ========================================
echo File renaming complete!
echo ========================================
echo.
echo Next steps:
echo 1. Reload your IDE project
echo 2. Enable annotation processing in IDE settings
echo 3. Run: mvn clean install
echo.
pause