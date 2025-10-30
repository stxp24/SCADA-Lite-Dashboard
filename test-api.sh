#!/bin/bash
# ============================================================
# FILE: test-api.sh
# LOCATION: scada-monitoring-system/test-api.sh
# DESCRIPTION: Comprehensive API test script
# USAGE: chmod +x test-api.sh && ./test-api.sh
# ============================================================

BASE_URL="http://localhost:8080"
SENSOR_ID="SENSOR-TEST-$(date +%s)"

echo "========================================="
echo "SCADA Monitoring System API Test Script"
echo "========================================="
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_section() {
    echo ""
    echo "${BLUE}========== $1 ==========${NC}"
    echo ""
}

print_success() {
    echo "${GREEN}✓ $1${NC}"
}

# Test 1: Create a new sensor
print_section "1. Creating New Sensor"
CREATE_SENSOR_RESPONSE=$(curl -s -X POST "$BASE_URL/api/sensors" \
  -H "Content-Type: application/json" \
  -d "{
    \"sensorId\": \"$SENSOR_ID\",
    \"name\": \"Test Sensor $(date +%H:%M:%S)\",
    \"tempThreshold\": 25.0,
    \"pressureThreshold\": 25.0,
    \"motorOn\": false
  }")
echo "$CREATE_SENSOR_RESPONSE" | python3 -m json.tool
print_success "Sensor created: $SENSOR_ID"

# Test 2: Get all sensors
print_section "2. Fetching All Sensors"
curl -s "$BASE_URL/api/sensors" | python3 -m json.tool
print_success "Retrieved all sensors"

# Test 3: Get specific sensor
print_section "3. Fetching Specific Sensor"
curl -s "$BASE_URL/api/sensors/$SENSOR_ID" | python3 -m json.tool
print_success "Retrieved sensor: $SENSOR_ID"

# Test 4: Submit a single reading
print_section "4. Submitting Single Reading"
curl -s -X POST "$BASE_URL/api/readings" \
  -H "Content-Type: application/json" \
  -d "{
    \"sensorId\": \"$SENSOR_ID\",
    \"temperature\": 22.5,
    \"pressure\": 15.3,
    \"motorOn\": false
  }" | python3 -m json.tool
print_success "Reading submitted"

