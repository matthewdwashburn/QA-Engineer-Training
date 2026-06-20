from flask import Flask, request, jsonify

app=Flask(__name__)

@app.route("/student", methods=['GET'])
def student():
    data=[{"id": 1, "name":"Alice", "course":"Python"},
          {"id": 2, "name": "Bob", "course": "Python"},
          {"id": 3, "name": "John", "course": "Python"}
          ]
    return jsonify(data)

if __name__ == "__main__":
    app.run(debug=True)