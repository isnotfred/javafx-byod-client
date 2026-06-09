package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperAdminService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<User> getAllUsers() throws Exception {
        // Fetches standard user list
        User[] users = apiClient.get("/api/v1/users", User[].class);
        return Arrays.asList(users);
    }

    public User onboardUser(int actingUserId, String fullName, String email, String role) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("actingUserId", actingUserId);
        body.put("fullName", fullName);
        body.put("email", email);
        body.put("role", role);
        return apiClient.post("/super-admin/users", body, User.class);
    }

    public User updateUser(int userId, int actingUserId, String fullName, String status) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("actingUserId", actingUserId);
        body.put("fullName", fullName);
        body.put("status", status);
        return apiClient.put("/super-admin/users/" + userId, body, User.class);
    }

    public void deactivateUser(int userId, int actingUserId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("actingUserId", actingUserId);
        apiClient.put("/super-admin/users/" + userId + "/deactivate", body, Void.class);
    }

    public void changeRole(int userId, int actingUserId, String role) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("actingUserId", actingUserId);
        body.put("role", role);
        apiClient.put("/super-admin/users/" + userId + "/role", body, Void.class);
    }
}
