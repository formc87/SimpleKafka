package com.example.portal.service;

import com.example.portal.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public boolean emailExists(String email) {
        return users.containsKey(email.toLowerCase());
    }

    public void register(User user) {
        users.put(user.getEmail().toLowerCase(), user);
    }

    public Optional<User> authenticate(String email, String password) {
        return Optional.ofNullable(users.get(email.toLowerCase()))
                .filter(user -> user.getPassword().equals(password));
    }
}
