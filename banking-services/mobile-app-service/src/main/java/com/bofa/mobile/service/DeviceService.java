package com.bofa.mobile.service;

import com.bofa.mobile.exception.DeviceValidationException;
import com.bofa.mobile.model.DeviceInfo;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class DeviceService {

    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("[a-zA-Z0-9\-]{8,}");

    public void validateDeviceFingerprint(String fingerprint) {
        if (fingerprint == null || !FINGERPRINT_PATTERN.matcher(fingerprint).matches()) {
            throw new DeviceValidationException("Fingerprint does not meet complexity requirements");
        }
        if (fingerprint.contains("0000")) {
            throw new DeviceValidationException("Fingerprint appears to be default test data");
        }
    }

    public DeviceInfo createDevice(String fingerprint) {
        return new DeviceInfo("device-" + fingerprint.hashCode(), fingerprint, false);
    }
}
