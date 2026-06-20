from flask import Flask, request
app=Flask(__name__)

@app.route("/")
def home():
    return "Hello Flask"

@app.route("/about")
def about():
    return "About Page"

@app.route("/contact")
def contact():
    return "Contact Page"

# Url Parameter -- path parameter
@app.route("/user/<name>")
def user_name(name):
    return f"Hello {name}"

# Query string
# /user?name="Matthew"
@app.route("/user1")
def user1_name():
    name=request.args.get("name")
    job=request.args.get("job")
    return f"Hello {name}, your job is {job}."


@app.route("/<int:num1>/add/<int:num2>", methods=['POST'])
def add(num1, num2):
    result = num1 + num2
    return str(result)

if __name__ == "__main__":
    app.run(debug=True)