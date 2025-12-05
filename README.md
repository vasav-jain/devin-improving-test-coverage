# Test Coverage Hub

A full-stack internal tool for **Bank of America's Developer Experience team** to track test coverage across services and use Devin to close quality gaps.

## 🎯 Overview

This tool enables platform and QA leaders to:
- View current test coverage of backend/frontend/internal tools
- Identify services at risk (low test coverage, high deprecation risk)
- Trigger **Devin** to generate automated tests and update coverage
- Use a simple internal dashboard UI to coordinate the above

## 🧱 Tech Stack

- **Backend**: FastAPI (Python 3.10+)
- **Frontend**: React (TypeScript) using Vite
- **Styling**: Bootstrap 5
- **State**: In-memory data (no database required)

## 🚀 Quick Start

### Backend Setup

```bash
cd backend
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

Backend will run on `http://localhost:8000`
API docs available at `http://localhost:8000/docs`

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend will run on `http://localhost:5173`

## 📁 Project Structure

```
/backend
├── main.py              # FastAPI application
├── models.py            # Pydantic models
├── data.py              # Seed data (12 services)
├── requirements.txt     # Python dependencies
└── README.md

/frontend
├── src/
│   ├── App.tsx          # Main app component
│   ├── api.ts           # API client
│   ├── types.ts         # TypeScript types
│   ├── index.tsx        # Entry point
│   └── components/
│       └── ServiceTable.tsx  # Service table component
├── package.json
├── vite.config.ts
└── README.md
```

## 🧪 Features

- **Service Dashboard**: View all services with coverage metrics, risk levels, and status
- **Color-Coded Indicators**: 
  - Coverage: Red (<50%), Orange (<goal), Green (>=goal)
  - Risk: Red (high), Yellow (medium), Green (low)
- **Devin Integration**: Generate tests button for services below goal
- **Real-time Updates**: Table updates immediately after test generation
- **Loading States**: Visual feedback during test generation

## 📊 Seed Data

The app includes 12 diverse services:
- Payments API (Java/Spring Boot) - 22% coverage
- Mobile App Backend (Node.js) - 75% coverage
- AML Compliance Module (Python/Django) - 25% coverage
- Legacy CRM Adapter (COBOL to Python) - 10% coverage
- And 8 more services across different teams and tech stacks

## 🔧 API Endpoints

- `GET /api/services` - Returns all services
- `POST /api/services/{id}/generate_tests` - Simulates Devin generating tests

## 📝 Notes

This is a demo simulation. The "Generate Tests with Devin" button simulates calling Devin to generate and apply tests. In production, this would call the actual Devin API with service-specific prompts like:

> "Generate unit tests for the InterestCalculator class in payments.py covering edge cases such as zero value, max principal, etc. Assume this is legacy Java code refactored into Python."

The app demonstrates how AI-driven DevEx tooling improves engineering quality and velocity.
