import Dropdown from 'react-bootstrap/Dropdown';

function DropDown({ title, dropDownItems, onSelect, selectedKey, styleClass, disabled = false, testId }) {
    const selected = dropDownItems.find(i => i.key === selectedKey);
    const displayTitle = selected ? selected.label : title;

    return (
        <Dropdown className={styleClass} onSelect={(eventKey) => onSelect(eventKey)} data-testid={testId}>
            <Dropdown.Toggle className="back-button" id="dropdown-basic" disabled={disabled}>
                {displayTitle}
            </Dropdown.Toggle>
            <Dropdown.Menu>
                {dropDownItems.map((item) => (
                    <Dropdown.Item
                        key={item.key}
                        eventKey={item.key}
                        active={item.key === selectedKey}
                    >
                        {item.label}
                    </Dropdown.Item>
                ))}
            </Dropdown.Menu>
        </Dropdown>
    );
}

export default DropDown;
