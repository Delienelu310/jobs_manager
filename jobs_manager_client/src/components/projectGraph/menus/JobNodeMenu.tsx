import { Link } from "react-router-dom";
import { JobNodeElement } from "../gof/JobNodeElement";

export interface JobNodeMenuArgs{
    element : JobNodeElement
}


const JobNodeMenu = ({element} : JobNodeMenuArgs) => {
    return (
        <div>

            <h3>{element.getData().jobNodeDetails.name}</h3>
            <span>ID: {element.getData().id}</span>

            <Link to={`/projects/${element.getGof().getProjectData().id}/job_nodes/${element.getData().id}`}>More</Link>

        </div>
    );
};

export default JobNodeMenu;