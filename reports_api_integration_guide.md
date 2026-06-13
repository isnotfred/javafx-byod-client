# Reports API Integration Guide for Frontend Developers

All backend business reports are exposed under the `/reports` path. These routes are restricted to users with the **`ADMIN`** and **`SUPER_ADMIN`** roles.

This guide details the backend contract, identifies mismatches in the current JavaFX client implementation, and explains how to integrate the remaining reports.

---

## 1. Quick Summary of API Endpoints

| Endpoint | HTTP Method | Expected Query Parameters | Response DTO Type | Status in Client |
| :--- | :--- | :--- | :--- | :--- |
| `/reports/daily-traffic` | `GET` | `date` (yyyy-MM-dd), `studentId` (opt), `deviceType` (opt), `status` (opt) | `DailyTrafficRow[]` | **Mismatch** (Client sends `from`/`to`) |
| `/reports/monthly-traffic` | `GET` | `year` (int, opt), `month` (int, opt) | `MonthlyTrafficRow[]` | **Mismatch** (Client sends `from`/`to`) |
| `/reports/pending-registrations` | `GET` | None | `PendingRegistrationRow[]` | **Not Implemented** |
| `/reports/active-devices` | `GET` | None | `ActiveDeviceRow[]` | **Not Implemented** |
| `/reports/device-frequency` | `GET` | `from` (yyyy-MM-dd, req), `to` (yyyy-MM-dd, req) | `DeviceFrequencyRow[]` | **Not Implemented** |
| `/reports/incidents` | `GET` | `from` (yyyy-MM-dd, req), `to` (yyyy-MM-dd, req) | `IncidentOverrideRow[]` | **Implemented** |

---

## 2. API Schema Details

### 2.1 Daily Device Traffic Summary
* **Endpoint:** `GET /reports/daily-traffic`
* **Backend Controller Method:** `getDailyTraffic(...)`
* **Response Body (`200 OK`):**
```json
[
  {
    "logId": 102,
    "eventType": "entry",
    "eventTime": "2026-06-09T08:00:00+08:00",
    "autoExit": false,
    "logoutType": null,
    "notes": "Regular entry",
    "deviceId": 12,
    "deviceName": "My Work Laptop",
    "serialNumber": "SN-DELL-12345",
    "deviceType": "Personal Computers",
    "registrationStatus": "approved",
    "studentId": "2021-10023",
    "studentName": "Juan Dela Cruz",
    "courseYearLevel": "BSIT 3-1",
    "handledByName": "John Doe"
  }
]
```

### 2.2 Monthly Device Traffic Summary
* **Endpoint:** `GET /reports/monthly-traffic`
* **Backend Controller Method:** `getMonthlyTraffic(...)`
* **Response Body (`200 OK`):**
```json
[
  {
    "reportMonth": "2026-06-01",
    "deviceCategory": "Personal Computers",
    "studentId": "2021-10023",
    "studentName": "Juan Dela Cruz",
    "courseYearLevel": "BSIT 3-1",
    "entryCount": 18,
    "exitCount": 18,
    "totalEvents": 36
  }
]
```

### 2.3 Pending Registration Report
* **Endpoint:** `GET /reports/pending-registrations`
* **Backend Controller Method:** `getPendingRegistrations()`
* **Response Body (`200 OK`):**
```json
[
  {
    "deviceId": 13,
    "studentId": "2021-10023",
    "studentName": "Juan Dela Cruz",
    "courseYearLevel": "BSIT 3-1",
    "deviceName": "iPad Pro",
    "brand": "Apple",
    "model": "M2 Pro",
    "serialNumber": "SN-APPLE-88392",
    "deviceType": "Components & Peripherals",
    "devicePurpose": "Academic BYOD",
    "imagePath": "/images/devices/13_ipad.png",
    "submittedAt": "2026-06-09T10:50:00+08:00",
    "submittedBy": "John Doe"
  }
]
```

### 2.4 Active Devices on Campus
* **Endpoint:** `GET /reports/active-devices`
* **Backend Controller Method:** `getActiveDevices()`
* **Response Body (`200 OK`):**
```json
[
  {
    "deviceId": 12,
    "studentId": "2021-10023",
    "studentName": "Juan Dela Cruz",
    "courseYearLevel": "BSIT 3-1",
    "deviceName": "My Work Laptop",
    "serialNumber": "SN-DELL-12345",
    "deviceType": "Personal Computers",
    "brand": "Dell",
    "model": "XPS 15",
    "enteredAt": "2026-06-09T08:00:00+08:00"
  }
]
```

