from datetime import datetime
from models import Service


def get_services() -> list[Service]:
    """Return seed data for 10+ services with varied coverage, risk, and stacks."""
    today = datetime.now().isoformat()
    
    return [
        Service(
            id=1,
            name="Payments API",
            team="Payments Platform",
            tech_stack="Java/Spring Boot",
            coverage=22,
            goal=80,
            last_updated="2024-01-15T10:30:00",
            status="at-risk",
            deprecation_risk="medium",
            codebase_path="banking-services/payments-service"
        ),
        Service(
            id=2,
            name="Mobile App Backend",
            team="Mobile Engineering",
            tech_stack="Node.js/Express",
            coverage=75,
            goal=80,
            last_updated="2024-01-20T14:22:00",
            status="healthy",
            deprecation_risk="low",
            codebase_path="banking-services/mobile-app-service"
        ),
        Service(
            id=3,
            name="AML Compliance Module",
            team="Compliance & Risk",
            tech_stack="Python/Django",
            coverage=25,
            goal=85,
            last_updated="2024-01-10T09:15:00",
            status="at-risk",
            deprecation_risk="high",
            codebase_path="banking-services/compliance-service"
        ),
        Service(
            id=4,
            name="Legacy CRM Adapter",
            team="Enterprise Integration",
            tech_stack="COBOL to Python",
            coverage=10,
            goal=70,
            last_updated="2023-12-05T16:45:00",
            status="at-risk",
            deprecation_risk="high"
        ),
        Service(
            id=5,
            name="Account Management Service",
            team="Core Banking",
            tech_stack="Java/Spring Boot",
            coverage=68,
            goal=75,
            last_updated="2024-01-18T11:20:00",
            status="ip",
            deprecation_risk="low"
        ),
        Service(
            id=6,
            name="Transaction Processing Engine",
            team="Payments Platform",
            tech_stack="Go",
            coverage=45,
            goal=90,
            last_updated="2024-01-12T13:30:00",
            status="at-risk",
            deprecation_risk="medium"
        ),
        Service(
            id=7,
            name="Fraud Detection API",
            team="Security & Fraud",
            tech_stack="Python/FastAPI",
            coverage=82,
            goal=85,
            last_updated="2024-01-22T15:10:00",
            status="healthy",
            deprecation_risk="low"
        ),
        Service(
            id=8,
            name="Loan Origination System",
            team="Lending",
            tech_stack=".NET Core",
            coverage=35,
            goal=80,
            last_updated="2024-01-08T08:45:00",
            status="at-risk",
            deprecation_risk="high"
        ),
        Service(
            id=9,
            name="Customer Onboarding Portal",
            team="Digital Banking",
            tech_stack="React/Node.js",
            coverage=58,
            goal=75,
            last_updated="2024-01-19T12:00:00",
            status="ip",
            deprecation_risk="medium"
        ),
        Service(
            id=10,
            name="Credit Scoring Microservice",
            team="Lending",
            tech_stack="Python/Flask",
            coverage=91,
            goal=85,
            last_updated="2024-01-21T16:30:00",
            status="healthy",
            deprecation_risk="low"
        ),
        Service(
            id=11,
            name="Legacy Mainframe Gateway",
            team="Enterprise Integration",
            tech_stack="COBOL/Java Bridge",
            coverage=15,
            goal=60,
            last_updated="2023-11-20T10:00:00",
            status="at-risk",
            deprecation_risk="high"
        ),
        Service(
            id=12,
            name="Notification Service",
            team="Platform Services",
            tech_stack="Node.js/TypeScript",
            coverage=72,
            goal=75,
            last_updated="2024-01-17T14:15:00",
            status="healthy",
            deprecation_risk="low"
        )
    ]

