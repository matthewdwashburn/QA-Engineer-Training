import "../App.css";
import DropDown from "./DropDown";

// Toolbar dropdown. Each item carries its own onClick handler; the component
// dispatches to the right one internally when DropDown fires its onSelect
// with the chosen key — so callers don't have to maintain a separate switch
// statement to route the selection.
//
// items: [{ key, label, onClick }]
// title: dropdown label (default "Actions"). Useful for reusing this component
//        as e.g. a "Sort By" picker.
// selectedKey: optional, shows which item is currently selected in the trigger.
const ActionsDropDown = ({ items, title = "Actions", selectedKey, testId }) => {
    const handleSelect = (key) => {
        const item = items.find(i => i.key === key);
        if (item?.onClick) item.onClick();
    };

    return (
        <DropDown
            styleClass="tech-dropdown"
            title={title}
            dropDownItems={items}
            onSelect={handleSelect}
            selectedKey={selectedKey}
            testId={testId}
        />
    );
};

export default ActionsDropDown;
