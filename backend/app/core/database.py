"""
Compatibility bridge for legacy imports: redirects to app.db.database
"""
from app.db.database import Base, engine, SessionLocal, get_db, test_db_connection

__all__ = ["Base", "engine", "SessionLocal", "get_db", "test_db_connection"]
