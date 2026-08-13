import LoginPage from "../universal pages/LoginPage"
import { EMPLOYEE_API } from "../config"

// Employee login Page
function EmployeeLoginPage() {
    return (
        <LoginPage apiURL={`${EMPLOYEE_API}/auth/login`} loginSuccessRoute={"/employee/dashboard"} title={"Employee Login"}/>
    )
}

export default EmployeeLoginPage
