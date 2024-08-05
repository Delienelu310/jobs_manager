import { useState } from "react";
import "../css/components/opener.css"


interface OpenerComponentArgs{
    closedLabel : JSX.Element,
    openedElement : JSX.Element
}

const OpenerComponent = ({
    closedLabel,
    openedElement
} : OpenerComponentArgs) => {
    
    const [isOpened, setIsOpened] = useState<boolean>(false);
    
    return (
        <div className="opener">

            <button className="opener_button btn btn-primary" onClick={() => {
                setIsOpened(!isOpened)
            }}>{isOpened ? "-" : "+"}</button>

            <div className="opener_container">
                {isOpened ? openedElement :closedLabel}
            </div>
           
        </div>
    );
}


export default OpenerComponent;