package com.bofa.mobile.service;

import com.bofa.mobile.dto.LoginRequest;
import com.bofa.mobile.dto.LoginResponse;
import com.bofa.mobile.dto.RegisterRequest;
import com.bofa.mobile.exception.AuthenticationException;
import com.bofa.mobile.model.DeviceInfo;
import com.bofa.mobile.model.MobileUser;
import com.bofa.mobile.repository.UserRepository;
import com.bofa.mobile.security.JwtTokenFactory;
import com.bofa.mobile.util.PasswordHasher;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenFactory jwtTokenFactory;
    private final DeviceService deviceService;

    public AuthService(UserRepository userRepository,
                       JwtTokenFactory jwtTokenFactory,
                       DeviceService deviceService) {
        this.userRepository = userRepository;
        this.jwtTokenFactory = jwtTokenFactory;
        this.deviceService = deviceService;
    }

    public String registerUser(RegisterRequest request) {
        deviceService.validateDeviceFingerprint(request.getDeviceFingerprint());
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AuthenticationException("Email already registered");
        });

        String hashed = PasswordHasher.hash(request.getPassword());
        MobileUser user = new MobileUser(request.getEmail(), hashed);
        DeviceInfo deviceInfo = deviceService.createDevice(request.getDeviceFingerprint());
        user.addDevice(deviceInfo);
        userRepository.save(user);
        return user.getUserId();
    }

    public LoginResponse loginUser(LoginRequest request) {
        deviceService.validateDeviceFingerprint(request.getDeviceFingerprint());
        MobileUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        String hashed = PasswordHasher.hash(request.getPassword());
        if (!hashed.equals(user.getHashedPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        boolean trusted = user.getTrustedDevices().stream()
                .anyMatch(device -> device.getFingerprint().equals(request.getDeviceFingerprint()));
        if (!trusted) {
            DeviceInfo newDevice = deviceService.createDevice(request.getDeviceFingerprint());
            user.addDevice(newDevice);
            userRepository.save(user);
        }
        String token = jwtTokenFactory.issueToken(user.getUserId());
        return new LoginResponse(token, Duration.ofMinutes(30).toSeconds());
    }
}
