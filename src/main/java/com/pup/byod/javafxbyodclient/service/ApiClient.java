package com.pup.byod.javafxbyodclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pup.byod.javafxbyodclient.session.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private static ApiClient instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .cookieHandler(new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private String getFullUrl(String path) {
        String base = SessionManager.getInstance().getBaseUrl();
        // Remove duplicate slashes if any
        if (base.endsWith("/") && path.startsWith("/")) {
            return base + path.substring(1);
        }
        return base + path;
    }

    public <T> T get(String path, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(path)))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(path)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) throws Exception {
        String jsonBody = body != null ? objectMapper.writeValueAsString(body) : "";
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(path)))
                .header("Accept", "application/json");

        if (body != null) {
            builder.header("Content-Type", "application/json")
                   .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            builder.PUT(HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, responseType);
    }

    public <T> T postMultipart(String path, java.io.File file, Class<T> responseType) throws Exception {
        String boundary = "JavaFXBYODClientBoundary" + System.currentTimeMillis();
        
        byte[] before = ("--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: text/csv\r\n\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        
        byte[] after = ("\r\n--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        byte[] payload = new byte[before.length + fileBytes.length + after.length];
        System.arraycopy(before, 0, payload, 0, before.length);
        System.arraycopy(fileBytes, 0, payload, before.length, fileBytes.length);
        System.arraycopy(after, 0, payload, before.length + fileBytes.length, after.length);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(path)))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
                .build();
                
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, responseType);
    }

    public void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(path)))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Delete request failed with code " + response.statusCode() + ": " + response.body());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) throws Exception {
        int status = response.statusCode();
        String body = response.body();

        if (status >= 200 && status < 300) {
            if (responseType == Void.class || body == null || body.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(body, responseType);
        } else {
            // Extract error message if JSON, else throw general
            String errMsg = null;
            try {
                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(body);
                if (root.has("message")) {
                    errMsg = root.get("message").asText();
                } else {
                    errMsg = body;
                }
            } catch (Exception e) {
                errMsg = body;
            }
            if (errMsg == null || errMsg.trim().isEmpty()) {
                errMsg = "Request failed with HTTP status code " + status;
            }
            throw new RuntimeException(errMsg);
        }
    }
}
