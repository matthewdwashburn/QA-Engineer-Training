import { useCallback, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from 'react-bootstrap'
import '../App.css'

// Login Page
// authType: "cookie" for the httpOnly-cookie backend, "bearer" for the token backend
function LoginPage({apiURL, loginSuccessRoute, title, authType = "cookie"}) {
    // global variables
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [error, setError] = useState("")

    // React router navigation
    const navigate = useNavigate();

    const login = useCallback(async () => {

        try {
            // Send username and password to local login endpoint
            const result = await fetch(apiURL, {
                method: "POST",
                // Required for the backend's jwt_token cookie to be stored
                ...(authType === "cookie" ? { credentials: "include" } : {}),
                body: JSON.stringify({
                    username: username || null,
                    password: password || null
                }),

                headers: {
                    "Content-Type": "application/json",
                }
            })

            const data = await result.json();

            // Failed logins come back as { error: "..." } from the employee backend,
            // or { message: "..." } from the manager backend.
            if (!result.ok) {
                setError(data.error || data.message || "Login failed")
                return
            }

            // Save user to local storage
            if (result.ok && data.user) {
                localStorage.setItem("user", JSON.stringify(data.user))
            }

            // Bearer backend returns the token in the body, so stash it for later requests
            if (authType === "bearer" && data.token) {
                localStorage.setItem("jwt_token", data.token)
            }

            // Login successful, route to employee dashboard
            navigate(loginSuccessRoute)

            // Successful logins come back as { message: "...", user: {...} }
            setError(data.message)

        } catch (error) {
            setError(error.message)
        }

    }, [apiURL, username, password, authType, loginSuccessRoute, navigate])


    return (
        <div id="center">
        <h1>Revature Expense Manager</h1>
            <h2>{title}</h2>
            <div className="login-form">
                <div className="input-field">
                    <input
                        required
                        type="text"
                        data-testid="username-text-field"
                        autoComplete="off"
                        className="input"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <label className="user-label">Username</label>
                </div>
                <div className="input-field">
                    <input
                        required
                        type="password"
                        data-testid="password-text-field"
                        autoComplete="off"
                        className="input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <label className="user-label">Password</label>
                </div>
            </div>
            <div className="rowContainer">
                <div className="rowContainer">
                <Button
                data-testid="login-btn"
                onClick={() => login()}
                >Login</Button>
                <Button onClick={() => {navigate('/')}}>Back to Home</Button>
                </div>

            </div>
            <div className={error ? "error-visible" : "error-hidden"} data-testid="login-error-label">
                {error || " "}
            </div>
        </div>
    )
}

export default LoginPage
