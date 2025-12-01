from fastapi import FastAPI
import mysql.connector
import os

DB_HOST = os.getenv("DB_HOST","127.0.0.1")
DB_USER = os.getenv("DB_USER","root")
DB_PASS = os.getenv("DB_PASS","rootpass")
DB_NAME = os.getenv("DB_NAME","etc_traffic_db")

app = FastAPI()

@app.get("/health")
def health():
    return {"status":"ok"}

@app.get("/traffic/latest")
def latest(limit: int = 100):
    conn = mysql.connector.connect(host=DB_HOST,user=DB_USER,password=DB_PASS,database=DB_NAME)
    cur = conn.cursor(dictionary=True)
    cur.execute("SELECT * FROM traffic_pass_dev ORDER BY pk_id DESC LIMIT %s", (limit,))
    rows = cur.fetchall()
    cur.close(); conn.close()
    return rows