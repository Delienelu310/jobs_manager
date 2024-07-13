
import { ProjectSimple } from "../../../api/abstraction/ProjectClient";

const ProjectElement = (projectSimple : ProjectSimple) => {
    return (
        <div>     
            <h4>{projectSimple.projectDetails.name}</h4>
            <span>{projectSimple.id}</span>
            <hr/>
            {projectSimple.projectDetails.description}
            <hr/>
            <b>Admin: ${projectSimple.admin}</b>
        </div>
    );
};

export default ProjectElement;