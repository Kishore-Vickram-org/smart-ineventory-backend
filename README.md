# Harbor Inventory — Backend (Spring Boot)

Spring Boot REST API for Harbor Inventory.

## Requirements

- Java 17
- Maven

## Run locally

### Option A: VS Code tasks (recommended)

From the VS Code task runner:

- `Backend: Spring Boot (8080)`
- `Backend: Stop (8080)`
- `Backend: Reset DB (delete backend/data)` (only relevant when using a file-based DB)

The backend listens on `http://localhost:8080`.

### Option B: PowerShell script

From the repo root:

```powershell
./backend/run-backend.ps1 -Port 8080
```

Health check:

- `http://localhost:8080/actuator/health`

Stop:

```powershell
./backend/stop-backend.ps1 -Port 8080
```

### Option C: Maven

```powershell
cd backend
mvn spring-boot:run
```

## API

Base path: `http://localhost:8080/api`

Endpoints:

- Items
  - `GET /api/items`
  - `GET /api/items/{id}`
  - `POST /api/items`
  - `PUT /api/items/{id}`
  - `DELETE /api/items/{id}`
  - `POST /api/items/{id}/movements`
- Locations
  - `GET /api/locations`
  - `POST /api/locations`
  - `PUT /api/locations/{id}`
  - `DELETE /api/locations/{id}`
- Movements
  - `GET /api/movements?itemId=&type=&locationId=&limit=100`

## Database

By default, this project is configured to use an **in-memory H2 database** (data resets on each restart).

H2 console:

- `http://localhost:8080/h2-console`

If you want persistence in local dev, set a file-based JDBC URL, for example:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:h2:file:./data/harbor-inventory;AUTO_SERVER=TRUE'
```

Docker Compose (in this repo) already configures a file-based H2 database stored in a named volume.

A PostgreSQL driver is included in the Maven dependencies; switching to Postgres is done by setting standard Spring `spring.datasource.*` properties.

## CORS

CORS is applied to `/api/**`.

- Default behavior is permissive for development (`allowedOriginPatterns("*")`).
- To lock it down, set one of:
  - `app.cors.allowed-origins`
  - `app.cors.allowed-origin-patterns`

## Docker

From the repo root:

```powershell
docker compose up --build
```

- Backend: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`
