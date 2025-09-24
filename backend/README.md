# Naviksha AI Backend

Spring Boot backend for AI-powered career guidance system.

## Quick Start

```bash
# 1. Start with Docker (Recommended)
docker-compose up --build

# 2. Or run locally
mvn spring-boot:run

# 3. Seed database
./scripts/seed.sh

# 4. Test API
curl http://localhost:4000/health
curl http://localhost:4000/api/reports/demo/aisha
```

## Environment Variables

```bash
MONGO_URI=mongodb://localhost:27017/naviksha
JWT_SECRET=your-secret-key
ADMIN_SECRET=admin-secret-123
PORT=4000
```

## API Endpoints

- `POST /api/auth/register` - Register user
- `POST /api/auth/login` - Login user  
- `GET /api/tests` - Get available tests
- `POST /api/tests/{testId}/submit` - Submit test
- `GET /api/reports/{reportId}` - Get report
- `POST /admin/seed` - Seed database (admin)

## Architecture

- **Spring Boot 3** + **MongoDB**
- **JWT Authentication** 
- **RIASEC Scoring Engine**
- **Docker Ready**
- **Comprehensive Testing**

Built for Naviksha AI career guidance platform.