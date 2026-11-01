# PARK.01 · Parking permit portal

> **PARK.01** · state **Implemented** · traces from [PARK-US-001](../user-stories/PARK-US-001.md)
> · source `backend/src/main/java/ee/taltech/ite4120/parking/`, `backend/src/main/resources/tea/db/changelog/`, `frontend/src/pages/`

This document is the product contract. Copied together with the other Markdown
in this repository — the user story, the three manuals, and the Liquibase
guide — it is enough to rebuild the portal. Do not add fields, paths, or
messages that are not written here. The manuals describe the platform patterns
(layers, formatted SQL, `ResourceList`, `ResourceForm`, `application/problem+json`).
Where a manual still uses the name `animals`, this specification wins.

## Description

A portal of parking permits: create, search, view, update, retire (soft delete). Each permit belongs to a person and a permit type. Someone grants it, for a start date and an end date, with a short reason. The id is assigned by the database.

Out of scope: payment, licence plates, and authentication beyond the course mock sign-in (`Authorization: Bearer <username>` when mock auth is on).

The Java package is `ee.taltech.ite4120.parking` (controller → service → repository). The React pages are `PermitList`, `PermitCreate`, and `PermitDetail`. The browser title and the shell name are **Parking permits**.

The PostgreSQL schema is still named `tea`. Build the tables below in that schema. If a database already contains the shipped tea changesets (`tea.tea_user`, `tea.category`, `tea.tea`), do not edit those changesets. Append one changeset that reshapes them into the target schema, then a `context:demo` changeset for the permit rows. A database that has no tea history is created directly as the target schema.

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

A saved permit also returns the joined names and the platform columns: `permitTypeName`, `holderName`, `grantedByName`, `sysStatus`, `sysVersion`, `sysCreatedAt`, `sysCreatedBy`, `sysModifiedAt`, `sysModifiedBy`. Dates are `YYYY-MM-DD`. The holder and the granter may be the same person.

## Example data

Demo rows load only when Liquibase runs with context `demo`. People are inserted before the permits that name them. The permit seed stores foreign keys: `permit_type_id` by type name, `holder_id` by the holder's email, `granted_by_id` by the granter's email, among active rows only.

| Name | Email |
| --- | --- |
| Mari Tamm | mari.tamm@example.com |
| Jaan Kask | jaan.kask@example.com |
| Liis Rebane | liis.rebane@example.com |
| Karl Põder | karl.poder@example.com |
| Eva Saar | eva.saar@example.com |

Permit types, always seeded (not only under `demo`): Resident, Visitor, Staff, Service.

| Holder | Type | Granted by | Start | End | Why |
| --- | --- | --- | --- | --- | --- |
| Mari Tamm | Resident | Eva Saar | 2026-09-01 | 2027-08-31 | Lives at Akadeemia tee 15 and needs a resident bay. |
| Jaan Kask | Staff | Eva Saar | 2026-01-06 | 2026-12-31 | ICT faculty staff, daytime parking by the main building. |
| Liis Rebane | Visitor | Jaan Kask | 2026-03-02 | 2026-03-06 | Guest lecture week, visitor bay at the library. |
| Karl Põder | Service | Eva Saar | 2026-04-01 | 2026-10-31 | Maintenance contractor working on the heating plant. |
| Eva Saar | Staff | Jaan Kask | 2026-01-06 | 2026-12-31 | Campus services staff who also grants other permits. |
| Mari Tamm | Visitor | Karl Põder | 2026-11-10 | 2026-11-12 | Extra visitor permit for a family weekend on campus. |
| Liis Rebane | Resident | Eva Saar | 2026-08-01 | 2027-07-31 | Moved into student housing and needs an overnight bay. |
| Karl Põder | Resident | Jaan Kask | 2026-02-01 | 2027-01-31 | Resident bay next to the sports hall. |

