import logging
import sys
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException as StarletteHTTPException
import uvicorn

from app.core.config import settings
from app.db.database import Base, engine, test_db_connection
from app.db.init_db import init_db
from app.routers import agents, clients, properties, admin
from app.api.v1.endpoints import health

# Structured Logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger("estateflow.main")


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Application startup and shutdown events.
    Verifies database connectivity, checks PostgreSQL schema, and ensures demo readiness.
    """
    logger.info("====================================================")
    logger.info(f"Starting {settings.APP_NAME}")
    logger.info(f"Host: http://{settings.HOST}:{settings.PORT}")
    logger.info(f"Database Target: {settings.get_database_url_safe()}")
    logger.info(f"Interactive Swagger Docs: http://{settings.HOST}:{settings.PORT}/docs")
    logger.info(f"ReDoc Documentation: http://{settings.HOST}:{settings.PORT}/redoc")
    logger.info("====================================================")

    try:
        # Initialize schema and seed demonstration records if empty
        init_db(seed_sample_data=True)
        logger.info("Database verification and initialization successful.")
    except Exception as e:
        logger.warning(f"Note on startup database initialization: {e}")

    yield

    logger.info(f"{settings.APP_NAME} shutdown cleanly.")


app = FastAPI(
    title=settings.APP_NAME,
    description=(
        "Production-grade FastAPI Backend with complete CRUD operations for "
        "Properties, Clients, and Agents. Includes relational integrity, "
        "Pydantic data validation, PostgreSQL connection pooling, and secure admin access."
    ),
    version="2.2.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    lifespan=lifespan
)

# Configure Cross-Origin Resource Sharing (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.get_cors_origins(),
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Global Exception Handlers
@app.exception_handler(StarletteHTTPException)
async def http_exception_handler(request: Request, exc: StarletteHTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"detail": exc.detail}
    )


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    errors = []
    for error in exc.errors():
        loc = " -> ".join(str(l) for l in error.get("loc", []))
        msg = error.get("msg", "Invalid value")
        errors.append(f"{loc}: {msg}")
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "detail": "Request validation failed.",
            "errors": errors
        }
    )


@app.exception_handler(Exception)
async def unhandled_exception_handler(request: Request, exc: Exception):
    logger.error(f"Unhandled server error on {request.method} {request.url.path}: {exc}", exc_info=True)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={"detail": "An unexpected internal server error occurred."}
    )


# 1. Primary CRUD Routers (mounted at root as requested)
app.include_router(properties.router)
app.include_router(clients.router)
app.include_router(agents.router)
app.include_router(admin.router)

# 2. Also mount under `/api` prefix for frontend / client compatibility
app.include_router(properties.router, prefix="/api")
app.include_router(clients.router, prefix="/api")
app.include_router(agents.router, prefix="/api")
app.include_router(admin.router, prefix="/api")

# 3. Mount health check at both root and /api
app.include_router(health.router)
app.include_router(health.router, prefix="/api")

# 4. Optional mobile sync router if needed
try:
    from app.api.v1.endpoints import appointments, inquiries, sync
    app.include_router(appointments.router, prefix="/api")
    app.include_router(inquiries.router, prefix="/api")
    app.include_router(sync.router, prefix="/api")
except Exception as e:
    logger.debug(f"Optional legacy sync modules: {e}")


@app.get("/", tags=["Root"])
def root_info():
    """Returns application status and documentation entry points."""
    db_check = test_db_connection()
    return {
        "app": settings.APP_NAME,
        "version": "2.2.0",
        "status": "online",
        "database": {
            "status": "connected" if db_check.get("connected") else "disconnected",
            "name": settings.DATABASE_NAME,
            "latency_ms": db_check.get("latency_ms")
        },
        "endpoints": {
            "properties": "/properties",
            "clients": "/clients",
            "agents": "/agents",
            "health": "/health",
            "admin": "/admin/db-status",
            "documentation_swagger": "/docs",
            "documentation_redoc": "/redoc"
        }
    }


if __name__ == "__main__":
    uvicorn.run(
        "app.main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=True if settings.ENVIRONMENT == "development" else False
    )
