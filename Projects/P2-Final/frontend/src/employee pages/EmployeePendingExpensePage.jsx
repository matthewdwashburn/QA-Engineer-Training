import { useCallback } from 'react'
import DataTable from '../components/DataTable'
import { useState, useEffect } from 'react'
import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import ConfirmationModal from '../components/ConfirmationModal'
import TextField from '../components/TextField'
import NumberField from '../components/NumberField'
import { EMPLOYEE_API } from '../config'

function EmployeePendingExpensePage() {
    // Global use state variables
    const navigate = useNavigate()
    const [pendingExpenses, setPendingExpenses] = useState([])
    const [loading, setLoading] = useState(true)
    const apiUrl = EMPLOYEE_API;
    const [response, setResponse] = useState("")

    // Delete modal
    const [showDeleteModal, setShowDeleteModal] = useState(false)
    const handleDeleteModalClose = () => setShowDeleteModal(false)
    const handleDeleteModalShow = () => setShowDeleteModal(true)

    // Edit modal
    const [showEditModal, setShowEditModal] = useState(false)
    const handleEditModalClose = () => setShowEditModal(false)
    const handleEditModalShow = () => setShowEditModal(true)

    // Currently selected row
    const [selectedRow, setSelectedRow] = useState({})
    const [editAmount, setEditAmount] = useState("")
    const [editDescription, setEditDescription] = useState("")

    const getPendingExpenses = useCallback(async () => {
        try {

            // Get pending expenses
            const result = await fetch(apiUrl + '/expenses/pending', {
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
            setPendingExpenses(data.pending_expenses || [])

        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [])

    const handleDelete = useCallback(async () => {
        try {
            // Get pending expenses
            const result = await fetch(apiUrl + `/expenses/${selectedRow.expense_id}`, {
                method: "DELETE",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                }
            })

            // Parse the JSON
            const data = await result.json();

            // Failed, display error to user
            if (!result.ok) {
                setResponse(data.error)
            } else { // Success, display message, remove selected row, rerender table
                setSelectedRow({})
                getPendingExpenses()
                setResponse(data.message)
            }

        } catch (error) {
            console.error(error.message)
        }

    }, [selectedRow])

    const handleEdit = useCallback(async () => {
        try {
            // Get pending expenses
            const result = await fetch(apiUrl + `/expenses/${selectedRow.expense_id}`, {
                method: "PUT",
                credentials: "include",
                body: JSON.stringify({
                    description: editDescription,
                    amount: editAmount
                }),
                headers: {
                    "Content-Type": "application/json",
                }
            })

            // Parse the JSON
            const data = await result.json();

            // Failed, display error to user
            if (!result.ok) {
                setResponse(data.error)
            } else { // Success, display message, remove selected row, rerender table
                getPendingExpenses()
                setSelectedRow({})
                setResponse(data.message)
            }

        } catch (error) {
            console.error(error.message)
        }

    }, [editAmount, editDescription])

    // Load pending expenses once on mount
    useEffect(() => {
        getPendingExpenses()
    }, [getPendingExpenses])


    const columns = [
    { field: 'description', label: 'Description' },
    { field: 'amount', label: 'Amount' },
    { field: 'category', label: 'Category' },
    { field: 'status', label: 'Status' },
    { field: 'expense_date', label: 'Date'}
    ]

    // If loading, don't show the page unfinished, just show nothing
    if(loading) {
        return (<></>)
    }

    return (
    <div className="page-top">
    <h1>Pending Expenses</h1>
    <Button onClick={() => {navigate('/employee/dashboard')}}
    data-testid="back-to-dashboard-btn"
    >Back to Dashboard</Button>
    <div className="table-container">
    <DataTable
    columns={columns}
    rows={pendingExpenses}
    actions={(row) => (
        <>
        <Button
        size="sm"
        data-testid={`edit-btn-${row.expense_id}`}
        onClick={() => {
            setSelectedRow(row)
            setEditAmount(row.amount)
            setEditDescription(row.description)
            handleEditModalShow()
        }}
        >Edit</Button>{' '}

        <Button
        size="sm"
        variant="danger"
        data-testid={`delete-btn-${row.expense_id}`}
        onClick={() => {
            setSelectedRow(row)
            handleDeleteModalShow()
        }}
        >Delete</Button>
        </>
    )}
    rowKey="expense_id"
    />
    </div>

    <div className={response ? "response-visible" : "response-hidden"}>
                {response || " "}
    </div>

    {/* Delete Expense Modal */}
    <ConfirmationModal show={showDeleteModal}
        handleClose={handleDeleteModalClose}
        dialogClassName="top-modal"
        header={`Delete Expense`}
        body={
        <div className="edit-body">
        <div className="setup-field">
            <span className="setup-label">ID:</span>
            <span>{selectedRow.expense_id}</span>
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
            <span className="setup-label">Status:</span>
            <span>{selectedRow.status}</span>
        </div>
        <strong>Are you sure you want to delete this expense? This action cannot be undone.</strong>
        </div>
        }
        buttons={[
            {
                text: "Delete Expense",
                onClick: handleDelete,
                testId: "confirm-delete-btn",
            },
        ]}
    ></ConfirmationModal>

    {/* Edit Expense Modal */}
    <ConfirmationModal show={showEditModal}
        handleClose={handleEditModalClose}
        dialogClassName="top-modal"
        header={`Edit Pending Expense`}
        body={
        <div className="edit-body">
        <div className="setup-field">
            <span className="setup-label">ID:</span>
            <span>{selectedRow.expense_id}</span>
        </div>
        <TextField
            label={"Description:"}
            value={editDescription}
            onChange={(c) => setEditDescription(c)}
            disabled={loading}
            testId={"edit-description-field"}
        ></TextField>
        <NumberField
            label={"Amount:"}
            value={editAmount}
            disabled={loading}
            onChange={(c) => setEditAmount(c)}
            testId={"edit-amount-field"}
        />
        <div className="setup-field">
            <span className="setup-label">Category:</span>
            <span>{selectedRow.category}</span>
        </div>
        <div className="setup-field">
            <span className="setup-label">Status:</span>
            <span>{selectedRow.status}</span>
        </div>
        </div>
        }
        buttons={[
            {
                text: "Save Changes",
                onClick: handleEdit,
                testId: "confirm-edit-btn",
            },
        ]}
    ></ConfirmationModal>
    </div>
)

}
export default EmployeePendingExpensePage