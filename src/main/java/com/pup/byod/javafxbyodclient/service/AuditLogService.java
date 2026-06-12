package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.AuditLog;
import java.util.Arrays;
import java.util.List;

public class AuditLogService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<AuditLog> getAllAuditLogs() throws Exception {
        AuditLog[] logs = apiClient.get("/api/v1/audit-logs?limit=500", AuditLog[].class);
        return Arrays.asList(logs);
    }
}
