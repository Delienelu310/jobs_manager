import "../../css/components/lists/commonListsElements.css";

import { ProjectSimple } from "../../api/abstraction/ProjectClient";
import { useNavigate } from "react-router-dom";


export interface ProjectElementProps{
    data: ProjectSimple
}

const ProjectElement = ( {data} : ProjectElementProps) => {
    
    const navigate = useNavigate();
    
    return (
        <div className="list_table_element list_table_row_4" onClick={() => navigate(`/projects/${data.id}`)}>     
            
            <div className="list_table_cell">
                <h4>{data.projectDetails.name}</h4>
            </div>
            
            <div className="list_table_cell">{data.id}</div>

            <div className="list_table_cell">{data.projectDetails.description || "no description"}</div>
            
            <div className="list_table_cell"><b>Admin: {data.admin}</b></div>
        </div>
    );
};

export default ProjectElement;