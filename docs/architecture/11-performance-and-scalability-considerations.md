# 11 - Performance and Scalability Considerations

## Gate Search Performance

Gate search must be fast enough for peak entry and exit periods. The highest-priority lookup fields are student ID, student name, device serial number, and asset tag.

## Database Indexes

Recommended indexes:

- `students.student_id`
- `students.last_name`, `students.first_name`
- `devices.serial_number`
- `devices.asset_tag`
- `devices.registration_status`, `devices.campus_status`, `devices.device_status`
- `device_logs.device_id`
- `device_logs.ingress_time`
- `device_logs.egress_time`

## Query Strategy

- Filter reports by date range before loading rows.
- Avoid loading all historical logs into UI tables.
- Use pagination or table limits for large result sets.
- Load detail records only when a user selects a row.
- Use specific report queries instead of one large general query.

## Local Database Considerations

A single local database is simpler but limits multi-gate use. A centralized campus database is better if multiple desktops need shared real-time records. **Needs Team Confirmation.**

## Peak-Hour Considerations

- Keep guard dashboard focused on search and log actions.
- Avoid slow report generation on guard screens.
- Validate in memory before database write when possible.
- Keep device image loading optional and lightweight.

