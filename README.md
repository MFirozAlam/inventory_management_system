# Inventory Management System

Spring Boot 4.1.1 + Java 21 + MySQL backend with a React/Vite frontend.

## Run backend
1. Create/use the `inventory_management` MySQL database.
2. Check `backend/src/main/resources/application.properties` and set your MySQL username/password.
3. From `backend`, run `mvn clean spring-boot:run`.

The application uses `spring.jpa.hibernate.ddl-auto=update`, so an existing `inventory_management` database is updated without dropping tables. `database/InventoryDB.sql` is a clean schema for a new database.

## Run frontend
From `frontend`:

```bash
npm install
npm run dev
```

The default API is `http://localhost:8080/api`. To change it, copy `.env.example` to `.env` and set `VITE_API_URL`.

## Authentication
- `POST /api/auth/register` creates a STAFF account.
- `POST /api/auth/login` returns a bearer token.
- `POST /api/auth/logout` invalidates the token.
- Existing ADMINISTRATOR users are normalized to ADMIN when they log in.

## Inventory features
- Product CRUD with category, supplier, price and stock threshold
- Purchase and sales records with automatic stock updates
- Low-stock alerts
- Search, category filtering and sorting
- Customer and supplier CRUD
- Admin-only user management
- Dashboard inventory statistics