On a database that already ran the tea demo changesets, those changesets keep the five people and also insert tea stock. The reshape changeset deletes the tea stock and the old classifications (Black, Green, Herbal, Oolong), then the demo permit changeset inserts the eight rows above. The people are not deleted.

## Data model

| Table | Field | Type | Rules |
| --- | --- | --- | --- |
| `tea.person` | `id` PK | bigint | from `core.seq_id`. A person who holds a permit, and who may grant one |
| | `name` | text | required, at most 255 characters |
| | `email` | text | required, a valid email, at most 255 characters; unique among active rows, case-insensitive |
| | `sys_*` | — | `sys_status`, `sys_version`, `sys_created_at`, `sys_created_by`, `sys_modified_at`, `sys_modified_by`. The platform trigger fills them. Application code does not write them |
| `tea.permit_type` | `id` PK | bigint | from `core.seq_id` |
| | `name` | text | required, at most 255 characters; unique among active rows, case-insensitive. Seeded: Resident, Visitor, Staff, Service |
| | `sys_*` | — | same platform columns |
| `tea.permits` | `id` PK | bigint | from `core.seq_id` |
| | `permit_type_id` | bigint | required; FK → `tea.permit_type`. Active rows only |
| | `holder_id` | bigint | required; FK → `tea.person`. The person the permit is for |
| | `granted_by_id` | bigint | required; FK → `tea.person`. Who granted the access |
| | `start_date`, `end_date` | date | required; `end_date >= start_date` |
| | `description` | text | required; 1 to 200 characters. Why the parking was granted |
| | `sys_*` | — | same platform columns. Retire sets `sys_status` to `C` |

Call `core.create_table_metadata` on each business table. Partial indexes, `where (sys_status = 'A')`:

- unique on `lower(person.email)`
- unique on `lower(permit_type.name)`
- `permits (permit_type_id)`, `permits (holder_id)`, `permits (granted_by_id)`

Checks: `end_date >= start_date`, and `char_length(description) between 1 and 200`.

Target shape, for a database that is not migrating an older tea table:

```sql
create table tea.person (
    id                  bigint default nextval('core.seq_id') not null,
    name                text not null,
    email               text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint person_pk primary key (id)
);

create table tea.permit_type (
    id                  bigint default nextval('core.seq_id') not null,
    name                text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint permit_type_pk primary key (id)
);

create table tea.permits (
    id                  bigint default nextval('core.seq_id') not null,
    permit_type_id      bigint not null,
    holder_id           bigint not null,
    granted_by_id       bigint not null,
    start_date          date not null,
    end_date            date not null,
    description         text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint permit_pk primary key (id),
    constraint permit_type_fk foreign key (permit_type_id) references tea.permit_type (id),
    constraint permit_holder_fk foreign key (holder_id) references tea.person (id),
    constraint permit_granted_by_fk foreign key (granted_by_id) references tea.person (id),
    constraint permit_dates_chk check (end_date >= start_date),
    constraint permit_description_chk check (char_length(description) between 1 and 200)
);
```

List and load join `permit_type.name` as `permit_type_name`, the holder's `person.name` as `holder_name`, and the granter's `person.name` as `granted_by_name`. Only `sys_status = 'A'` is visible.

## API

Every error is `application/problem+json`. 400 is the caller's fault, 404 is missing, 409 is a conflict, 204 is an empty success. A list answer is `{ "data": [ ... ], "meta": { "total", "offset", "pages", "itemsPerPage" } }`.

