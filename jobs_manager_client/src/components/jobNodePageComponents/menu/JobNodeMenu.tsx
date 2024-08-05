import React from "react";
import "../../../css/components/jobNodePageComponent/menu/jobNodeMenu.css"


export interface JobNodeMenuArgs{
    currentMenu : JSX.Element | null
    setCurrentMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>
}

const JobNodeMenu = ({currentMenu, setCurrentMenu} : JobNodeMenuArgs) => {

    return (
        <div  hidden={currentMenu == null} className="job_node_menu">
            <div className="job_node_menu_closer" onClick={e => setCurrentMenu(null)}>X</div>
            {currentMenu}
        </div>
    );
}

export default JobNodeMenu;