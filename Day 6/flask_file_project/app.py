"""
Week 2 Exercise — Flask API with file-backed JSON storage.
"""

from __future__ import annotations

from pathlib import Path

from flask import Flask, jsonify, request

import json

app = Flask(__name__)
DATA_FILE = Path(__file__).resolve().parent / "data" / "findings.json"

def load_findings():
    with open(DATA_FILE, "r") as file:
        data = file.read()
        file.close()
    return data

def save_findings(items):
    with open(DATA_FILE, "w") as file:
            json.dump(items, file)
    file.close()

@app.get("/findings")
def list_findings():
    return json.loads(load_findings())

@app.post("/findings")
def create_finding():
    finding = request.json
    current_data = json.loads(load_findings())
    id = max((f["id"] for f in current_data ), default=0) + 1
    title = finding["title"]
    severity = finding["severity"]
    new_finding = {"id": id, "title": title, "severity": severity}
    current_data.append(new_finding)
    save_findings(current_data)
    return jsonify(new_finding), 201

if __name__ == "__main__":
    app.run(debug=True)
