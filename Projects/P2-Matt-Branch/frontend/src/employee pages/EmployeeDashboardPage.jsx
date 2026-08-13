import { Button } from 'react-bootstrap'
import LogOut from '../components/LogOut'
import { useNavigate } from 'react-router-dom'

function EmployeeDashboardPage() {
    const navigate = useNavigate()
    const user = JSON.parse(localStorage.getItem("user") || "null")

    return (
        <div id="center">
            <h1>Employee Expense Dashboard</h1>
            <h2>Welcome, {user?.username}!</h2>
        <div className='rowContainer'>
            <Button
            onClick={() => navigate("/employee/submit-expense")}
            data-testid="submit-expense-btn"
            >Submit Expense</Button>
            <Button
            onClick={() => navigate("/employee/pending-expenses")}
            data-testid="pending-expenses-btn"
            >Pending Expenses
            </Button>
            <Button
            data-testid="expense-history-btn"
            onClick={() => navigate("/employee/expense-history")}
            >Expense History</Button>
            <Button
            data-testid="logout-btn"
            onClick={() => {LogOut()}}
            >Log Out</Button>
        </div>
        </div>
    )
}

export default EmployeeDashboardPage