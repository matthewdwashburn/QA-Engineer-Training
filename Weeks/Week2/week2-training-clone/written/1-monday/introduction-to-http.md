# Introduction to HTTP

## Learning Objectives
- Understand what HTTP is and why it is the backbone of web communication.
- Describe the client-server model and how a request-response cycle works.
- Identify the key components of an HTTP request and an HTTP response.
- Distinguish between the most common HTTP methods (GET, POST, PUT, DELETE).
- Interpret common HTTP status codes and understand what they communicate.

---

## Why This Matters

As a QA Engineer, you will spend a significant portion of your career **testing APIs and web applications**. Whether you are validating a REST API with Postman, writing automated tests in Python or Java, or investigating a bug reported in production, every one of those tasks depends on a solid understanding of HTTP.

HTTP is not just a "developer topic" — it is the universal language of the web. Every time a user logs into an application, submits a form, or loads a page, HTTP is working behind the scenes. When something goes wrong, the HTTP response code is often the first clue. When you write API tests later in this programme (Week 7), you will be crafting HTTP requests and asserting on HTTP responses directly.

This week's **Epic** takes you from Python Mastery to Java Foundations. Before you learn how to handle HTTP requests using Flask (your very next topic today), you need to understand the protocol itself — what is actually being sent and received.

---

## The Concept

### What is HTTP?

**HTTP** stands for **HyperText Transfer Protocol**. It is a text-based, **stateless** application-layer protocol that defines how clients and servers communicate over a network.

- **Stateless** means that each request is completely independent. The server does not remember any previous request from the same client unless a mechanism like sessions or tokens is used.
- **Text-based** means the messages are human-readable (unlike binary protocols).

HTTP runs on top of TCP/IP and, by default, operates on **port 80** (or **port 443** for HTTPS, the secure, encrypted version).

---

### The Client-Server Model

HTTP follows a strict **client-server model**:

```
Client (Browser / App / Test Tool)
          |
          |  ---- HTTP Request ---->
          |
       Server (Web App / API)
          |
          |  <--- HTTP Response ----
          |
```

1. The **client** initiates every communication by sending a **request**.
2. The **server** processes the request and returns a **response**.
3. The connection is typically closed (or reused via HTTP keep-alive).

---

### Anatomy of an HTTP Request

An HTTP request has three main parts:

| Part | Description |
|---|---|
| **Request Line** | The HTTP method, the target URL path, and the HTTP version |
| **Headers** | Metadata about the request (e.g., content type, authorization token) |
| **Body** | Optional payload — used mainly with POST, PUT, PATCH |

**Example — A raw GET request:**

```
GET /api/users/42 HTTP/1.1
Host: api.example.com
Accept: application/json
Authorization: Bearer eyJhbGci...
```

**Example — A raw POST request:**

```
POST /api/users HTTP/1.1
Host: api.example.com
Content-Type: application/json
Content-Length: 45

{"name": "Alice", "role": "QA Engineer"}
```

---

### HTTP Methods (Verbs)

HTTP methods describe the **intent** of the request — what action the client wants the server to perform.

| Method | Purpose | Has Body? |
|---|---|---|
| **GET** | Retrieve data — never modify anything | No |
| **POST** | Submit data to create a new resource | Yes |
| **PUT** | Replace an existing resource entirely | Yes |
| **PATCH** | Partially update an existing resource | Yes |
| **DELETE** | Remove a resource | No |

> 💡 **Analogy:** Think of HTTP methods like actions at a restaurant. **GET** is "give me the menu." **POST** is "I'd like to place a new order." **PUT** is "cancel my order and replace it with a new one." **DELETE** is "cancel my order entirely."

In RESTful APIs (which you will encounter constantly as a QA engineer), these methods map directly to **CRUD** operations:

| HTTP Method | CRUD Operation |
|---|---|
| GET | **R**ead |
| POST | **C**reate |
| PUT / PATCH | **U**pdate |
| DELETE | **D**elete |

### Idempotency

**Idempotency** is an important property of HTTP methods: a request is idempotent if making the same request multiple times has the same effect as making it once.

| Method | Idempotent? | Safe (no side effects)? | Notes |
|--------|------------|------------------------|-------|
| GET | ✅ Yes | ✅ Yes | Retrieve only, never modifies |
| DELETE | ✅ Yes | ❌ No | Delete once = same result as delete twice |
| PUT | ✅ Yes | ❌ No | Replace whole resource — same every time |
| PATCH | ❌ No (usually) | ❌ No | Partial update may accumulate changes |
| POST | ❌ No | ❌ No | Creates a new resource each time |

> **QA Tip:** Test idempotency explicitly. Call `DELETE /items/42` twice — the second call should return `404`, not `500`. Call `GET /items` ten times — it should never change the data.

---

### Anatomy of an HTTP Response

When the server replies, it sends back an HTTP response with three main parts:

| Part | Description |
|---|---|
| **Status Line** | HTTP version + status code + reason phrase |
| **Headers** | Metadata about the response (e.g., content type, server info) |
| **Body** | The actual content returned — often JSON, HTML, or XML |

**Example — A successful response:**

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 72

{"id": 42, "name": "Alice", "role": "QA Engineer", "active": true}
```

**Example — A not-found response:**

```
HTTP/1.1 404 Not Found
Content-Type: application/json

