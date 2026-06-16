package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public User login(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        
        User user = apiClient.post("/api/v1/auth/login", credentials, User.class);
        SessionManager.getInstance().setCurrentUser(user);
        return user;
    }

    public void logout() throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            Map<String, Integer> logoutBody = new HashMap<>();
            logoutBody.put("userId", currentUser.getUserId());
            apiClient.post("/api/v1/auth/logout", logoutBody, Void.class);
        }
        SessionManager.getInstance().logout();
    }

    public void forgotPassword(String email) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        apiClient.post("/api/v1/auth/forgot-password", body, Void.class);
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("newPassword", newPassword);
        apiClient.post("/api/v1/auth/reset-password", body, Void.class);
    }

    public void updatePassword(int userId, String currentPassword, String newPassword) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("newPassword", newPassword);
        apiClient.put("/api/v1/users/" + userId + "/profile/password", body, Void.class);
    }

    public User updateUsername(int userId, String newUsername) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", newUsername);
        User updated = apiClient.put("/api/v1/users/" + userId + "/profile/username", body, User.class);
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUserId() == userId) {
            currentUser.setUsername(updated.getUsername());
        }
        return updated;
    }
}
