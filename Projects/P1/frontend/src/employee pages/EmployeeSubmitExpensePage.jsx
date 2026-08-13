import { useCallback, useState } from "react"
import { Button } from "react-bootstrap"
import DropDown from '../components/DropDown'
import NumberField from "../components/NumberField"
import TextField from "../components/TextField"
import { useNavigate } from "react-router-dom"

function EmployeeSubmitExpensePage() {

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

    const [description, setDescription] = useState("")
    const [amount, setAmount] = useState("")
    const [category, setCategory] = useState("")
    const [date, setDate] = useState("")
    const [response, setResponse] = useState("")
    const apiUrl = "http://127.0.0.1:5000";

    const navigate = useNavigate()

    const handleSubmit = useCallback(async () => {
            try {
                // Get pending expenses
                const result = await fetch(apiUrl + `/expenses/submit`, {
                    method: "POST",
                    credentials: "include",
                    body: JSON.stringify({
                        description: description,
                        amount: amount,
                        category: category,
                        expense_date: date
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
                    setDescription("")
                    setAmount("")
                    setCategory("")
                    setDate("")
                    setResponse(`${data.message} Submit an additional expense or return to dashboard.`)
                }
    
            } catch (error) {
                console.error(error.message)
            }
    
    }, [description, amount, category, date])

    return (
        <div id="center">
            <h1>Submit Expense</h1>
            <div className="form-box">
                <TextField
                label={"Description:"}
                value={description}
                onChange={(c) => setDescription(c)}
                testId={"description-input"}
                ></TextField>
                <NumberField
                label={"Amount:"}
                value={amount}
                onChange={(c) => setAmount(c)}
                testId={"amount-input"}
                />
                <div className="setup-field">
                    <label className="setup-label">Category:</label>
                    <DropDown
                        styleClass="tech-dropdown"
                        title={"Category"}
                        dropDownItems={categories}
                        onSelect={(key) => {setCategory(key)}}
                        selectedKey={category}
                        testId={"category-input"}
                    />
                </div>
                <TextField
                label={"Date:"}
                value={date}
                onChange={(c) => setDate(c)}
                testId={"date-input"}
                ></TextField>
            </div>
            <div className="rowContainer">
            <Button
            onClick={() => handleSubmit()}
            data-testid="submit-expense-confirm-btn"
            >Submit</Button>
            <Button onClick={() => {navigate('/employee/dashboard')}}
            data-testid="back-to-dashboard-btn"   
            >
            Back to Dashboard
            </Button>
            </div>
            <div className={response ? "response-visible" : "response-hidden"} data-testid="response-label">
                    {response || " "}
            </div>
        </div>
    )
}

export default EmployeeSubmitExpensePage