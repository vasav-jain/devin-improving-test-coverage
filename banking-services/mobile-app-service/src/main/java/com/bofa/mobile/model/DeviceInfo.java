package com.bofa.mobile.model;

import java.time.Instant;

public class DeviceInfo {
    private final String deviceId;
    private final String fingerprint;
    private final Instant registeredAt;
    private final boolean jailBroken;

    public DeviceInfo(String deviceId, String fingerprint, boolean jailBroken) {
        this.deviceId = deviceId;
        this.fingerprint = fingerprint;
        this.registeredAt = Instant.now();
        this.jailBroken = jailBroken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public boolean isJailBroken() {
        return jailBroken;
    }
}
