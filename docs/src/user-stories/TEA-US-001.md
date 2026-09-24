# TEA-US-001 · Keep a tea cupboard

> **TEA-US-001** · state **Implemented**

**As** a tea owner
**I want** a register of the teas I have, who they belong to, and how they are classified
**so that** I can see what is in stock and when it expires.

## Context

Three tables: the people who own tea, the classification list (black, green, herbal, oolong), and each stocked tea. A tea records the brand, dates, and how much is left.

Out of scope: shop checkout, brewing recipes, and authentication beyond the course mock sign-in.

## Acceptance criteria

- A tea cannot expire before it was purchased.
- Quantity cannot be negative.
- An email belongs to at most one active user.
