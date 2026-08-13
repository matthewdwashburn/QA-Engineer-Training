FROM python:3.11-slim

ENV PYTHONUNBUFFERED=1

ENV FLASK_HOST=0.0.0.0

ENV FLASK_PORT=5000

ENV FLASK_DEBUG=false

WORKDIR /app/employee_app

COPY requirements.txt /app/requirements.txt

RUN pip install --no-cache-dir -r /app/requirements.txt

COPY employee_app/ /app/employee_app/

COPY database/ /app/db-init/

EXPOSE 5000

CMD ["python", "main.py"]