import { EMPLOYEE_API } from '../config'

// Logs the user out for either backend.
// authType: "cookie" clears the httpOnly cookie via the backend, "bearer" drops the stored token
async function LogOut(apiURL = EMPLOYEE_API, authType = "cookie") {
    try {
        if (authType === "bearer") {
            // Stateless token — logout is just forgetting it client-side
            localStorage.removeItem("jwt_token")
        } else {
            // httpOnly cookie can only be cleared server-side
            await fetch(apiURL + '/auth/logout', {
                method: "POST",
                credentials: "include",
            })
        }
    } catch (error) {
        console.error(error.message)
    } finally {
        // Return to the welcome page
        window.location.href = '/'
    }
}

export default LogOut
