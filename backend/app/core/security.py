"""
Security and Authentication Helpers
"""
import hashlib
import hmac
from datetime import datetime, timedelta, timezone
from typing import Optional
from app.core.config import settings


def hash_password(password: str) -> str:
    """Simple cryptographic hash for passwords using SHA-256 with salt."""
    salt = settings.SECRET_KEY[:16].encode("utf-8")
    return hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 100000).hex()


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verifies a plain password against stored hash."""
    return hmac.compare_digest(hash_password(plain_password), hashed_password)
