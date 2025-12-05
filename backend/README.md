# Test Coverage Hub - Backend

FastAPI backend for the Bank of America Test Coverage Hub.

## Setup

```bash
cd backend
pip install -r requirements.txt
```

## Running

```bash
uvicorn main:app --reload --port 8000
```

The API will be available at `http://localhost:8000`

API documentation (Swagger UI) will be available at `http://localhost:8000/docs`

## Optional Devin API integration

To have the backend actually call Devin when you click **Generate Tests with Devin**, configure:

```bash
export DEVIN_API_KEY="your-api-key-here"
export DEVIN_API_URL="https://your-devin-endpoint"  # provided by Devin
uvicorn main:app --reload --port 8000
```

If either `DEVIN_API_KEY` or `DEVIN_API_URL` is missing, the endpoint behaves as a pure simulation and only updates the in-memory data.

## Endpoints

- `GET /api/services` - Returns all services
- `POST /api/services/{id}/generate_tests` - Simulates Devin generating tests for a service

## Notes

This is a demo simulation. The "Generate Tests" endpoint simulates Devin generating and applying tests by:
- Setting coverage to the goal percentage
- Setting status to "healthy"
- Updating last_updated timestamp

In production, this would call the actual Devin API with service-specific prompts.