{"error": "User with ID 999 does not exist."}
```

---

### HTTP Status Codes

Status codes are a three-digit number that immediately tells the client whether the request succeeded and why. They are grouped into five categories:

| Range | Category | Common Codes |
|---|---|---|
| **1xx** | Informational — request received, continuing | `100 Continue` |
| **2xx** | Success — request was received and processed | `200 OK`, `201 Created`, `204 No Content` |
| **3xx** | Redirection — further action needed | `301 Moved Permanently`, `302 Found` |
| **4xx** | Client Error — the request has a problem | `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `422 Unprocessable Entity` |
| **5xx** | Server Error — the server failed to fulfill a valid request | `500 Internal Server Error`, `502 Bad Gateway`, `503 Service Unavailable` |

> 🔍 **QA Tip:** When testing an API, **4xx errors** usually mean the test data or request is wrong. **5xx errors** usually mean there is a bug in the application itself. This distinction is crucial for filing accurate defect reports.

---

### HTTP Headers — Key Examples

Headers are key-value pairs that carry metadata. Here are the most important ones you will encounter:

| Header | Direction | Purpose |
|---|---|---|
| `Content-Type` | Request & Response | Specifies the format of the body (e.g., `application/json`) |
| `Accept` | Request | Tells the server what format the client can handle |
| `Authorization` | Request | Carries credentials (e.g., `Bearer <token>`) |
| `Location` | Response | URL of the newly created resource (used with `201 Created`) |
| `Cache-Control` | Response | Controls caching behaviour |

---

### HTTP vs HTTPS

| | HTTP | HTTPS |
|---|---|---|
| Port | 80 | 443 |
| Encryption | None — data is in plain text | TLS/SSL encryption |
| Use in Production | Never (for sensitive data) | Always |

Any production application handling user data, passwords, or payment information **must** use HTTPS. As a QA engineer, verifying that your application enforces HTTPS is a standard security test case.

---

## Code Example — Making HTTP Requests with Python

Python's built-in `urllib` and the popular third-party `requests` library make it easy to send HTTP requests. Below are simple examples using `requests`.

**Install the library first:**

```bash
pip install requests
```

**Understanding a URL:**

```
https://api.example.com:8080/users/42?sort=name&page=2#section
|------|  |--------------||----||---------||-------------||----|
 scheme    host            port  path       query string  fragment
```

- **Scheme**: `http` or `https` — the protocol to use
- **Host**: the domain name or IP address of the server
- **Port**: optional; defaults to 80 (HTTP) or 443 (HTTPS)
- **Path**: the specific resource on the server
- **Query string**: key=value pairs after `?`, separated by `&` — used for filtering, sorting, pagination
- **Fragment**: `#section` — client-side only, never sent to the server

**GET Request — Retrieve data:**

```python
import requests

response = requests.get("https://jsonplaceholder.typicode.com/users/1")

print(response.status_code)        # 200
print(response.headers["Content-Type"])  # application/json; charset=utf-8
print(response.json())             # Parsed JSON body as a Python dict
```

**POST Request — Send data:**

```python
import requests

payload = {
    "name": "Alice",
    "job": "QA Engineer"
}

response = requests.post(
    "https://jsonplaceholder.typicode.com/users",
    json=payload  # Automatically sets Content-Type: application/json
)

print(response.status_code)  # 201
print(response.json())       # {'name': 'Alice', 'job': 'QA Engineer', 'id': '11'}
```

> ⚠️ **Note:** We are using `jsonplaceholder.typicode.com` — a free public mock API used for testing. It does not actually save data.

**Checking for errors:**

```python
import requests

response = requests.get("https://jsonplaceholder.typicode.com/users/9999")

if response.status_code == 404:
    print("Resource not found!")
elif response.status_code == 200:
    print(response.json())
else:
    print(f"Unexpected status: {response.status_code}")
```

**Using `raise_for_status()` — the professional pattern:**

`raise_for_status()` raises an `HTTPError` exception for any 4xx or 5xx response, making error handling clean and explicit:

```python
import requests

try:
    response = requests.get("https://jsonplaceholder.typicode.com/users/9999")
    response.raise_for_status()   # Raises HTTPError for 4xx/5xx responses
    user = response.json()
except requests.exceptions.HTTPError as e:
    print(f"HTTP error: {e.response.status_code} — {e}")
except requests.exceptions.ConnectionError:
    print("Could not connect to the server")
except requests.exceptions.Timeout:
    print("Request timed out")

# In tests, this pattern makes failures visible immediately:
# assert response.status_code == 200  — or
# response.raise_for_status()          — cleaner alternative
```

---

## Summary

| Concept | Key Takeaway |
|---|---|
| **HTTP** | Stateless text protocol for client-server communication |
| **Methods** | GET (read), POST (create), PUT (replace), PATCH (update), DELETE (remove) |
| **Status Codes** | 2xx = success, 4xx = client error, 5xx = server error |
| **Headers** | Metadata — `Content-Type`, `Authorization`, `Accept` |
| **HTTPS** | Encrypted HTTP — mandatory for production systems |
| **QA Relevance** | Foundation for all API testing, defect analysis, and security testing |

HTTP is the universal language of modern software. From this point forward in the programme, almost every tool and framework you learn — Flask, REST Assured, Postman, Selenium — communicates using these same fundamental rules.

> 📌 **Coming Up Next:** Now that you understand the HTTP protocol, you will immediately apply it by learning how to **handle HTTP requests with Flask** — Python's lightweight web framework. You will build your own server that responds to client requests.

---

## Additional Resources

- [MDN Web Docs — An Overview of HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview) — Comprehensive, authoritative reference for HTTP concepts, methods, and headers.
- [MDN Web Docs — HTTP Response Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) — Complete reference for every HTTP status code with detailed explanations.
- [Requests Library Documentation](https://requests.readthedocs.io/en/latest/) — Official docs for the Python `requests` library, the industry-standard tool for making HTTP requests in Python.