| Method + path | Body / query | Success | Failure detail |
| --- | --- | --- | --- |
| `GET /api/permits` | `textContains`, `permitTypeId`, `holderId`, `limit`, `offset`, `sort` | 200, the page of active permits | 400 `unknown sort key` when `sort` is not one of `startDate`, `endDate`, `holderName` |
| `GET /api/permits/{id}` | — | 200, one active permit | 404 `permit {id} not found` |
| `POST /api/permits` | the example record | **201**, `Location: /api/permits/{id}`, body is the saved permit | 400, see validation |
| `PUT /api/permits/{id}` | the same body | 200, the updated permit | 400 · 404 `permit {id} not found` |
| `DELETE /api/permits/{id}` | — | **204** | 404 `permit {id} not found` |
| `GET /api/permit-types` | — | 200, active types ordered by name | — |
| `POST /api/permit-types` | `{ "name": "Contractor" }` | **201**, `Location: /api/permit-types/{id}` | 400 `name is required` · 409 `an active permit type named {name} already exists` |
| `GET /api/persons` | — | 200, active people ordered by name | — |
| `GET /api/persons/{id}` | — | 200, one active person | 404 `person {id} not found` |
| `POST /api/persons` | `{ "name": "Ada", "email": "ada@example.com" }` | **201**, `Location: /api/persons/{id}` | 400 `name is required` or `email is required` · 409 `an active person with email {email} already exists` |

`textContains` matches, case-insensitively, the description, the holder name, or the granter name. `permitTypeId` and `holderId` filter the corresponding columns. Unknown ids on a write are 400:

- `unknown permitTypeId: {id}`
- `unknown holderId: {id}`
- `unknown grantedById: {id}`

Permit validation, in this order, before the database constraints:

- `description is required` when it is missing or blank
- `description must be at most 200 characters`
- `startDate is required`
- `endDate is required`
- `endDate must not be before startDate`

The request body rejects a missing field, a description longer than 200 characters, a person name or email longer than 255 characters, and an email that is not an email address. Those answers are 400 with title `Validation failed`.

## UI

`/` redirects to `/permits`. The old tea addresses redirect as well: `/teas` and `/teas/` to `/permits`, `/teas/new` to `/permits/new`, `/teas/{id}` to `/permits/{id}`. When the built UI is served by nginx on port 80 behind a published port, those redirects are relative (`Location: /permits`), so the published port is kept.

| Screen | Element | Component | Rules |
| --- | --- | --- | --- |
| Permit list `/permits` | table | `ResourceList` | title **Permits**. First column **No.** is the row number `1.` `2.` `3.` in the order the loaded rows are shown, and it cannot be hidden. **Holder** links to `/permits/{id}`. Then **Type** (`AppTag`), **Start**, **End**, **Granted by**, **Why**. Search placeholder `Holder, granter, or reason…`, debounced 250 ms, sent as `textContains` with `limit=50`. **Add permit** goes to `/permits/new`. The detail drawer shows holder, type, granter, the two dates, and the reason |
| Add permit `/permits/new` | form | antd `Form` | **Holder**, **Permit type**, **Granted by** (antd `Select`, people labelled `name · email`), **Start date**, **End date** (antd `DatePicker`; the end date notes that it must not be before the start), **Why** (`Input.TextArea`, required, max 200). Cancel returns to `/permits`. Success returns to the list |
| Permit page `/permits/{id}` | view / edit | `ResourceForm` | entity label **Permit**, name is the holder, subtitle and chip are the type. Fields: holder, permit type, granted by, start date, end date, why. Dates are a `custom` field that converts `YYYY-MM-DD` to dayjs and back (`DateField`). Why is a `custom` textarea, max 200. **Retire** soft-deletes and returns to the list. Confirm: `Retire this permit? It stays in the database with status retired.` The sidebar shows created at / by, modified at / by, and version |

A missing permit on the record page reports the error and returns to the list.

## Business tests

These three are the executable tests. They call the service, and they also prove the migrations run from an empty database.

1. **End is not before start.** Given a permit whose end date is the day before it starts, when it is granted, then the grant is refused with "endDate must not be before startDate".
2. **The reason is at most 200 characters.** Given a description of 201 characters, when it is granted, then the grant is refused with "description must be at most 200 characters".
3. **An email is unique among active people.** Given an active person with an email, when a second person claims it, then the registration conflicts with a message containing "already exists".
