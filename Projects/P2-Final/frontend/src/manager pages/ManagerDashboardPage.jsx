import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import LogOut from '../components/LogOut'
import { MANAGER_API } from '../config'

function ManagerDashboardPage() {
    const navigate = useNavigate()
    return (
        <div id="center">
            <h1>Manager Expense Dashboard</h1>
        <div className='rowContainer'>
            <Button
            onClick={() => navigate("/manager/expense-review")}
            data-testid="expense-review-btn"
            >Expense Review</Button>
            <Button
            onClick={() => navigate("/manager/expense-report")}
            >Expense Report</Button>
            <Button
            data-testid="logout-btn"
            onClick={() => {LogOut(MANAGER_API, "bearer")}}
            >Log Out</Button>
        </div>
        </div>
    )
}

export default ManagerDashboardPage