import { useCallback } from 'react'
import DataTable from '../components/DataTable'
import { useState, useEffect } from 'react'
import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import ConfirmationModal from '../components/ConfirmationModal'
import TextField from '../components/TextField'
import DropDown from '../components/DropDown'
import { MANAGER_API_URL } from '../apiConfig'
function ManagerExpenseReviewPage() {
    // Global use state variables
    const navigate = useNavigate()
    const [pendingExpenses, setPendingExpenses] = useState([])
    const [loading, setLoading] = useState(true)
    const apiUrl = MANAGER_API_URL;
    const jwtToken = localStorage.getItem("jwt_token")
    const [response, setResponse] = useState("")

    // Edit modal
    const [showReviewModal, setShowReviewModal] = useState(false)
    const handleReviewModalClose = () => setShowReviewModal(false)
    const handleReviewModalShow = () => setShowReviewModal(true)

    // Currently selected row
    const [selectedRow, setSelectedRow] = useState({})
    const [comment, setComment] = useState("")
    const [status, setStatus] = useState("")

    // Status list
    const statusList = [
        {key: "approved", label: "Approved"},
        {key: "denied", label: "Denied"}
    ]

    const getPendingExpenses = useCallback(async () => {
        try {

            // Get pending expenses
            const result = await fetch(apiUrl + '/expenses/pending', {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwtToken}` 
                }
            })

            // Parse the JSON
            const data = await result.json();

            // Failed, return to dashboard
            if (!result.ok) {
                setResponse(data.error || data.message)
            } else {
                // Successfully returned pending expenses, display
                setPendingExpenses(data || [])
            }


        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [])

    const handleReview = useCallback(async () => {
        try {
            // Get pending expenses
            const result = await fetch(apiUrl + `/expenses/${selectedRow.id}/review`, {
                method: "PUT",
                body: JSON.stringify({
                    status: status,
                    comment: comment
                }),
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwtToken}`
                }
            })

            // Parse the JSON
            const data = await result.json();

            // Failed, display error to user
            if (!result.ok) {
                setResponse(data.error || data.message)
            } else { // Success, display message, remove selected row, rerender table
                getPendingExpenses()
                setSelectedRow({})
                setResponse(data.message)
            }

        } catch (error) {
            console.error(error.message)
        }

    }, [comment, status, selectedRow])

    // Load pending expenses once on mount
    useEffect(() => {
        getPendingExpenses()
    }, [getPendingExpenses])

    const columns = [
    { field: 'userId', label: 'Employee ID' },
    { field: 'description', label: 'Description' },
    { field: 'amount', label: 'Amount' },
    { field: 'category', label: 'Category' },
    { field: 'date', label: 'Date'}
    ]

    // If loading, don't show the page unfinished, just show nothing
    if(loading) {
        return (<></>)
    }

    return (
    <div className="page-top">
    <h1>Expense Review</h1>
    <Button onClick={() => {navigate('/manager/dashboard')}} data-testid="back-to-dashboard-btn">Back to Dashboard</Button>
    <div className="table-container">
    <DataTable
    columns={columns}
    rows={pendingExpenses}
    actions={(row) => (
        <>
        <Button
        size="sm"
        data-testid={`review-btn-${row.id}`}
        onClick={() => {
            setSelectedRow(row)
            setComment("")
            setStatus("")
            handleReviewModalShow()
        }}
        >Review</Button>{' '}

        </>
    )}
    rowKey="id"
    />
    </div>

    <div className={response ? "response-visible" : "response-hidden"} data-testid="review-response-label">
                {response || " "}
    </div>

    {/* Review Expense Modal */}
    <ConfirmationModal show={showReviewModal}
        handleClose={handleReviewModalClose}
        dialogClassName="top-modal"
        header={`Review Expense`}
        body={
        <div className="edit-body">

        <div className="setup-field">
            <span className="setup-label">Employee ID:</span>
            <span>{selectedRow.userId}</span>
        </div>
        <div className="setup-field">
            <span className="setup-label">Description:</span>
            <span>{selectedRow.description}</span>
        </div>
        <div className="setup-field">
            <span className="setup-label">Amount:</span>
            <span>${selectedRow.amount}</span>
        </div>
        <div className="setup-field">
            <span className="setup-label">Category:</span>
            <span>{selectedRow.category}</span>
        </div>

        <div className="setup-field">
            <label className="setup-label">Status:</label>
            <DropDown
                styleClass="tech-dropdown"
                title={"Status"}
                dropDownItems={statusList}
                onSelect={(key) => {setStatus(key)}}
                selectedKey={status}
                testId={"review-status-input"}
            />
        </div>

        <TextField
            label={"Comment:"}
            value={comment}
            onChange={(c) => setComment(c)}
            disabled={loading}
            testId={"review-comment-input"}
        ></TextField>
        </div>
        }
        buttons={[
            {
                text: "Submit Review",
                onClick: handleReview,
                testId: "confirm-review-btn",
            },
        ]}
    ></ConfirmationModal>
    </div>
)

}
export default ManagerExpenseReviewPage