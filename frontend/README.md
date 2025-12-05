# Test Coverage Hub - Frontend

React + TypeScript frontend for the Bank of America Test Coverage Hub.

## Setup

```bash
cd frontend
npm install
```

## Running

```bash
npm run dev
```

Frontend will run on `http://localhost:5173`

Make sure the backend is running on `http://localhost:8000` before using the app.

## Build

```bash
npm run build
```

## Features

- **Service Table**: Displays all services with coverage metrics, risk levels, and status
- **Color Coding**: 
  - Coverage: Red (<50%), Orange (<goal), Green (>=goal)
  - Risk: Red (high), Yellow (medium), Green (low)
- **Generate Tests**: Button appears for services where coverage < goal
- **Loading States**: Shows spinner while generating tests
- **Real-time Updates**: Table updates immediately after test generation

## Notes

This is a demo simulation. The "Generate Tests with Devin" button simulates calling Devin to generate and apply tests. In production, this would call the actual Devin API with service-specific prompts.

