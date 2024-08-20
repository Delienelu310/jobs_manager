import { Link, useNavigate } from "react-router-dom";
import { JobNodeElement } from "../gof/JobNodeElement";
import SecuredNode from "../../../authentication/SecuredNode";

export interface JobNodeMenuArgs{
    element : JobNodeElement
}


const JobNodeMenu = ({element} : JobNodeMenuArgs) => {
    
    const navigate = useNavigate();
    
    return (
        <div>

            <h5>Job Node Menu</h5>

            <strong>Name : {element.getData().jobNodeDetails.name}</strong> <br/>
            <strong>ID: {element.getData().id}</strong><br/>

            <SecuredNode
                alternative={null}
                roles={null}
                projectPrivilegeConfig={null}
                moderator
                jobNodePrivilegeConfig={{
                    jobNode: element.getData(),
                    privileges : null
                }}
            >
                <button className="btn btn-primary m-3" onClick={() => navigate(`/projects/${element.getGof().getContext().projectData.id}/job_nodes/${element.getData().id}`)}>More</button>
            </SecuredNode>

        </div>
    );
};

export default JobNodeMenu;