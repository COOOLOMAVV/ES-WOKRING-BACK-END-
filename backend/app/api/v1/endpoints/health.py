import logging
import time
from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session
from sqlalchemy import text
from app.db.database import get_db, test_db_connection
from app.core.config import settings
from app.schemas.health import HealthResponse

logger = logging.getLogger("estateflow.health")

router = APIRouter()


@router.get("/health", response_model=HealthResponse, tags=["Health"])
def check_health(db: Session = Depends(get_db)):
    """
    Checks PostgreSQL connectivity and returns database latency, version, and record counts.
    Ensures safe error handling without leaking sensitive connection details.
    """
    start_time = time.time()
    try:
        db_res = db.execute(text("SELECT NOW() as current_time, version() as pg_version")).mappings().first()

        # Safely count records in primary tables
        try:
            prop_count = db.execute(text("SELECT COUNT(*) FROM properties")).scalar() or 0
        except Exception:
            prop_count = 0

        try:
            agent_count = db.execute(text("SELECT COUNT(*) FROM agents")).scalar() or 0
        except Exception:
            agent_count = 0

        try:
            client_count = db.execute(text("SELECT COUNT(*) FROM clients")).scalar() or 0
        except Exception:
            client_count = 0

        latency = max(1, int((time.time() - start_time) * 1000))

        current_time_str = (
            db_res["current_time"].isoformat()
            if hasattr(db_res["current_time"], "isoformat")
            else str(db_res["current_time"])
        )

        pg_version_str = str(db_res["pg_version"]).split(" on ")[0] if db_res else "PostgreSQL"
        database_name = settings.DATABASE_NAME

        return {
            "status": "CONNECTED",
            "database": database_name,
            "serverTime": current_time_str,
            "postgresVersion": pg_version_str,
            "latencyMs": latency,
            "stats": {
                "properties": int(prop_count),
                "agents": int(agent_count),
                "clients": int(client_count),
                "appointments": 0,
                "inquiries": 0
            },
            "message": f"Connected to PostgreSQL ({latency}ms)"
        }
    except Exception as e:
        logger.error(f"Health check database connection error: {e}")
        # Return sanitized response - no raw stack trace or credential leak
        return JSONResponse(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            content={
                "status": "ERROR",
                "database": settings.DATABASE_NAME,
                "serverTime": None,
                "postgresVersion": None,
                "latencyMs": int((time.time() - start_time) * 1000),
                "stats": {"properties": 0, "agents": 0, "clients": 0, "appointments": 0, "inquiries": 0},
                "message": "Database server is unreachable. Please verify PostgreSQL service and network access."
            }
        )
