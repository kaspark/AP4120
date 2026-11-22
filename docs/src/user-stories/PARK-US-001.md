# PARK-US-001 · Issue parking permits

> **PARK-US-001** · state **Implemented**

**As** a campus parking clerk
**I want** a register of who may park, which kind of permit they have, and who granted it
**so that** I can see why the access was given and when it starts and ends.

## Context

Three tables: the people who hold permits, the permit types (resident, visitor, staff, service), and each permit. A permit records the holder, the type, the license plate, the start and end dates, who granted it, and a short reason. The demo portal opens with the five people and eight permits listed in [PARK.01](../specifications/PARK.01-parking-permits.md#example-data).

Out of scope: payment, licence-plate recognition, and authentication beyond the course mock sign-in. The clerk types the plate; the portal does not read it from a camera.

## Acceptance criteria

- A permit cannot end before it starts. The form shows that on the end date, the same way it shows a missing license plate.
- The reason is at most 200 characters.
- A license plate is required, and it is at most 20 characters.
