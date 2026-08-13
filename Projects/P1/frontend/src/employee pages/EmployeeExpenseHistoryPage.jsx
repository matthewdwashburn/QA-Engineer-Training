import { useCallback } from 'react'
import DataTable from '../components/DataTable'
import { useState, useEffect } from 'react'
import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

function EmployeeExpenseHistoryPage() {
    // Global use state variables
    const navigate = useNavigate()
    const [expenseHistory, setExpenseHistory] = useState([])
    const [loading, setLoading] = useState(true)
    const apiUrl = "http://127.0.0.1:5000";
    const [response, setResponse] = useState("")

    const getExpenseHistory = useCallback(async () => {
        try {

            // Get pending expenses
            const result = await fetch(apiUrl + '/expenses/ledger', {
                method: "GET",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                }
            })

            // Parse the JSON
            const data = await result.json();

            // Failed, return to dashboard
            if (!result.ok) {
                setResponse(data.error)
            }

            // Successfully returned pending expenses, display
            setExpenseHistory(data.expense_history || [])

        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [])

    // Load pending expenses once on mount
    useEffect(() => {
        getExpenseHistory()
    }, [getExpenseHistory])


    const columns = [
    { field: 'description', label: 'Description' },
    { field: 'amount', label: 'Amount' },
    { field: 'category', label: 'Category' },
    { field: 'status', label: 'Status' },
    { field: 'manager_comment', label: 'Comment'},
    { field: 'expense_date', label: 'Date'}
    ]

    // If loading, don't show the page unfinished, just show nothing
    if(loading) {
        return (<></>)
    }

    return (
    <div className="page-top">
        <h1>Expense History</h1>
        <Button onClick={() => {navigate('/employee/dashboard')}}>Back to Dashboard</Button>
        <div className="table-container">
        <DataTable
        columns={columns}
        rows={expenseHistory}
        rowKey="expense_id"
        />
        </div>
            <div className={response ? "response-visible" : "response-hidden"}>
                {response || " "}
        </div>
    </div>
)

}
export default EmployeeExpenseHistoryPage