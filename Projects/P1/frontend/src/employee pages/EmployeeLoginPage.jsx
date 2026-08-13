import LoginPage from "../universal pages/LoginPage"

// Employee login Page
function EmployeeLoginPage() {
    return (
        <LoginPage apiURL={"http://127.0.0.1:5000/auth/login"} loginSuccessRoute={"/employee/dashboard"} title={"Employee Login"}/>
    )
}

export default EmployeeLoginPage
