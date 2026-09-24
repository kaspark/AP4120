# TEA.01 · Tea register

> **TEA.01** · state **Implemented** · traces from [TEA-US-001](../user-stories/TEA-US-001.md)
> · source `backend/src/main/java/ee/taltech/ite4120/tea/`, `backend/src/main/resources/tea/db/changelog/`, `frontend/src/pages/`

## Description

A register of stocked tea: create, search, view, update, retire (soft delete). Each tea belongs to a user and a classification. The id is assigned by the database.

Example record:

```json
{
  "name": "Earl Grey",
  "brand": "Twinings",
  "categoryId": 1,
  "ownerId": 1,
  "purchaseDate": "2026-09-01",
  "expiryDate": "2027-09-01",
  "quantity": 45,
  "unit": "bags"
}
```

## Data model

| Table | Field | Type | Rules |
| --- | --- | --- | --- |
| `tea.tea_user` | `id` PK | bigint | from `core.seq_id` |
| | `name` | text | required |
| | `email` | text | required; unique among active rows |
| | `sys_*` | — | platform columns, trigger-managed |
| `tea.category` | `id` PK | bigint | from `core.seq_id`; the classification |
| | `name` | text | required; unique among active rows. Seeded: Black, Green, Herbal, Oolong |
| `tea.tea` | `id` PK | bigint | from `core.seq_id` |
| | `name`, `brand` | text | required |
| | `category_id` | bigint | required; FK → category |
| | `owner_id` | bigint | required; FK → tea_user |
| | `purchase_date`, `expiry_date` | date | required; expiry is not before purchase |
| | `quantity` | int | required; zero or greater |
| | `unit` | text | required, for example `bags` |

## API

| Method + path | Does | Failure answers |
| --- | --- | --- |
| `GET /api/teas` | paged search: `textContains`, `categoryId`, `ownerId`, `limit`, `offset`, `sort` | — |
| `GET /api/teas/{id}` | one active tea | 404 |
| `POST /api/teas` | register; **201 + Location** | 400 validation |
| `PUT /api/teas/{id}` | update | 400 · 404 |
| `DELETE /api/teas/{id}` | retire (soft) — **204** | 404 |
| `GET /api/categories` | classifications | — |
| `POST /api/categories` | add a classification | 409 duplicate name |
| `GET /api/users` | users | — |
| `POST /api/users` | add a user | 409 duplicate email |

Every error is `application/problem+json`.

## UI

| Screen | Element | Component | Rules |
| --- | --- | --- | --- |
| Tea list `/teas` | table | `ResourceList` | name links to the record; brand, classification, quantity, unit, expiry |
| Add tea `/teas/new` | form | antd `Form` | name, brand, classification, owner, dates, quantity, unit |
| Tea page `/teas/{id}` | view / edit | `ResourceForm` | same fields; Retire is the soft delete |

## Business tests

1. **Expiry is not before purchase.** Given a tea whose expiry is the day before it was bought, when it is registered, then the registration is refused with "expiryDate must not be before purchaseDate".
2. **Quantity is not negative.** Given a quantity of -1, when it is registered, then the registration is refused with "quantity must not be negative".
3. **An email is unique among active users.** Given an active user with an email, when a second user claims it, then the registration conflicts.
