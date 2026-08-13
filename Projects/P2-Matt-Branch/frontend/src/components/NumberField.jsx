import "../App.css";

const NumberField = ({ label, value, onChange, min = 0, max, step = "1", small = false, disabled = false, testId}) => (
    <div className="setup-field">
        {label && <label className="setup-label">{label}</label>}
        <input
            type="number"
            min={min}
            max={max}
            step={step}
            value={value}
            className={`modal-input${small ? " small" : ""}`}
            onChange={(e) => {
                const v = e.target.value;
                if (v === "") { onChange(v); return; }
                const n = Number(v);
                if (n >= min && (max === undefined || n <= max)) onChange(v);
            }}
            onKeyDown={(e) => { if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault(); }}
            placeholder="Enter here"
            disabled={disabled}
            data-testid={testId}
        />
    </div>
);

export default NumberField;
