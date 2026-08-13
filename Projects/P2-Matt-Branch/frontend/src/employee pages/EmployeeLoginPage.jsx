import LoginPage from "../universal pages/LoginPage"
import { EMPLOYEE_API_URL } from '../apiConfig'

// Employee login Page
function EmployeeLoginPage() {
    return (
        <LoginPage apiURL={`${EMPLOYEE_API_URL}/auth/login`} loginSuccessRoute={"/employee/dashboard"} title={"Employee Login"}/>
    )
}

export default EmployeeLoginPage
