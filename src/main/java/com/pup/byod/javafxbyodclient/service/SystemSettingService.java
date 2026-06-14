package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.SystemSetting;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSettingService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<SystemSetting> getAllSettings() throws Exception {
        SystemSetting[] settings = apiClient.get("/api/v1/settings", SystemSetting[].class);
        return Arrays.asList(settings);
    }

    public void updateSetting(String key, String value) throws Exception {
        Map<String, Object> body = new HashMap<>();
        if (SessionManager.getInstance().getCurrentUser() != null) {
            body.put("actingUserId", SessionManager.getInstance().getCurrentUser().getUserId());
        }
        body.put("settingValue", value);
        apiClient.put("/api/v1/settings/" + key, body, Void.class);
    }
}
