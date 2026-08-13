import "../App.css";
import { Button } from "react-bootstrap";
import { Modal } from "react-bootstrap";

const ConfirmationModal = ({ show, handleClose, dialogClassName, header, body, buttons}) => {
    return (
        <Modal show={show} onHide={handleClose} dialogClassName={dialogClassName} animation={false} enforceFocus={false} restoreFocus={false}>
            <Button className="modal-close-btn" onClick={handleClose}>&times;</Button>
            <Modal.Header>
                <Modal.Title>{header}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {body}
            </Modal.Body>
            <Modal.Footer className="rowContainer">
                {buttons.map((button, index) =>(
                    <Button key={button.text} data-testid={button.testId} onClick={() => { if(button.close !== false) {handleClose();} button.onClick(); }} className={button.className}>
                        {button.text}
                    </Button>
                ))}
            </Modal.Footer>
        </Modal>
    );
};

export default ConfirmationModal;