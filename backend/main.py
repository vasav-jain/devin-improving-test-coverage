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
GITHUB_REPO_URL = "https://github.com/vasav-jain/devin-improving-test-coverage.git"
GITHUB_BRANCH = "main"


async def call_devin_for_service(service: Service) -> Optional[Dict[str, Any]]:
  """
  Optional integration point to call Devin's API.

  If DEVIN_API_URL or DEVIN_API_KEY are not configured, this becomes a no-op and the
  endpoint behaves as a pure simulation.
  """
  if not DEVIN_API_URL or not DEVIN_API_KEY:
      return None

  # Build detailed prompt with codebase location and repo instructions
  branch_name = f"feature/test-coverage-service-{service.id}-{service.name.lower().replace(' ', '-')}"
  
  prompt = (
      f"You are Devin helping the Bank of America DevEx team improve test coverage.\n\n"
      f"REPOSITORY & SERVICE INFORMATION\n"
      f"Repository: {GITHUB_REPO_URL}\n"
      f"Branch: {GITHUB_BRANCH}\n"
      f"New Branch to Create: {branch_name}\n"
      f"Service: {service.name}\n"
      f"Team: {service.team}\n"
      f"Tech stack: {service.tech_stack}\n"
      f"Service directory: {service.codebase_path}/\n"
      f"Source code: {service.codebase_path}/src/main/java/\n"
      f"Test directory: {service.codebase_path}/src/test/java/\n"
      f"Current coverage: {service.coverage}%\n"
      f"Target coverage: {service.goal}%\n"
      f"Status: {service.status}\n"
      f"Risk level: {service.deprecation_risk}\n\n"
      f"WORKFLOW - STEP BY STEP\n"
      f"1. Clone the repository: {GITHUB_REPO_URL}\n"
      f"2. Checkout base branch: git checkout {GITHUB_BRANCH}\n"
      f"3. Create new branch: git checkout -b {branch_name}\n"
      f"4. Navigate to: cd {service.codebase_path}\n"
      f"5. Review existing code in src/main/java/ (all controllers, services, repositories)\n"
      f"6. Review existing tests in src/test/java/ to understand patterns\n"
      f"7. Generate comprehensive JUnit 5 test files to reach {service.goal}% coverage\n"
      f"8. Run tests: mvn test (all must pass)\n"
      f"9. Run coverage report: mvn test jacoco:report (verify {service.goal}% target met)\n"
      f"10. Stage changes: git add src/test/\n"
      f"11. Commit with detailed message (format below)\n"
      f"12. Push branch: git push origin {branch_name}\n"
      f"13. Create PR to {GITHUB_BRANCH} with title: 'test: {service.name} coverage {service.coverage}% → {service.goal}%'\n\n"
      f"TEST GENERATION REQUIREMENTS\n"
      f"Focus areas:\n"
      f"1. Critical business logic paths (all public methods)\n"
      f"2. Edge cases and boundary conditions (min/max values, null, empty)\n"
      f"3. Exception handling scenarios (invalid inputs, constraint violations)\n"
      f"4. Validation logic (all @Valid annotations, custom validators)\n"
      f"5. Integration between components (controller → service → repository)\n"
      f"6. Data transformation and mapping logic\n\n"
      f"Test standards:\n"
      f"- Use JUnit 5 (@Test, @BeforeEach, @AfterEach)\n"
      f"- Use proper assertions (assertEquals, assertTrue, assertThrows)\n"
      f"- Test method names should be descriptive: test<Method>_<Scenario>\n"
      f"- Include JavaDoc comments explaining what each test validates\n"
      f"- Mock external dependencies if needed\n"
      f"- Aim for {service.goal}% line coverage minimum\n\n"
      f"COMMIT MESSAGE FORMAT (REQUIRED)\n"
      f"Title: test: Increase {service.name} coverage from {service.coverage}% to {service.goal}%\n\n"
      f"Body must include:\n\n"
      f"Test Cases Added:\n"
      f"1. <ClassName>.<testMethod>() - <what it tests>\n"
      f"2. <ClassName>.<testMethod>() - <what it tests>\n"
      f"[...list ALL test methods created...]\n\n"
      f"Coverage Breakdown:\n"
      f"- <ClassName>: <before>% → <after>%\n"
      f"- <ClassName>: <before>% → <after>%\n"
      f"[...for each class tested...]\n\n"
      f"Overall: {service.coverage}% → {service.goal}%\n\n"
      f"Example:\n"
      f"```\n"
      f"test: Increase {service.name} coverage from {service.coverage}% to {service.goal}%\n\n"
      f"Test Cases Added:\n"
      f"1. InterestCalculatorTest.testNegativeRate() - validates rejection of negative annual rates\n"
      f"2. InterestCalculatorTest.testMaxPrincipal() - tests upper boundary with MAX_VALUE\n"
      f"3. InterestCalculatorTest.testGracePeriodBoundary() - validates 3-day grace period edge\n"
      f"4. MortgageCalculatorTest.testPmiThreshold() - validates PMI calculation at 80% LTV\n"
      f"5. MortgageCalculatorTest.testPrepaymentPenalty() - tests early payoff within 24 months\n"
      f"6. PaymentServiceTest.testScheduleValidation() - validates future date requirements\n\n"
      f"Coverage Breakdown:\n"
      f"- InterestCalculator: 15% → 92%\n"
      f"- MortgageCalculator: 8% → 85%\n"
      f"- PaymentService: 22% → 78%\n"
      f"- PaymentRepository: 30% → 75%\n\n"
      f"Overall: {service.coverage}% → {service.goal}%\n"
      f"```\n"
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
    Trigger Devin to generate tests.
    Sets status to in-progress while Devin works asynchronously.
    """
    # Find the service
    service = next((s for s in services_db if s.id == service_id), None)
    
    if not service:
        raise HTTPException(status_code=404, detail="Service not found")
    
    # Call Devin API to start async test generation
    devin_response = await call_devin_for_service(service)
    
    # Set to in-progress while Devin works (user can manually mark as healthy after PR is merged)
    service.status = "in-progress"
    service.last_updated = datetime.now().isoformat()
    
    return service


@app.post("/api/services/{service_id}/mark_complete")
async def mark_complete(service_id: int) -> Service:
    """
    Mark a service as complete after reviewing and merging Devin's PR.
    Updates coverage to goal and sets status to healthy.
    """
    service = next((s for s in services_db if s.id == service_id), None)
    
    if not service:
        raise HTTPException(status_code=404, detail="Service not found")
    
    # Mark as complete - coverage achieved
    service.coverage = service.goal
    service.status = "healthy"
    service.last_updated = datetime.now().isoformat()
    
    return service

