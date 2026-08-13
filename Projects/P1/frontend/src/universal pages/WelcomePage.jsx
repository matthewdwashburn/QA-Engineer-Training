import { useNavigate } from 'react-router-dom'
import { Button } from 'react-bootstrap';

function WelcomePage() {

    const navigate = useNavigate();

    return (
        <div id="center">
        <h1>Revature Expense Manager</h1>
        <div className="rowContainer">
            <Button 
            onClick={() => {navigate('/employee/login')}}
            data-testid='employee-login-btn'
            >Employee Login</Button>

            <Button
            onClick={() => {navigate('/manager/login')}}
            data-testid='manager-login-btn'
            >Manager Login</Button>
        </div>
        </div>
    )
}

export default WelcomePage