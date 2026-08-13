import LoginPage from "../universal pages/LoginPage"

// Manager login page
function ManagerLoginPage() {
    return (
        <LoginPage apiURL={"http://127.0.0.1:7001/login"} loginSuccessRoute={"/manager/dashboard"} title={"Manager Login"} authType={"bearer"}/>
    )
}

export default ManagerLoginPage