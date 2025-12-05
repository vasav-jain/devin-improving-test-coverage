from pydantic import BaseModel
from typing import Literal


class Service(BaseModel):
    id: int
    name: str
    team: str
    tech_stack: str
    coverage: int  # percentage
    goal: int  # percentage
    last_updated: str  # ISO date string
    status: Literal["healthy", "at-risk", "ip"]
    deprecation_risk: Literal["low", "medium", "high"]
    codebase_path: str = ""  # Path to actual Java service for Devin to target

