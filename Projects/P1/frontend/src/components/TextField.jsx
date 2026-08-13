import "../App.css";

const TextField = ({ label, value, onChange, placeholder = "Enter here", disabled = false, testId }) => (
    <div className="setup-field">
        {label && <label className="setup-label">{label}</label>}
        <input
            type="text"
            value={value}
            className="modal-input"
            onChange={(e) => onChange(e.target.value)}
            placeholder={placeholder}
            disabled={disabled}
            data-testid={testId}
        />
    </div>
);

export default TextField;
