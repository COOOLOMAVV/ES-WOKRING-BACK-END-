# Real Estate Management FastAPI Backend

A clean, production-ready **FastAPI** backend with **SQLAlchemy ORM 2.x**, **Pydantic v2 data validation**, **PostgreSQL connection pooling via psycopg 3**, and complete **CRUD operations** for:
1. **Properties**
2. **Clients**
3. **Agents**

Designed for high reliability, strict schema alignment, secure credential management via environment variables, and seamless presentation demonstrations.

---

## 🏗️ Project Architecture

```text
backend/
├── app/
│   ├── main.py                 # FastAPI application, lifespan, middleware & routers
│   ├── core/
│   │   ├── config.py           # Pydantic Settings (.env configuration & safe DB URL resolver)
│   │   └── database.py         # Re-export bridge for database session
│   ├── db/
│   │   ├── database.py         # SQLAlchemy engine, connection pool & get_db dependency
│   │   ├── models.py           # Canonical relational models matching database/schema.sql
│   │   └── init_db.py          # Database table creation & demo data seeder
│   ├── models/
│   │   └── __init__.py         # Re-exports canonical models (Agent, Client, Property)
│   ├── schemas/
│   │   ├── agent.py            # Pydantic validation models for Agents
│   │   ├── client.py           # Pydantic validation models for Clients
│   │   ├── property.py         # Pydantic validation models for Properties
│   │   └── health.py           # Sanitized health response schemas
│   ├── crud/
│   │   ├── agent.py            # Database operations for Agents
│   │   ├── client.py           # Database operations for Clients
│   │   └── property.py         # Database operations for Properties
│   └── routers/
│       ├── agents.py           # Endpoints: POST, GET, PUT, DELETE /agents
│       ├── clients.py          # Endpoints: POST, GET, PUT, DELETE /clients
│       ├── properties.py       # Endpoints: POST, GET, PUT, DELETE /properties
│       └── admin.py            # Restricted Admin DB status & diagnostic endpoints
├── database/
│   └── schema.sql              # Source of truth SQL initialization script & sample data
├── tests/
│   ├── conftest.py             # Isolated in-memory SQLite test fixtures
│   ├── test_agents.py          # Agent CRUD, validation & uniqueness tests
│   ├── test_clients.py         # Client CRUD & email validation tests
│   ├── test_properties.py      # Property CRUD, validation & relational integrity tests
│   └── test_admin.py           # Health check and administrative authorization tests
├── .env                        # Active environment configuration (git-ignored)
├── .env.example                # Template configuration with documentation
├── init_db.py                  # One-command database initializer script
├── requirements.txt            # Pinned dependencies (FastAPI, SQLAlchemy, psycopg 3, etc.)
├── run.py                      # Interactive application runner with auto-IP detection
└── README.md
```

---

## 🔒 Security & Architecture Principles

1. **Server-Side PostgreSQL Connection**:
   Clients (mobile app, web app, external users) communicate exclusively with the FastAPI REST API over HTTP/HTTPS. Clients never connect directly to PostgreSQL.
2. **Zero Client Exposure of Credentials**:
   Database usernames, passwords, hostnames, and internal ports are handled strictly on the backend via environment variables (`.env`). No database credentials are ever returned in client-facing API responses or packaged in mobile builds.
3. **Sanitized Health Checks**:
   The `/health` endpoint reports connectivity status, latency, and record counts without leaking raw database connection strings, passwords, or error stack traces to unauthenticated callers.
4. **Restricted Admin Interface**:
   System diagnostics and connection pool inspection are restricted behind `/admin/db-status` and require the `X-Admin-Key` header.
5. **Robust Connection Pooling & Safe URLs**:
   Connections are pooled with `pool_pre_ping=True` (detects dropped connections and self-heals), bounded timeouts, and credential encoding via `sqlalchemy.engine.URL` so special characters in passwords do not break connection strings.

---

## ⚡ Quick Start (Ready in 60 Seconds)

### 1. Create Virtual Environment & Activate

#### On macOS / Linux:
```bash
cd backend
python3 -m venv venv
source venv/bin/activate
```

#### On Windows (PowerShell):
```powershell
cd backend
python -m venv venv
.\venv\Scripts\Activate.ps1
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Configure `.env`
Copy the example configuration:
```bash
cp .env.example .env
```

Default PostgreSQL settings:
```env
DATABASE_TYPE=postgresql
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=real_estate
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
ADMIN_API_KEY=estateflow_admin_secret_key_2024
```

### 4. Initialize Database & Seed Demo Records
Run the initialization script (creates tables and seeds 3 agents, 3 clients, 5 properties if empty):
```bash
python init_db.py
```

### 5. Start the FastAPI Server
```bash
python run.py
```
Or with Uvicorn directly:
```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Open your browser to:
- **Interactive Swagger UI**: [http://localhost:8000/docs](http://localhost:8000/docs)
- **ReDoc Documentation**: [http://localhost:8000/redoc](http://localhost:8000/redoc)
- **Health Check**: [http://localhost:8000/health](http://localhost:8000/health)

### 6. Run Automated Tests
```bash
pytest -v
```
All test cases execute against an isolated in-memory test database without touching your development database.

---

## 🌐 Connecting to PostgreSQL on Another Computer (LAN)

If your database is hosted on another machine on your local network:

1. **Update `.env`**:
   ```env
   DATABASE_HOST=192.168.1.100   # Remote computer's LAN IP
   DATABASE_PORT=5432
   DATABASE_NAME=real_estate
   DATABASE_USER=postgres
   DATABASE_PASSWORD=your_password
   ```

2. **Configure Remote PostgreSQL Server**:
   - In `postgresql.conf`:
     ```text
     listen_addresses = '*'
     ```
   - In `pg_hba.conf`:
     ```text
     # Allow incoming connections from your local subnet:
     host    all    all    192.168.1.0/24    scram-sha-256
     ```
   - Restart the PostgreSQL service on the remote computer:
     - Windows: Open `services.msc` → Restart `postgresql-x64-XX`.
     - Linux: `sudo systemctl restart postgresql`.
   - Ensure TCP Port `5432` is open in the remote computer's firewall.

---

## 📊 Database Schema Summary (`database/schema.sql`)

| Table | Primary Key | Key Fields | Relationships |
| :--- | :--- | :--- | :--- |
| **`agents`** | `id BIGSERIAL` | `first_name`, `last_name`, `email` (UNIQUE), `phone` | One-to-Many with `properties` |
| **`clients`** | `id BIGSERIAL` | `first_name`, `last_name`, `email` (UNIQUE), `phone` | Independent buyer profiles |
| **`properties`** | `id BIGSERIAL` | `title`, `price` (NUMERIC 12,2), `address`, `city`, `bedrooms`, `bathrooms`, `area`, `status` | Belongs to `agents` via `agent_id` (`ON DELETE CASCADE`) |
