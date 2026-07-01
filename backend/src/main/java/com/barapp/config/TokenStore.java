package com.barapp.config;

import com.barapp.enums.Role;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {

    public record TokenEntry(Long userId, Role role) {}

    private final ConcurrentHashMap<String, TokenEntry> store = new ConcurrentHashMap<>();

    public String generate(Long userId, Role role) {
        String token = UUID.randomUUID().toString();
        store.put(token, new TokenEntry(userId, role));
        return token;
    }

    public Optional<TokenEntry> find(String token) {
        return Optional.ofNullable(store.get(token));
    }

    public void revoke(String token) {
        store.remove(token);
    }
}
