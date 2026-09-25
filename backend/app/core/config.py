import urllib.parse
from typing import List, Union, Optional
from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict
from sqlalchemy.engine import URL


class Settings(BaseSettings):
    """
    Application Settings loaded from environment variables and .env file.
    Configured specifically for PostgreSQL with psycopg 3.
    """
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"
    )

    # Server Settings
    APP_NAME: str = "Real Estate Management API"
    ENVIRONMENT: str = "development"
    PORT: int = 8000
    HOST: str = "0.0.0.0"

    # PostgreSQL Database Configuration
    DATABASE_URL: Optional[str] = Field(
        default=None,
        description="Full SQLAlchemy database connection URL override"
    )
    DATABASE_TYPE: str = Field(
        default="postgresql",
        description="Database engine type (postgresql or sqlite for tests)"
    )
    DATABASE_HOST: str = Field(default="localhost", alias="PG_HOST")
    DATABASE_PORT: int = Field(default=5432, alias="PG_PORT")
    DATABASE_NAME: str = Field(default="real_estate", alias="PG_DATABASE")
    DATABASE_USER: str = Field(default="postgres", alias="PG_USER")
    DATABASE_PASSWORD: str = Field(default="postgres", alias="PG_PASSWORD")

    # PostgreSQL Connection Pool Settings
    DB_POOL_SIZE: int = 10
    DB_MAX_OVERFLOW: int = 20
    DB_POOL_TIMEOUT: int = 30
    DB_POOL_RECYCLE: int = 1800

    # Security & Admin Access
    SECRET_KEY: str = "real_estate_super_secret_jwt_key_for_fastapi_production"
    ADMIN_API_KEY: str = "estateflow_admin_secret_key_2024"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7
    CORS_ORIGINS: Union[str, List[str]] = "http://localhost:3000,http://localhost:5173"

    def get_database_url(self) -> str:
        """
        Constructs the modern SQLAlchemy 2.x connection URL.
        Properly encodes credentials using sqlalchemy.engine.URL to safely handle
        special characters in passwords.
        Uses postgresql+psycopg:// for PostgreSQL with psycopg 3.
        """
        if self.DATABASE_URL and self.DATABASE_URL.strip():
            raw_url = self.DATABASE_URL.strip()
            if raw_url.startswith("postgres://"):
                return raw_url.replace("postgres://", "postgresql+psycopg://", 1)
            elif raw_url.startswith("postgresql://") and not raw_url.startswith("postgresql+"):
                return raw_url.replace("postgresql://", "postgresql+psycopg://", 1)
            return raw_url

        db_type = self.DATABASE_TYPE.lower().strip()
        if db_type == "sqlite":
            return f"sqlite:///./{self.DATABASE_NAME}.db"

        # Production / Default: PostgreSQL with psycopg3 driver
        url_obj = URL.create(
            drivername="postgresql+psycopg",
            username=self.DATABASE_USER or "postgres",
            password=self.DATABASE_PASSWORD or "",
            host=self.DATABASE_HOST,
            port=self.DATABASE_PORT,
            database=self.DATABASE_NAME
        )
        return url_obj.render_as_string(hide_password=False)

    def get_database_url_safe(self) -> str:
        """
        Returns the database URL with the password masked for safe logging and status endpoints.
        """
        if self.DATABASE_URL and self.DATABASE_URL.strip():
            try:
                # Mask credentials in raw URL
                parsed = urllib.parse.urlparse(self.DATABASE_URL.strip())
                netloc = parsed.netloc
                if "@" in netloc:
                    userinfo, hostinfo = netloc.split("@", 1)
                    user = userinfo.split(":", 1)[0]
                    netloc = f"{user}:***@{hostinfo}"
                return urllib.parse.urlunparse(parsed._replace(netloc=netloc))
            except Exception:
                return "postgresql+psycopg://***"

        db_type = self.DATABASE_TYPE.lower().strip()
        if db_type == "sqlite":
            return f"sqlite:///./{self.DATABASE_NAME}.db"

        url_obj = URL.create(
            drivername="postgresql+psycopg",
            username=self.DATABASE_USER or "postgres",
            password="***",
            host=self.DATABASE_HOST,
            port=self.DATABASE_PORT,
            database=self.DATABASE_NAME
        )
        return url_obj.render_as_string(hide_password=False)

    def get_cors_origins(self) -> List[str]:
        """Parses CORS origins into a list of strings."""
        if isinstance(self.CORS_ORIGINS, list):
            return self.CORS_ORIGINS
        if self.CORS_ORIGINS == "*":
            return ["*"]
        return [origin.strip() for origin in self.CORS_ORIGINS.split(",") if origin.strip()]


settings = Settings()
