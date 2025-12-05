package com.bofa.mobile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobileUser {
    private final String userId;
    private String email;
    private String hashedPassword;
    private final List<DeviceInfo> trustedDevices = new ArrayList<>();

    public MobileUser(String email, String hashedPassword) {
        this.userId = UUID.randomUUID().toString();
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<DeviceInfo> getTrustedDevices() {
        return trustedDevices;
    }

    public void addDevice(DeviceInfo device) {
        trustedDevices.add(device);
    }
}
