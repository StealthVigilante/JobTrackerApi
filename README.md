# JobTracker API

A JWT-secured REST API to track job applications through their lifecycle — multi-user, with per-user data isolation. Every user sees only their own applications; every endpoint is locked behind a bearer token.

Built with Spring Boot 3.5, Java 21, Spring Security + JWT, Spring Data JPA, and PostgreSQL.

## Why it exists

Job hunting is a pipeline problem: dozens of applications, each moving through `APPLIED → INTERVIEW → OFFER / REJECTED`. This API is the backend for tracking that pipeline — register, log every application, update its status as things move, and filter to see (for example) everything currently at the interview stage.

## Features

- **JWT auth** — register / login, returns a signed token. All job endpoints require it.
- **Per-user isolation** — jobs are scoped to the authenticated user. You can never see or touch another user's data.
- **Full CRUD** — create, list, get one, update (PUT), delete.
- **Status filter** — `GET /jobs?status=INTERVIEW` returns only your jobs in that state.
- **Validation** — request bodies are validated; bad input returns a clean error.
- **Enum-backed status** — stored as a string in the DB (stable across reordering), not a fragile ordinal.

## Tech stack

| Layer | Choice |
|-------|--------|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| Deploy | Docker (multi-stage build) |

## Data model

A **Job** belongs to a **User** (`@ManyToOne`). The owner is set from the JWT, never from the request body.

```
Job
 ├─ id        Long
 ├─ company   String
 ├─ role      String
 ├─ link      String
 └─ status    enum { APPLIED, INTERVIEW, OFFER, REJECTED }
```

## API

All `/jobs` endpoints require an `Authorization: Bearer <token>` header.

### Auth

| Method | Path | Body | Returns |
|--------|------|------|---------|
| POST | `/auth/register` | `{ "username", "password" }` | `201` + `{ "token" }` |
| POST | `/auth/login` | `{ "username", "password" }` | `200` + `{ "token" }` |

### Jobs

| Method | Path | Body | Returns |
|--------|------|------|---------|
| GET | `/jobs` | — | `200` your jobs |
| GET | `/jobs?status=INTERVIEW` | — | `200` your jobs with that status |
| GET | `/jobs/{id}` | — | `200` one job / `404` |
| POST | `/jobs` | job fields | `201` created job |
| PUT | `/jobs/{id}` | job fields | `200` updated job / `404` |
| DELETE | `/jobs/{id}` | — | `204` / `404` |

Job request body:

```json
{
  "company": "Acme Corp",
  "role": "Backend Engineer",
  "link": "https://acme.example/careers/123",
  "status": "APPLIED"
}
```

## Quick start (local)

**1. Start PostgreSQL** (Docker example):

```bash
docker run -d --name study-pg -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres
docker exec study-pg psql -U postgres -c "CREATE DATABASE jobtracker;"
```

**2. Run the app:**

```bash
./mvnw spring-boot:run
```

App boots on `http://localhost:8080`.

**3. Try it:**

```bash
# register -> grab the token from the response
curl -s -X POST localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"steve","password":"secret123"}'

# create a job (paste your token)
curl -s -X POST localhost:8080/jobs \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <TOKEN>' \
  -d '{"company":"Acme","role":"Backend Engineer","link":"https://acme.example","status":"APPLIED"}'

# list only interviews
curl -s localhost:8080/jobs?status=INTERVIEW \
  -H 'Authorization: Bearer <TOKEN>'
```

## Configuration

All settings are externalized via environment variables (sensible local defaults in `application.properties`):

| Variable | Default | Purpose |
|----------|---------|---------|
| `PORT` | `8080` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/jobtracker` | JDBC URL |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | `postgres` | DB password |
| `JWT_SECRET` | *(dev default)* | HMAC signing key — **set a real one in production** |
| `JWT_EXPIRATION` | `86400000` | Token lifetime (ms, = 24h) |

## Deploy (Docker)

The included multi-stage `Dockerfile` builds the jar and ships a lean JRE runtime image.

```bash
# build
docker build -t jobtracker .

# run (point at your database, set a real secret)
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://<host>:5432/jobtracker" \
  -e DB_USERNAME="<user>" \
  -e DB_PASSWORD="<password>" \
  -e JWT_SECRET="<a-long-random-production-secret>" \
  jobtracker
```

On platforms like Railway / Render / Fly.io, point the service at this repo, set the env vars above, and the platform builds from the `Dockerfile` automatically.

## License

Personal study project.