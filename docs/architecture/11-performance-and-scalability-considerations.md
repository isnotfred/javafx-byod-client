# 11 - Performance And Scalability Considerations

## Gate Search Performance

Gate operations must remain fast during peak traffic. Highest-priority lookup paths are:

- `students.student_id`
- `students.last_name`, `students.first_name`
- `devices.serial_number`
- latest `device_logs` row per device
- `v_device_campus_status` for approved active devices

## Database Indexes

The uploaded schema already defines indexes for:

- Student name and status.
- Device owner, serial number, registration status, and pending queue.
- Event request owner/status/date range.
- Event request line items by parent request.
- Latest event-device log by manifest row.
- Latest device log lookup.
- Open entry lookup for automatic logout.
- Audit lookup by user/time, target, and timestamp.

## Query Strategy

- Use `v_pending_devices` for approval queues.
- Use `v_active_event_requests` for event request queues.
- Use `v_event_device_status` for event-device guard and reconciliation views.
- Use `v_device_campus_status` or equivalent latest-log query for active-device screens.
- Apply report filters in SQL.
- Avoid loading all historical logs into JavaFX tables.
- Use pagination or row limits for large admin queries once API response shape is defined.

## Backend Scalability

- Railway backend can be scaled independently from JavaFX clients.
- Connection pool size must match Railway PostgreSQL limits.
- High-write tables `device_logs` and `audit_logs` use the schema autovacuum tuning.
- Report endpoints should avoid long-running unfiltered queries.

## Frontend Considerations

- Keep guard screens focused on search and entry/exit actions.
- Defer report-heavy workflows to admin screens.
- Load images lazily after the image storage policy is finalized.
