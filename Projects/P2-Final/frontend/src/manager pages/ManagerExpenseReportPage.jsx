
import { useCallback } from 'react'
import DataTable from '../components/DataTable'
import { useState, useEffect } from 'react'
import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import ConfirmationModal from '../components/ConfirmationModal'
import TextField from '../components/TextField'
import { MANAGER_API } from '../config'
import DropDown from '../components/DropDown'
import ActionsDropDown from '../components/ActionsDropDown'

function ManagerExpenseReportPage() {
    // Global use state variables
    const navigate = useNavigate()
    const [expenses, setExpenses] = useState([])
    const [sortBy, setSortBy] = useState("")
    const [employees, setEmployees] = useState([])
    const [selectedEmployeeID, setSelectedEmployeeID] = useState("")
    const [selectedCategory, setSelectedCategory] = useState("")
    const [selectedStartDate, setSelectedStartDate] = useState("")
    const [selectedEndDate, setSelectedEndDate] = useState("")
    const [loading, setLoading] = useState(true)
    const apiUrl = MANAGER_API;
    const jwtToken = localStorage.getItem("jwt_token")
    const [response, setResponse] = useState("")
    const categories = [
        {key: "TRAVEL", label: "Travel"},
        {key: "MEALS", label: "Meals"},
        {key: "LODGING", label: "Lodging"},
        {key: "OFFICE_SUPPLIES", label: "Office Supplies"},
        {key: "EQUIPMENT", label: "Equipment"},
        {key: "SOFTWARE", label: "Software"},
        {key: "TRAINING", label: "Training"},
        {key: "OTHER", label: "Other"}
    ]

    const getEmployeeReports = useCallback(async (employeeId = selectedEmployeeID) => {
        try {

            // Get reports for the selected employee id
            const result = await fetch(apiUrl + '/reports/employee/?userId=' + employeeId, {
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
                setResponse(data.message)
            } else {
                // Successfully returned reports for the selected employee id
                setExpenses(data || [])
            }


        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [selectedEmployeeID])

    const getCategoryReports = useCallback(async (category = selectedCategory) => {
        try {

            // Get reports for the selected category
            const result = await fetch(apiUrl + '/reports/category/?category=' + category, {
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
                setResponse(data.message)
            } else {
                // Successfully returned reports for the selected category
                setExpenses(data || [])
            }

        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [selectedCategory])

    const getDateRangeReports = useCallback(async () => {
        try {

            // Get reports for the selected date range
            const result = await fetch(apiUrl + '/reports/date/?startDate=' + selectedStartDate + '&endDate=' + selectedEndDate, {
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
                setResponse(data.message)
            } else {
                // Successfully returned reports for the selected date range
                setExpenses(data || [])
            }
        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [selectedStartDate, selectedEndDate])

    const getEmployees = useCallback(async () => {
        try {

            // Get reports for the selected date range
            const result = await fetch(apiUrl + '/employees', {
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
                setResponse(data.message)
            } else {
                // Successfully returned reports for the selected date range
                const dataMap = data.map(employee => ({key: String(employee.id), label: employee.username}))
                setEmployees(dataMap || [])
            }
        } catch (error) {
            console.error(error.message)
        } finally {
            setLoading(false)
        }
    }, [selectedStartDate, selectedEndDate])

    const handleSortByEmployee = useCallback(async () => {
        await getEmployees()
        setSortBy("employee")
    })

    const handleSortByCategory = useCallback(() => {
        setSortBy("category")
    })

    const handleSortByDateRange = useCallback(async () => {
        setSortBy("date")
    })


    // Load pending expenses once on mount
    useEffect(() => {
        getEmployees()
    }, [getEmployees])

    const columns = [
    { field: 'id', label: 'Expense ID' },
    { field: 'userId', label: 'Employee ID' },
    { field: 'amount', label: 'Amount' },
    { field: 'description', label: 'Description' },
    { field: 'category', label: 'Category' },
    { field: 'date', label: 'Date' }
    ]

    // If loading, don't show the page unfinished, just show nothing
    if(loading) {
        return (<></>)
    }

    return (
    <div className="page-top">
    <h1>Expense Report</h1>
    <div className='rowContainer'>
        <div className="setup-field">
            <span className="setup-label">Filter By:</span>
        </div>
        <ActionsDropDown title="Filter" selectedKey={sortBy} items={[
        { key: 'employee', label: 'Employee', onClick: handleSortByEmployee },
        { key: 'category', label: 'Category', onClick: handleSortByCategory },
        { key: 'date', label: 'Date', onClick: handleSortByDateRange },
        ]} />
        {sortBy == "employee" && 
            <div className="setup-field">
            <label className="setup-label">Employee:</label>
            <DropDown
                styleClass="tech-dropdown"
                title={"Employee Name"}
                dropDownItems={employees}
                onSelect={(key) => {
                    setSelectedEmployeeID(key)
                    getEmployeeReports(key)
                }}
                selectedKey={selectedEmployeeID}
            />
        </div>
        }
        {sortBy == "category" && 
            <div className="setup-field">
            <label className="setup-label">Category:</label>
            <DropDown
                styleClass="tech-dropdown"
                title={"Category"}
                dropDownItems={categories}
                onSelect={(key) => {
                    setSelectedCategory(key)
                    getCategoryReports(key)
                }}
                selectedKey={selectedCategory}
            />
        </div>
        }
        {sortBy == "date" && 
        <>
        <TextField
            label={"Start Date:"}
            value={selectedStartDate}
            onChange={(c) => setSelectedStartDate(c)}
            disabled={loading}
        ></TextField>
        <TextField
            label={"End Date:"}
            value={selectedEndDate}
            onChange={(c) => setSelectedEndDate(c)}
            disabled={loading}
        ></TextField>
        <Button
        onClick={() => getDateRangeReports()}
        >Search Date Range</Button>
        </>
        }
        <Button onClick={() => {navigate('/manager/dashboard')}}>Back to Dashboard</Button>
    </div>
    <div className="table-container">
    <DataTable
    columns={columns}
    rows={expenses}
    rowKey="id"
    />
    </div>

    <div className={response ? "response-visible" : "response-hidden"}>
                {response || " "}
    </div>
    </div>
)
}
export default ManagerExpenseReportPage