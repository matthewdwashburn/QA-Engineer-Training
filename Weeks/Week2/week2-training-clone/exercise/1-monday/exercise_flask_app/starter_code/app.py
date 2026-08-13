"""
Week 2 Exercise — Flask API with file-backed JSON storage.

TODO:
- Implement GET /findings  -> return list from data/findings.json
- Implement POST /findings -> body: {"title": "...", "severity": "..."}
      assign id = max existing id + 1, append, save file with json.dump + with open
"""

from __future__ import annotations

from pathlib import Path

from flask import Flask, jsonify, request

app = Flask(__name__)
DATA_FILE = Path(__file__).resolve().parent / "data" / "findings.json"


def load_findings():
    raise NotImplementedError


def save_findings(items):
    raise NotImplementedError


@app.get("/findings")
def list_findings():
    raise NotImplementedError


@app.post("/findings")
def create_finding():
    raise NotImplementedError


if __name__ == "__main__":
    app.run(debug=True)
