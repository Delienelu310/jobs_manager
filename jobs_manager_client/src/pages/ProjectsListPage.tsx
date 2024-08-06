import { useState } from "react";
import { ProjectSimple } from "../api/abstraction/ProjectClient";

import { FieldType } from "../components/lists/Filter";
import ProjectElement from "../components/projectListPageComponents/ProjectElement";
import ServerBoundList from "../components/lists/ServerBoundList";
import { createProject, ProjectDetails } from "../api/abstraction/projectApi";
import { useNavigate } from "react-router-dom";
import { updateProjectGraph } from "../api/ui/projectGraphApi";
import OpenerComponent from "../components/OpenerComponent";
import { useAuth } from "../authentication/AuthContext";
import { Roles } from "../api/authorization/usersApi";


export interface ProjectListPageContext{

}

const ProjectListPage = () => {

    const {authentication} = useAuth();
    
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
            {authentication && 
                (
                    authentication.roles.includes("ROLE_ADMIN") ||
                    authentication.roles.includes("ROLE_MODERATOR") ||
                    authentication.roles.includes("ROLE_" + Roles.MANAGER)
                )
                &&
                <div className="m-5">
                    <OpenerComponent 
                        closedLabel={<h4>Create Project</h4>}
                        openedElement={
                            <div style={{margin: "30px 10%"}}>
                                <h4 className="m-2">Create project:</h4>
                                <strong>Name :</strong> 
                                <input className="form-control m-2" value={projectDetails.name} 
                                    onChange={e => setProjectDetails({...projectDetails, name : e.target.value})}/>

                                <strong>Description : </strong>
                                <input className="form-control m-2" value={projectDetails.description} 
                                    onChange={e => setProjectDetails({...projectDetails, description : e.target.value})}/>
                            

                                <button onClick={create} className="btn btn-success m-2">Create</button>
                                
                            </div>
                        }
                    />
                </div>
            }
           
           
          
            <h4 className="m">Project List:</h4>

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