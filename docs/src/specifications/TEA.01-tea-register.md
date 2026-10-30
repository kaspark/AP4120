# TEA.01 · Tea register

> **TEA.01** · state **Implemented** · traces from [TEA-US-001](../user-stories/TEA-US-001.md)
> · source `backend/src/main/java/ee/taltech/ite4120/tea/`, `backend/src/main/resources/tea/db/changelog/`, `frontend/src/pages/`

## Description

A register of stocked tea: create, search, view, update, retire (soft delete). Each tea belongs to a user and a classification. The id is assigned by the database.

Example record. The ids in this body are assigned by the database; the seed below names the classification and the owner instead.

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

## Example data

Demo rows load only when Liquibase runs with context `demo` (`spring.liquibase.contexts` in `application.yml`). `90-demo-data.sql` is the changeset that already shipped (`ite4120:tea-90-demo-data`): Mari Tamm and Earl Grey. It is not edited. `91-demo-data.sql` (`ite4120:tea-91-demo-data`, `context:demo`) appends four users and twenty-five teas, and `tea/changelog.xml` includes that file after `90-demo-data.sql`.

The seed stores foreign keys. It resolves `category_id` by classification name and `owner_id` by email, among active rows, the same way Earl Grey is inserted. Users go in before the teas that name them.

| Name | Email | Changeset |
| --- | --- | --- |
| Mari Tamm | mari.tamm@example.com | `90-demo-data.sql` |
| Jaan Kask | jaan.kask@example.com | `91-demo-data.sql` |
| Liis Rebane | liis.rebane@example.com | `91-demo-data.sql` |
| Karl Põder | karl.poder@example.com | `91-demo-data.sql` |
| Eva Saar | eva.saar@example.com | `91-demo-data.sql` |

| Name | Brand | Classification | Owner email | Purchase | Expiry | Quantity | Unit | Changeset |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Earl Grey | Twinings | Black | mari.tamm@example.com | 2026-09-01 | 2027-09-01 | 45 | bags | `90-demo-data.sql` |
| Sencha | Ippodo | Green | mari.tamm@example.com | 2026-03-12 | 2027-03-12 | 80 | g | `91-demo-data.sql` |
| Dragon Well | TeaVivre | Green | jaan.kask@example.com | 2026-04-02 | 2027-04-02 | 50 | g | `91-demo-data.sql` |
| Genmaicha | Yamamotoyama | Green | liis.rebane@example.com | 2026-01-20 | 2027-01-20 | 40 | bags | `91-demo-data.sql` |
| Jasmine Pearl | Harney & Sons | Green | karl.poder@example.com | 2026-06-15 | 2027-06-15 | 30 | g | `91-demo-data.sql` |
| Gyokuro | Marukyu | Green | eva.saar@example.com | 2026-05-01 | 2027-05-01 | 25 | g | `91-demo-data.sql` |
| English Breakfast | Twinings | Black | mari.tamm@example.com | 2026-02-10 | 2028-02-10 | 100 | bags | `91-demo-data.sql` |
| Assam | Ahmad Tea | Black | jaan.kask@example.com | 2025-11-03 | 2027-11-03 | 60 | bags | `91-demo-data.sql` |
| Darjeeling | Makaibari | Black | liis.rebane@example.com | 2026-07-08 | 2027-07-08 | 40 | g | `91-demo-data.sql` |
| Lapsang Souchong | Taylors | Black | karl.poder@example.com | 2026-08-19 | 2028-08-19 | 20 | bags | `91-demo-data.sql` |
| Ceylon | Dilmah | Black | eva.saar@example.com | 2026-09-14 | 2028-09-14 | 75 | bags | `91-demo-data.sql` |
| Russian Caravan | Kusmi | Black | mari.tamm@example.com | 2026-01-05 | 2027-06-05 | 35 | bags | `91-demo-data.sql` |
| Keemun | Teapigs | Black | jaan.kask@example.com | 2026-03-28 | 2027-09-28 | 15 | tins | `91-demo-data.sql` |
| Chamomile | Traditional Medicinals | Herbal | liis.rebane@example.com | 2026-04-18 | 2028-04-18 | 20 | bags | `91-demo-data.sql` |
| Peppermint | Pukka | Herbal | karl.poder@example.com | 2026-02-22 | 2028-02-22 | 24 | bags | `91-demo-data.sql` |
| Rooibos | Tick Tock | Herbal | eva.saar@example.com | 2026-06-01 | 2028-06-01 | 50 | bags | `91-demo-data.sql` |
| Hibiscus | Celestial Seasonings | Herbal | mari.tamm@example.com | 2026-05-11 | 2027-11-11 | 18 | bags | `91-demo-data.sql` |
| Lemon Verbena | Palais des Thés | Herbal | jaan.kask@example.com | 2026-07-30 | 2027-12-30 | 40 | g | `91-demo-data.sql` |
| Ginger Turmeric | Yogi | Herbal | liis.rebane@example.com | 2026-08-04 | 2028-08-04 | 16 | bags | `91-demo-data.sql` |
| Tieguanyin | TeaVivre | Oolong | karl.poder@example.com | 2026-03-03 | 2027-03-03 | 45 | g | `91-demo-data.sql` |
| Da Hong Pao | Wuyi Star | Oolong | eva.saar@example.com | 2026-09-20 | 2028-03-20 | 30 | g | `91-demo-data.sql` |
| Milk Oolong | Lupicia | Oolong | mari.tamm@example.com | 2026-04-25 | 2027-04-25 | 55 | g | `91-demo-data.sql` |
| Dong Ding | Mountain Tea | Oolong | jaan.kask@example.com | 2026-01-16 | 2027-01-16 | 35 | g | `91-demo-data.sql` |
| Oriental Beauty | Tea from Taiwan | Oolong | liis.rebane@example.com | 2026-06-21 | 2027-12-21 | 20 | g | `91-demo-data.sql` |
| Osmanthus Oolong | Ten Ren | Oolong | karl.poder@example.com | 2026-02-14 | 2027-08-14 | 28 | bags | `91-demo-data.sql` |
| Iron Buddha | Fujian Tea | Oolong | eva.saar@example.com | 2026-10-01 | 2028-04-01 | 42 | g | `91-demo-data.sql` |

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
