import { useState, useEffect, cloneElement } from 'react'
import { Navigate } from 'react-router-dom'
import { EMPLOYEE_API } from '../config'

// authType "cookie" (employee/Python): httpOnly cookie, /auth/me call IS the check.
// authType "bearer" (manager/Java): token in localStorage, still re-validated via /me.
export default function ProtectedRoute({
    children,
    apiURL = EMPLOYEE_API,
    authType = "cookie",
    meRoute = "/auth/me",
}) {
    const [authState, setAuthState] = useState("loading")
    const [user, setUser] = useState(null)

    useEffect(() => {
        const validateSession = async () => {
            const token = authType === "bearer" ? localStorage.getItem("jwt_token") : null

            if (authType === "bearer" && !token) {
                setAuthState("unauthenticated")
                return
            }

            try {
                const response = await fetch(`${apiURL}${meRoute}`, {
                    ...(authType === "cookie"
                        ? { credentials: "include" }
                        : { headers: { "Authorization": `Bearer ${token}` } }),
                })

                if (!response.ok) {
                    localStorage.removeItem("user")
                    if (authType === "bearer") localStorage.removeItem("jwt_token")
                    setAuthState("unauthenticated")
                    return
                }

                const data = await response.json()
                localStorage.setItem("user", JSON.stringify(data))
                setUser(data)
                setAuthState("authenticated")

            } catch (error) {
                console.error("Auth validation failed:", error.message)
                localStorage.removeItem("user")
                if (authType === "bearer") localStorage.removeItem("jwt_token")
                setAuthState("unauthenticated")
            }
        }

        validateSession()
    }, [apiURL, authType, meRoute])

    if (authState === "loading") {
        return <div>Loading...</div>
    }

    if (authState === "unauthenticated") {
        return <Navigate to="/" replace />
    }

    return cloneElement(children, { user })
}