### 2.5 Device Frequency Report
* **Endpoint:** `GET /reports/device-frequency`
* **Backend Controller Method:** `getDeviceFrequency(...)`
* **Response Body (`200 OK`):**
```json
[
  {
    "deviceId": 12,
    "deviceName": "My Work Laptop",
    "serialNumber": "SN-DELL-12345",
    "deviceType": "Personal Computers",
    "brand": "Dell",
    "model": "XPS 15",
    "studentId": "2021-10023",
    "studentName": "Juan Dela Cruz",
    "courseYearLevel": "BSIT 3-1",
    "entryCount": 18,
    "exitCount": 18,
    "firstSeen": "2026-06-01T08:00:00+08:00",
    "lastSeen": "2026-06-09T08:00:00+08:00"
  }
]
```

### 2.6 Incident / Override Report
* **Endpoint:** `GET /reports/incidents`
* **Backend Controller Method:** `getIncidents(...)`
* **Response Body (`200 OK`):**
```json
[
  {
    "auditId": 25,
    "actionType": "DEVICE_REJECTED",
    "targetTable": "devices",
    "targetId": "14",
    "oldValues": "{\"registrationStatus\":\"pending\"}",
    "newValues": "{\"registrationStatus\":\"rejected\"}",
    "ipAddress": "192.168.1.5",
    "createdAt": "2026-06-09T11:05:00+08:00",
    "performedBy": "Jane Smith",
    "performerRole": "admin"
  }
]
```

---

## 3. Client Implementation Mismatches

