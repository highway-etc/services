FROM python:3.10-slim
WORKDIR /app
RUN pip install fastapi uvicorn mysql-connector-python
COPY api/ /app/
EXPOSE 8080
CMD ["uvicorn","app:app","--host","0.0.0.0","--port","8080"]