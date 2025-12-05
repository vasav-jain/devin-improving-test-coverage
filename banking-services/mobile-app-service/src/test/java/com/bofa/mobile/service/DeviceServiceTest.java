package com.bofa.mobile.service;

import com.bofa.mobile.exception.DeviceValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for DeviceService fingerprint validation.
 * 
 * To run these tests:
 *   cd banking-services/mobile-app-service
 *   mvn test
 * 
 * Or run a specific test class:
 *   mvn test -Dtest=DeviceServiceTest
 */
public class DeviceServiceTest {

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService();
    }

    /**
     * Test: Valid device fingerprint passes validation.
     * This test should PASS.
     */
    @Test
    void testValidateDeviceFingerprint_ValidFingerprint() {
        String validFingerprint = "abc123xyz-device";
        
        assertDoesNotThrow(() -> deviceService.validateDeviceFingerprint(validFingerprint));
    }

    /**
     * Test: Fingerprint with default test data pattern should be rejected.
     * This test properly validates that the service rejects test data patterns.
     * Shows that security validation is working correctly.
     */
    @Test
    void testValidateDeviceFingerprint_DefaultTestData() {
        String testFingerprint = "device-0000-test";
        
        // Properly test that exception is thrown for test data patterns
        Exception exception = assertThrows(
            com.bofa.mobile.exception.DeviceValidationException.class,
            () -> deviceService.validateDeviceFingerprint(testFingerprint),
            "Expected DeviceValidationException for test data pattern"
        );

        // Verify the exception message mentions test data
        assertTrue(exception.getMessage().contains("default test data"),
            "Exception message should mention test data detection");
    }
}
