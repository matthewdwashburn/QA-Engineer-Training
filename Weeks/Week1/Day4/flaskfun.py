from flask import Flask, request
app: Flask = Flask(__name__) # this tells flask where to look for resources (name is reference to the module it resides in)

count = 0

data_set = {"1":"some data", "two":"more data"}

# curl http://localhost:5000/
@app.route("/", methods = ["GET"])
def hello_world():
    return "Hello World"

# curl http://localhost:5000/greeting/Matt
@app.route("/greeting/<name>", methods=["GET"])
def greeting(name:str) -> str:
    return f"Hello {name}"

# curl http://localhost:5000/3/add/5
@app.route("/<num1>/add/<num2>", methods=["GET"])
def addition(num1:str, num2:str) -> str:
    result = int(num1) + int(num2)
    return str(result)

# curl -X POST http://localhost:5000/login -H "Content-Type: application/json" -d '{"username":"good","password":"correct"}'
@app.route("/login", methods=["POST"])
def login():
    credentials:dict = request.get_json() # sets our variable to the JSON dictionary values
    username:str = credentials["username"]
    password:str = credentials["password"]
    if username == "good" and password == "correct":
        return "your credentials are good"
    else:
        return "your credentials are bad"

# curl -X PUT http://localhost:5000/count
@app.route("/count", methods=["PUT"])
def add_count():
    global count
    count += 1
    return f"The count is {count}"

# curl "http://localhost:5000/data?DB=1"
@app.get("/data") # route will look like this: http://domain:port/data?query_param=value
def query_database():
    query:str = request.args.get("DB", "")
    if query == "":
        return data_set
    else:
        return data_set[query]

app.run()