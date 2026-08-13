import LoginPage from "../universal pages/LoginPage"
import { MANAGER_API_URL } from '../apiConfig'

// Manager login page
function ManagerLoginPage() {
    return (
        <LoginPage apiURL={`${MANAGER_API_URL}/login`} loginSuccessRoute={"/manager/dashboard"} title={"Manager Login"} authType={"bearer"}/>
    )
}

export default ManagerLoginPage