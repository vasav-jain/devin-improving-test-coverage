package com.bofa.mobile.repository;

import com.bofa.mobile.model.DeviceInfo;
import com.bofa.mobile.model.MobileUser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<String, MobileUser> users = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        MobileUser user = new MobileUser("j.smith@bofa.com", "seed-hash");
        user.addDevice(new DeviceInfo("iphone-13", "fingerprint-123", false));
        users.put(user.getUserId(), user);
    }

    public MobileUser save(MobileUser user) {
        users.put(user.getUserId(), user);
        return user;
    }

    public Optional<MobileUser> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<MobileUser> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Collection<MobileUser> findAll() {
        return users.values();
    }
}
