from datetime import datetime
import os
from typing import Any, Dict, Optional

import httpx
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from models import Service
from data import get_services

app = FastAPI(title="Test Coverage Hub API", version="1.0.0")

# Enable CORS for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# In-memory storage (simulating database)
services_db = get_services()


DEVIN_API_URL = "https://api.devin.ai/v1/sessions"
DEVIN_API_KEY = "apk_user_ZW1haWx8NjkxMjVmNTIwMjViZThmOGExMmY3YTE0X29yZy1lMGI4NWQ5Yjk5MDk0YWViYTc4MTY5MzJlNzkzMmVlZTo1YzFhMzY2NDk0MTI0ZTNmODY0N2JiNGFkNzEyM2RiOQ=="


async def call_devin_for_service(service: Service) -> Optional[Dict[str, Any]]:
  """
  Optional integration point to call Devin's API.

  If DEVIN_API_URL or DEVIN_API_KEY are not configured, this becomes a no-op and the
  endpoint behaves as a pure simulation.
  """
  if not DEVIN_API_URL or not DEVIN_API_KEY:
      return None

  # Build detailed prompt with codebase location
  test_path_instruction = ""
  if service.codebase_path:
      test_path_instruction = (
          f"\n\nCodebase location: {service.codebase_path}\n"
          f"Test directory: {service.codebase_path}/src/test/java/\n"
          "Place new test files following the package structure in src/main/java/. "
          "Use JUnit 5 annotations (@Test, @BeforeEach, etc.). "
          "Run tests with: mvn test"
      )
  
  prompt = (
      f"You are Devin helping the Bank of America DevEx team improve test coverage.\n\n"
      f"Service: {service.name}\n"
      f"Team: {service.team}\n"
      f"Tech stack: {service.tech_stack}\n"
      f"Current coverage: {service.coverage}% | Target coverage: {service.goal}%\n"
      f"Status: {service.status}\n"
      f"Risk level: {service.deprecation_risk}\n"
      f"{test_path_instruction}\n\n"
      "TASK: Generate comprehensive unit and integration tests to increase coverage from "
      f"{service.coverage}% to {service.goal}%. Focus on:\n"
      "1. Critical business logic paths\n"
      "2. Edge cases and boundary conditions\n"
      "3. Exception handling scenarios\n"
      "4. Validation logic\n"
      "5. Fixing any existing failing or incorrect tests\n\n"
      "Review existing test files first to understand patterns, then add missing coverage.\n\n"
      "IMPORTANT - COMMIT MESSAGE REQUIREMENTS:\n"
      "When you commit the test files, your commit message MUST include:\n"
      "1. A complete numbered list of all test cases you created\n"
      "2. The specific classes/methods each test targets (e.g., 'InterestCalculator.calculateDailyCompound')\n"
      "3. What scenarios each test covers (e.g., 'negative values', 'boundary conditions', 'null handling')\n"
      "4. The final coverage percentage achieved\n\n"
      "Example commit message format:\n"
      "```\n"
      f"test: Increase {service.name} coverage from {service.coverage}% to {service.goal}%\n\n"
      "Test cases added:\n"
      "1. InterestCalculatorTest.testNegativeRate() - validates negative rate rejection\n"
      "2. InterestCalculatorTest.testMaxPrincipal() - tests upper boundary (MAX_VALUE)\n"
      "3. MortgageCalculatorTest.testPmiThreshold() - validates PMI calculation at 80% LTV\n"
      "[...continue for all tests...]\n\n"
      "Coverage improvements:\n"
      "- InterestCalculator: 15% → 92%\n"
      "- MortgageCalculator: 8% → 85%\n"
      "- PaymentService: 22% → 78%\n"
      "```"
  )

  payload = {"prompt": prompt}

  headers = {
      "Authorization": f"Bearer {DEVIN_API_KEY}",
      "Content-Type": "application/json",
  }

  async with httpx.AsyncClient(timeout=60) as client:
      try:
          response = await client.post(DEVIN_API_URL, json=payload, headers=headers)
          response.raise_for_status()
          return response.json()
      except httpx.HTTPError as exc:
          # In a real system you'd send this to structured logging/observability
          print(f"[Devin] API call failed for service {service.id}: {exc}")
          return None


@app.get("/api/services")
async def get_all_services() -> list[Service]:
    """Return all services."""
    return services_db


@app.post("/api/services/{service_id}/generate_tests")
async def generate_tests(service_id: int) -> Service:
    """
    Simulate calling Devin to generate tests.
    Updates coverage to goal, sets status to healthy, and updates last_updated.
    """
    # Find the service
    service = next((s for s in services_db if s.id == service_id), None)
    
    if not service:
        raise HTTPException(status_code=404, detail="Service not found")
    
    # Optionally call Devin API; simulation still applies coverage updates below
    await call_devin_for_service(service)

    # Simulate Devin generating tests by updating coverage locally
    service.coverage = service.goal
    service.status = "healthy"
    service.last_updated = datetime.now().isoformat()
    
    return service

