import LoginPage from "../universal pages/LoginPage"
import { MANAGER_API } from "../config"

// Manager login page
function ManagerLoginPage() {
    return (
        <LoginPage apiURL={`${MANAGER_API}/login`} loginSuccessRoute={"/manager/dashboard"} title={"Manager Login"} authType={"bearer"}/>
    )
}

export default ManagerLoginPage