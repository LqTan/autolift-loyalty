FROM python:3.12-slim

WORKDIR /opt/autolift/ml

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY autolift_ml/ ./autolift_ml/
COPY scripts/ ./scripts/
COPY config.yaml .

ENV PYTHONUNBUFFERED=1

CMD ["python", "scripts/run_ml_worker.py"]