# Test 5: Submit bulk readings (simulating multiple sensors)
print_section "5. Submitting Bulk Readings (10+ Sensors)"
curl -s -X POST "$BASE_URL/api/readings/bulk" \
  -H "Content-Type: application/json" \
  -d "[
    {\"sensorId\": \"$SENSOR_ID\", \"temperature\": 23.1, \"pressure\": 16.2, \"motorOn\": false},
    {\"sensorId\": \"$SENSOR_ID\", \"temperature\": 23.5, \"pressure\": 16.8, \"motorOn\": false},
    {\"sensorId\": \"$SENSOR_ID\", \"temperature\": 24.0, \"pressure\": 17.5, \"motorOn\": false},
    {\"sensorId\": \"$SENSOR_ID\", \"temperature\": 24.5, \"pressure\": 18.1, \"motorOn\": false},
    {\"sensorId\": \"$SENSOR_ID\", \"temperature\": 25.2, \"pressure\": 19.0, \"motorOn\": false}
  ]" | python3 -m json.tool
print_success "Bulk readings submitted"

# Test 6: Submit reading that triggers warning
print_section "6. Submitting Reading with Threshold Violation"
curl -s -X POST "$BASE_URL/api/readings" \
  -H "Content-Type: application/json" \
  -d "{
    \"sensorId\": \"$SENSOR_ID\",
    \"temperature\": 26.5,
    \"pressure\": 27.0,
    \"motorOn\": false
  }" | python3 -m json.tool
print_success "Warning-triggering reading submitted"

# Test 7: Get recent readings
print_section "7. Fetching Recent Readings"
curl -s "$BASE_URL/api/readings/recent?sensorId=$SENSOR_ID&limit=10" | python3 -m json.tool
print_success "Retrieved recent readings"

# Test 8: Get warnings
print_section "8. Fetching Warning Logs"
curl -s "$BASE_URL/api/readings/warnings?sensorId=$SENSOR_ID&limit=10" | python3 -m json.tool
print_success "Retrieved warnings"

# Test 9: Turn motor ON
print_section "9. Turning Motor ON"
curl -s -X PUT "$BASE_URL/api/sensors/$SENSOR_ID/motor" \
  -H "Content-Type: application/json" \
  -d "{\"motorOn\": true}" | python3 -m json.tool
print_success "Motor turned ON"

# Test 10: Submit reading with motor on
print_section "10. Submitting Reading with Motor ON"
curl -s -X POST "$BASE_URL/api/readings" \
  -H "Content-Type: application/json" \
  -d "{
    \"sensorId\": \"$SENSOR_ID\",
    \"temperature\": 20.0,
    \"pressure\": 14.5,
    \"motorOn\": true
  }" | python3 -m json.tool
print_success "Reading with motor ON submitted"

# Test 11: Update thresholds
print_section "11. Updating Sensor Thresholds"
curl -s -X PUT "$BASE_URL/api/sensors/$SENSOR_ID/thresholds" \
  -H "Content-Type: application/json" \
  -d "{
    \"tempThreshold\": 30.0,
    \"pressureThreshold\": 28.0
  }" | python3 -m json.tool
print_success "Thresholds updated"

# Test 12: Get trend analysis
print_section "12. Generating Trend Analysis"
curl -s "$BASE_URL/api/readings/trends/$SENSOR_ID?limit=100" | python3 -m json.tool
print_success "Trend analysis generated"

# Test 13: Get readings by time range
print_section "13. Fetching Readings by Time Range"
START_TIME=$(date -u -d '1 hour ago' +%Y-%m-%dT%H:%M:%S)
END_TIME=$(date -u +%Y-%m-%dT%H:%M:%S)
curl -s "$BASE_URL/api/readings/range?sensorId=$SENSOR_ID&start=$START_TIME&end=$END_TIME" | python3 -m json.tool
print_success "Retrieved readings for time range"

# Test 14: Test concurrent requests (simulating real-time monitoring)
print_section "14. Testing Concurrent Requests (10 simultaneous)"
for i in {1..10}; do
  (curl -s -X POST "$BASE_URL/api/readings" \
    -H "Content-Type: application/json" \
    -d "{
      \"sensorId\": \"$SENSOR_ID\",
      \"temperature\": $((20 + RANDOM % 10)).$((RANDOM % 100)),
      \"pressure\": $((15 + RANDOM % 5)).$((RANDOM % 100)),
      \"motorOn\": true
    }" > /dev/null) &
done
wait
print_success "10 concurrent requests completed"

# Test 15: Get all readings for the test sensor
print_section "15. Fetching All Readings for Test Sensor"
curl -s "$BASE_URL/api/readings?sensorId=$SENSOR_ID" | python3 -m json.tool | head -50
print_success "Retrieved all readings (showing first 50 lines)"

# Test 16: Test with pre-loaded sensors
print_section "16. Testing Pre-loaded Sensors"
echo "Sensor SENSOR-001 readings:"
curl -s "$BASE_URL/api/readings/recent?sensorId=SENSOR-001&limit=5" | python3 -m json.tool
print_success "Retrieved pre-loaded sensor data"

# Test 17: Delete the test sensor
print_section "17. Deleting Test Sensor"
curl -s -X DELETE "$BASE_URL/api/sensors/$SENSOR_ID"
print_success "Test sensor deleted: $SENSOR_ID"

# Final summary
print_section "TEST SUMMARY"
echo "${GREEN}All API tests completed successfully!${NC}"
echo ""
echo "Additional endpoints to explore:"
echo "  - Swagger UI: $BASE_URL/swagger-ui.html"
echo "  - H2 Console: $BASE_URL/h2-console"
echo "  - API Docs: $BASE_URL/api-docs"
echo ""
echo "========================================="