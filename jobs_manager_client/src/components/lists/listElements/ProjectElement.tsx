
import { ProjectSimple } from "../../../api/abstraction/ProjectClient";


export interface ProjectElementProps{
    data: ProjectSimple
}

const ProjectElement = ( {data} : ProjectElementProps) => {
    return (
        <div>     
            <h4>{data.projectDetails.name}</h4>
            <span>{data.id}</span>
            <hr/>
            {data.projectDetails.description}
            <hr/>
            <b>Admin: {data.admin}</b>
        </div>
    );
};

export default ProjectElement;