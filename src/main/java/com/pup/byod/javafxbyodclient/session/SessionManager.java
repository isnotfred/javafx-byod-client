package com.pup.byod.javafxbyodclient.session;

import com.pup.byod.javafxbyodclient.model.User;

public class SessionManager {
    private static SessionManager instance;
    
    private User currentUser;
    private String baseUrl = "https://java-byod-backend-production.up.railway.app"; // Default local development URL

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
