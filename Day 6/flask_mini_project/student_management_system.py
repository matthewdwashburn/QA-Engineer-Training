from flask import Flask, request, jsonify

students = []

app = Flask(__name__)

@app.route("/students", methods=['GET'])
def get_students():
    return jsonify(students)

@app.route("/students/<id>", methods=['GET'])
def get_student_id(id):
    try:
        # Next is an easy way to find a specific id in a list of dicts
        student = next((s for s in students if s["id"] == int(id)), None)
        if student == None:
            return jsonify({"message": "Student not found."}), 404
        return student
    except:
        return jsonify({"message": "Bad Request"}), 400

@app.route("/students", methods=['POST'])
def post_student():
    try: 
        request_data = request.json
        id = max((s["id"] for s in students), default=0) + 1
        name = request_data["name"]
        course = request_data["course"]
        students.append({"id": id, "name":name, "course":course})
        return jsonify({"message": "Student added successfully"}), 201
    except:
        return jsonify({"message": "Bad Request"}), 400
    
@app.route("/students/<id>", methods=['PUT'])
def put_student(id):
    try:
        request_data = request.json
        request_name = request_data["name"]
        request_course = request_data["course"]
        student = next((s for s in students if s["id"] == int(id)), None)
        if student == None:
            return jsonify({"message": "Student not found."}), 404
        student["name"] = request_name
        student["course"] = request_course
        return jsonify({"message": "Student updated successfully"}), 201
    except:
        return jsonify({"message": "Bad Request"}), 400

@app.route("/students/<id>", methods=['DELETE'])
def delete_student(id):
    try:
        student = next((s for s in students if s["id"] == int(id)), None)
        if student == None:
            return jsonify({"message": "Student not found."}), 404
        # Student exists, remove it from the list, pop is for dict, remove for list
        students.remove(student)
        return jsonify({"message": "Student deleted successfully"}), 200
    except:
        return jsonify({"message": "Bad Request"}), 400

if __name__ == "__main__":
    app.run(debug=True)