The current client-side service [ReportService.java](file:///d:/IdeaProjects/javafx-byod-client/src/main/java/com/pup/byod/javafxbyodclient/service/ReportService.java) has parameter mismatches for **Daily** and **Monthly** traffic reports.

### Mismatch 1: Daily Traffic Request Parameters
* **What the client does:** The client calls `/reports/daily-traffic?from={startDate}&to={endDate}`.
* **What the backend expects:** The backend expects a single `date` parameter (e.g. `/reports/daily-traffic?date=2026-06-13`). It does **not** recognize `from` and `to`. As a result, the backend defaults to today's date and ignores the date range.
* **Action Required:** Update the client to pass the `date` parameter or adjust the UI to request single-day summaries for daily logs.

### Mismatch 2: Monthly Traffic Request Parameters
* **What the client does:** The client calls `/reports/monthly-traffic?from={startDate}&to={endDate}`.
* **What the backend expects:** The backend expects integer parameters `year` and `month` (e.g. `/reports/monthly-traffic?year=2026&month=6`). It does **not** recognize `from` and `to`.
* **Action Required:** Extract the year and month integers from the selected client date picker and pass them as query parameters.

---

## 4. How to Update `ReportService.java` on the Client

To align with the backend API, update [ReportService.java](file:///d:/IdeaProjects/javafx-byod-client/src/main/java/com/pup/byod/javafxbyodclient/service/ReportService.java) with the correct signatures and implement the missing endpoints:

```java
package com.pup.byod.javafxbyodclient.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final ApiClient apiClient = ApiClient.getInstance();

    // 1. Daily Traffic Report (Accepts single date)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDailyTrafficReport(String dateString, String studentId, String deviceType, String status) throws Exception {
        StringBuilder url = new StringBuilder("/reports/daily-traffic?date=").append(URLEncoder.encode(dateString, StandardCharsets.UTF_8));
        if (studentId != null) url.append("&studentId=").append(URLEncoder.encode(studentId, StandardCharsets.UTF_8));
        if (deviceType != null) url.append("&deviceType=").append(URLEncoder.encode(deviceType, StandardCharsets.UTF_8));
        if (status != null) url.append("&status=").append(URLEncoder.encode(status, StandardCharsets.UTF_8));
        
        Map[] reports = apiClient.get(url.toString(), Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 2. Monthly Traffic Report (Accepts year and month)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMonthlyTrafficReport(int year, int month) throws Exception {
        Map[] reports = apiClient.get("/reports/monthly-traffic?year=" + year + "&month=" + month, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 3. Pending Registrations
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPendingRegistrationReport() throws Exception {
        Map[] reports = apiClient.get("/reports/pending-registrations", Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 4. Active Devices On Campus
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getActiveDevicesReport() throws Exception {
        Map[] reports = apiClient.get("/reports/active-devices", Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 5. Device Frequency Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDeviceFrequencyReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/reports/device-frequency?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 6. Incidents Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getIncidentsReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/reports/incidents?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }
}
```

---

## 5. Suggested Visual Formats (Graphs) & JavaFX Implementation

Below are the recommended visualization models for each report, including details on how to map the JSON payload fields to JavaFX `Chart` components.

### 5.1 Hourly Ingress/Egress Load (Daily Traffic Report)
* **Visual Format:** **LineChart** or **Grouped BarChart**
* **Goal:** Visualize peak entry/exit times to optimize security guard scheduling at the campus gates.
* **Mapping Schema:**
  * **X-Axis:** `CategoryAxis` representing hours of the day (e.g., `"08:00 AM"`, `"09:00 AM"`, etc. derived by parsing the `eventTime` hour).
  * **Y-Axis:** `NumberAxis` representing count of occurrences.
  * **Series 1 (Entries):** `XYChart.Series<String, Number>` where `eventType` equals `"entry"`.
  * **Series 2 (Exits):** `XYChart.Series<String, Number>` where `eventType` equals `"exit"`.

**JavaFX Implementation Example:**
```java
LineChart<String, Number> loadChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
loadChart.setTitle("Hourly Campus Gate Load");

XYChart.Series<String, Number> entriesSeries = new XYChart.Series<>();
entriesSeries.setName("Entries");

// Map eventTime to hour string groups (e.g. "08:00") and count
Map<String, Long> entryCountsByHour = records.stream()
    .filter(r -> "entry".equals(r.get("eventType")))
    .collect(Collectors.groupingBy(
        r -> parseHourFromTime(String.valueOf(r.get("eventTime"))),
        Collectors.counting()
    ));

entryCountsByHour.forEach((hour, count) -> {
    entriesSeries.getData().add(new XYChart.Data<>(hour, count));
});

loadChart.getData().add(entriesSeries);
```

### 5.2 Monthly Category Traffic Share (Monthly Traffic Report)
* **Visual Format:** **PieChart** or **Stacked BarChart**
* **Goal:** Understand which device classes are generating the most campus bandwidth/footprint traffic month-over-month.
* **Mapping Schema:**
  * **X-Axis / Category:** `deviceCategory` (e.g., `"Personal Computers"`, `"Mobile Phones"`).
  * **Values:** Aggregated sum of `totalEvents` (or `entryCount`) for each category.

**JavaFX Implementation Example:**
```java
PieChart categoryShareChart = new PieChart();
categoryShareChart.setTitle("Monthly Device Category Share");

// Group total events by device category
Map<String, Integer> categoryEvents = new HashMap<>();
for (Map<String, Object> record : records) {
    String category = String.valueOf(record.get("deviceCategory"));
    int total = ((Number) record.get("totalEvents")).intValue();
    categoryEvents.put(category, categoryEvents.getOrDefault(category, 0) + total);
}

categoryEvents.forEach((category, total) -> {
    categoryShareChart.getData().add(new PieChart.Data(category + " (" + total + ")", total));
});
```

### 5.3 Pending Approvals by Category (Pending Registrations)
* **Visual Format:** **Horizontal BarChart** or **PieChart**
* **Goal:** Display outstanding workload backlogs for administrators.
* **Mapping Schema:**
  * **Category (Y-Axis / Pie Slice):** `deviceType` or `submittedBy` (Guard name).
  * **Value:** Number of pending registrations in that grouping.

### 5.4 Live Campus Device Inventory (Active Devices)
* **Visual Format:** **Doughnut / PieChart**
* **Goal:** Live operational status dashboard showing what device types are on campus right now.
* **Mapping Schema:**
  * **Slices:** `deviceType` (category value).
  * **Value:** Sum/count of devices currently checked in.

### 5.5 Top 10 Power Users / Most Frequent Devices (Device Frequency Report)
* **Visual Format:** **Horizontal BarChart**
* **Goal:** Highlight high-frequency bring-in assets (e.g., student laptops brought on campus daily).
* **Mapping Schema:**
  * **Y-Axis (Categories):** Combined label: `studentName` + `deviceName` (e.g., `"Juan Dela Cruz (XPS 15)"`).
  * **X-Axis (Values):** `entryCount`.

### 5.6 Security Audits & Incidents (Incident / Override Report)
* **Visual Format:** **PieChart**
* **Goal:** Keep track of the ratio of critical events (such as deactivations or rejections) to trace potential issues.
* **Mapping Schema:**
  * **Slices:** `actionType` (e.g. `"DEVICE_REJECTED"`, `"DEVICE_DEACTIVATED"`).
  * **Value:** Count of audit log entries.

