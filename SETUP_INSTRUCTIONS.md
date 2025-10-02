# 🚀 Naviksha AI - Quick Setup Guide

## ⚡ Quick Start (2 Minutes)

### Step 1: Start the Backend

```bash
# Option A: Using Docker (Recommended)
docker-compose up --build

# Wait for "Started NavikshaApplication" message
```

**OR**

```bash
# Option B: Manual Setup
# 1. Start MongoDB
docker run -d -p 27017:27017 --name naviksha-mongo mongo:6.0

# 2. Start Spring Boot backend
cd backend
mvn spring-boot:run

# Wait for "Started NavikshaApplication" message
```

### Step 2: Seed the Database

```bash
# In a new terminal
cd backend
chmod +x scripts/seed.sh
./scripts/seed.sh
```

This creates:
- ✅ Career database (100+ careers)
- ✅ Test questions (vibematch + edustats)
- ✅ Demo user account

### Step 3: Start the Frontend

```bash
# In a new terminal, from project root
npm install
npm run dev
```

Frontend will open at: **http://localhost:5173**

---

## 🎯 Test Your Setup

### 1. Check Backend Health

Open: http://localhost:4000/health

Should show:
```json
{
  "status": "UP",
  "timestamp": "2025-10-02T...",
  "database": "connected"
}
```

### 2. Login to the App

Go to: http://localhost:5173/auth

**Demo Account:**
- **Email**: demo@naviksha.ai  
- **Password**: demo123

**OR**

Click "Create account" to register a new user.

### 3. Take the Assessment

After login:
1. Click "Get Started"
2. Complete the Personality Test (15 questions)
3. Complete the Academic Test (15 questions)
4. View your personalized career report!

---

## 🐛 Troubleshooting

### "Sign in/Sign up doesn't work"

**Problem**: Frontend can't connect to backend

**Solution**:
1. Check if backend is running:
   ```bash
   curl http://localhost:4000/health
   ```

2. If not running, start it:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. Clear browser cache and refresh:
   - Press `Ctrl+Shift+Delete` (Windows/Linux)
   - Press `Cmd+Shift+Delete` (Mac)
   - Select "Cached images and files"
   - Clear and refresh page

### "Backend connection refused"

**Check if ports are free:**
```bash
# Check if port 4000 is in use
lsof -i :4000

# Check if MongoDB port is in use
lsof -i :27017
```

**Kill processes if needed:**
```bash
kill -9 <PID>
```

### "Database is empty / No careers found"

**Re-run the seed script:**
```bash
cd backend
./scripts/seed.sh --force
```

### "Cannot connect to MongoDB"

**If using Docker:**
```bash
# Check if MongoDB container is running
docker ps | grep mongo

# If not, start it
docker run -d -p 27017:27017 --name naviksha-mongo mongo:6.0
```

**If using local MongoDB:**
```bash
# Check MongoDB status
sudo systemctl status mongod

# Start if not running
sudo systemctl start mongod
```

---

## 📋 Environment Variables

Create a `.env` file in the project root:

```bash
# Backend API URL (for local development)
VITE_BACKEND_URL=http://localhost:4000

# For production deployment:
# VITE_BACKEND_URL=https://your-backend.com
```

### Backend Environment (.env or system)

```bash
# Database
MONGO_URI=mongodb://localhost:27017/naviksha

# Security (CHANGE THESE FOR PRODUCTION!)
JWT_SECRET=your-super-secret-jwt-key-min-32-characters
ADMIN_SECRET=your-admin-secret

# Server
PORT=4000
SPRING_PROFILES_ACTIVE=dev
```

---

## 🎓 User Guide

### For Students

1. **Register**: Create your account with email/password
2. **Take Tests**: Complete both assessments (8-12 minutes total)
3. **View Results**: Get personalized career recommendations
4. **Explore Careers**: Click on careers to see details
5. **Download Report**: Export your report as PDF (coming soon)

### For Developers

- **API Docs**: http://localhost:4000/swagger-ui.html (coming soon)
- **Admin Panel**: Use admin endpoints with `X-Admin-Secret` header
- **Database**: Access MongoDB at `mongodb://localhost:27017/naviksha`

---

## 📚 Next Steps

1. **Read Full Documentation**: See `COMPLETE_DOCUMENTATION.md`
2. **Explore API**: Check `backend/README.md` for API reference
3. **Run Tests**: `cd backend && mvn test`
4. **Deploy**: Follow deployment guide in documentation

---

## 🆘 Still Having Issues?

1. **Check Console Logs**: Open Browser DevTools (F12) → Console
2. **Check Backend Logs**: Look at terminal where backend is running
3. **Restart Everything**:
   ```bash
   # Stop all
   docker-compose down
   
   # Start fresh
   docker-compose up --build
   cd backend && ./scripts/seed.sh
   ```

4. **Contact Support**: Check GitHub Issues or documentation

---

## ✅ Success Checklist

- [ ] Backend running at http://localhost:4000
- [ ] Health check shows "UP" status
- [ ] Database seeded with careers and tests
- [ ] Frontend running at http://localhost:5173
- [ ] Can register new account
- [ ] Can login with demo account (demo@naviksha.ai / demo123)
- [ ] Can complete personality test
- [ ] Can view career report

If all checked ✅, you're ready to go! 🎉

---

**Happy Career Exploring! 🚀**
