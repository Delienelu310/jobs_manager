import { useState } from "react";
import { ProjectSimple } from "../api/abstraction/ProjectClient";

import { FieldType } from "../components/lists/Filter";
import ProjectElement from "../components/lists/listElements/ProjectElement";
import ServerBoundList from "../components/lists/ServerBoundList";
import { createProject, ProjectDetails } from "../api/abstraction/projectApi";
import { useNavigate } from "react-router-dom";
import { updateProjectGraph } from "../api/ui/projectGraphApi";


export interface ProjectListPageContext{

}

const ProjectListPage = () => {
    
    const navigate = useNavigate();

    const [projectDetails, setProjectDetails] = useState<ProjectDetails>({
        description : "",
        name : ""
    });

    function create(){
        createProject(projectDetails)
            .then(response => {
                updateProjectGraph(response.data)
                    .then(r => navigate(`/projects/${response.data}`));
                
            })
            .catch();
        ;
    }
    
    return (
        <div>
            <h4>Create project:</h4>

            <div>
                <label>
                    Name : <input value={projectDetails.name} onChange={e => setProjectDetails({...projectDetails, name : e.target.value})}/>
                </label>
                <br/>
                <label>
                    Description : <input value={projectDetails.description} onChange={e => setProjectDetails({...projectDetails, description : e.target.value})}/>
                </label>
                <br/>

                <button onClick={create}className="btn btn-success m-2">Create</button>
                
            </div>

            <hr/>
            <h4>Project List:</h4>

            <ServerBoundList<ProjectSimple, ProjectListPageContext> 
                pager={{
                    defaultPageSize: 10
                }} 
                filter={{parameters: [{label: "admin", additionalData: [], fieldType: FieldType.SingleInput}]}} 
                Wrapper={ProjectElement}
                endpoint={{
                    resourse: "/projects?",
                    count: "/projects/count?"
                }}
                context={{}}
                dependencies={[]}
            />
        </div>
    );
};


export default ProjectListPage; 