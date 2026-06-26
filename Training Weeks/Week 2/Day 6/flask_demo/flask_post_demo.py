from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/sum", methods=['POST'])
def sum():
    # Json data
    data = request.json
    x=data["num1"]
    y=data["num2"]
    return jsonify({"result": x + y})

if __name__ == "__main__":
    app.run(debug=True)