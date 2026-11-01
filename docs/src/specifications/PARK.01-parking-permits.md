# PARK.01 · Parking permit portal

> **PARK.01** · state **Implemented** · traces from [PARK-US-001](../user-stories/PARK-US-001.md)
> · source `backend/src/main/java/ee/taltech/ite4120/parking/`, `backend/src/main/resources/tea/db/changelog/`, `frontend/src/pages/`

## Description

A portal of parking permits: create, search, view, update, retire (soft delete). Each permit belongs to a person and a permit type. Someone grants it, for a start date and an end date, with a short reason. The id is assigned by the database.

The PostgreSQL schema is still named `tea`. The first changesets already shipped under that name and are not edited. `04-parking-permit.sql` renames the tables and replaces the tea columns.

Example record. The ids in this body are assigned by the database; the seed below names the permit type, the holder, and the granter instead.

```json
{
  "permitTypeId": 1,
  "holderId": 1,
  "grantedById": 5,
  "startDate": "2026-09-01",
  "endDate": "2027-08-31",
  "description": "Lives at Akadeemia tee 15 and needs a resident bay."
}
```

## Example data

Demo rows load only when Liquibase runs with context `demo` (`spring.liquibase.contexts` in `application.yml`). `90-demo-data.sql` and `91-demo-data.sql` still insert the five people and the old tea rows; those changesets are not edited. `04-parking-permit.sql` deletes the tea rows, renames the tables, and seeds the permit types. `92-demo-permits.sql` (`ite4120:tea-92-demo-permits`, `context:demo`) then inserts the permits.

The permit seed stores foreign keys. It resolves `permit_type_id` by type name, `holder_id` by the holder's email, and `granted_by_id` by the granter's email, among active rows.

| Name | Email | Changeset |
| --- | --- | --- |
| Mari Tamm | mari.tamm@example.com | `90-demo-data.sql` |
| Jaan Kask | jaan.kask@example.com | `91-demo-data.sql` |
| Liis Rebane | liis.rebane@example.com | `91-demo-data.sql` |
| Karl Põder | karl.poder@example.com | `91-demo-data.sql` |
| Eva Saar | eva.saar@example.com | `91-demo-data.sql` |

Permit types, seeded by `04-parking-permit.sql`: Resident, Visitor, Staff, Service.

| Holder | Type | Granted by | Start | End | Why | Changeset |
| --- | --- | --- | --- | --- | --- | --- |
| Mari Tamm | Resident | Eva Saar | 2026-09-01 | 2027-08-31 | Lives at Akadeemia tee 15 and needs a resident bay. | `92-demo-permits.sql` |
| Jaan Kask | Staff | Eva Saar | 2026-01-06 | 2026-12-31 | ICT faculty staff, daytime parking by the main building. | `92-demo-permits.sql` |
| Liis Rebane | Visitor | Jaan Kask | 2026-03-02 | 2026-03-06 | Guest lecture week, visitor bay at the library. | `92-demo-permits.sql` |
| Karl Põder | Service | Eva Saar | 2026-04-01 | 2026-10-31 | Maintenance contractor working on the heating plant. | `92-demo-permits.sql` |
| Eva Saar | Staff | Jaan Kask | 2026-01-06 | 2026-12-31 | Campus services staff who also grants other permits. | `92-demo-permits.sql` |
| Mari Tamm | Visitor | Karl Põder | 2026-11-10 | 2026-11-12 | Extra visitor permit for a family weekend on campus. | `92-demo-permits.sql` |
| Liis Rebane | Resident | Eva Saar | 2026-08-01 | 2027-07-31 | Moved into student housing and needs an overnight bay. | `92-demo-permits.sql` |
| Karl Põder | Resident | Jaan Kask | 2026-02-01 | 2027-01-31 | Resident bay next to the sports hall. | `92-demo-permits.sql` |

## Data model

| Table | Field | Type | Rules |
| --- | --- | --- | --- |
| `tea.person` | `id` PK | bigint | from `core.seq_id`. A person who holds a permit, and who may grant one |
| | `name` | text | required |
| | `email` | text | required; unique among active rows |
| | `sys_*` | — | platform columns, trigger-managed |
| `tea.permit_type` | `id` PK | bigint | from `core.seq_id` |
| | `name` | text | required; unique among active rows. Seeded: Resident, Visitor, Staff, Service |
| `tea.permits` | `id` PK | bigint | from `core.seq_id` |
| | `permit_type_id` | bigint | required; FK → permit_type |
| | `holder_id` | bigint | required; FK → person. The person the permit is for |
| | `granted_by_id` | bigint | required; FK → person. Who granted the access |
| | `start_date`, `end_date` | date | required; end is not before start |
| | `description` | text | required; 1 to 200 characters. Why the parking was granted |

## API

| Method + path | Does | Failure answers |
| --- | --- | --- |
| `GET /api/permits` | paged search: `textContains` (description, holder name, or granter name), `permitTypeId`, `holderId`, `limit`, `offset`, `sort` | — |
| `GET /api/permits/{id}` | one active permit | 404 |
| `POST /api/permits` | grant; **201 + Location** | 400 validation |
| `PUT /api/permits/{id}` | update | 400 · 404 |
| `DELETE /api/permits/{id}` | retire (soft) — **204** | 404 |
| `GET /api/permit-types` | permit types | — |
| `POST /api/permit-types` | add a permit type | 409 duplicate name |
| `GET /api/persons` | people | — |
| `POST /api/persons` | add a person | 409 duplicate email |

Every error is `application/problem+json`.

## UI

| Screen | Element | Component | Rules |
| --- | --- | --- | --- |
| Permit list `/permits` | table | `ResourceList` | first column is the row number, `1.` `2.` `3.`, in the order the rows are shown; holder links to the record; type, start, end, granted by, why |
| Add permit `/permits/new` | form | antd `Form` | holder, permit type, granted by, start, end, why |
| Permit page `/permits/{id}` | view / edit | `ResourceForm` | same fields; Retire is the soft delete |

## Business tests

1. **End is not before start.** Given a permit whose end date is the day before it starts, when it is granted, then the grant is refused with "endDate must not be before startDate".
2. **The reason is at most 200 characters.** Given a description of 201 characters, when it is granted, then the grant is refused with "description must be at most 200 characters".
3. **An email is unique among active people.** Given an active person with an email, when a second person claims it, then the registration conflicts.
