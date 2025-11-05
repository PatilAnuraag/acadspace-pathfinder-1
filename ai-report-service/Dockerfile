FROM python:3.11-slim

WORKDIR /app

# Note: Build context is repo root, so paths must include subdirectory
COPY ai-report-service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY ai-report-service/ .

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
