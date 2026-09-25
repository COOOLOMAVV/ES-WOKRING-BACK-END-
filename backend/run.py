#!/usr/bin/env python3
"""
Real Estate FastAPI Backend Application Runner
Run with:
    python run.py
"""
import socket
import uvicorn
from app.core.config import settings


def get_local_ip() -> str:
    """Detects primary local network IP address."""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"


if __name__ == "__main__":
    local_ip = get_local_ip()
    db_target = settings.get_database_url().split("@")[-1] if "@" in settings.get_database_url() else settings.get_database_url()

    print("==================================================================")
    print("  🚀 REAL ESTATE MANAGEMENT API (FastAPI + SQLAlchemy)")
    print("==================================================================")
    print(f"  • Local Server:       http://localhost:{settings.PORT}")
    print(f"  • Network Server:     http://{local_ip}:{settings.PORT}")
    print(f"  • Swagger UI Docs:    http://localhost:{settings.PORT}/docs")
    print(f"  • ReDoc Docs:         http://localhost:{settings.PORT}/redoc")
    print(f"  • Target Database:    {db_target}")
    print("------------------------------------------------------------------")
    print("  📋 DEMO CHEATSHEET:")
    print("     1. Open Swagger:   http://localhost:{settings.PORT}/docs")
    print("     2. Test GET /properties to see seeded listings")
    print("     3. Test POST /properties with invalid agent to see error handling")
    print("     4. Test POST /clients or POST /agents to demonstrate CRUD")
    print("==================================================================\n")

    uvicorn.run(
        "app.main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=True if settings.ENVIRONMENT == "development" else False
    )
