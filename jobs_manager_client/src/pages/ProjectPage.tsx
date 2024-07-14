import { useEffect, useState } from "react";
import ProjectGraphComponent from "../components/projectGraph/ProjectGraph";
import { ProjectGraph, retrieveProjectGraph } from "../api/ui/projectGraphApi";
import { useParams } from "react-router-dom";

const ProjectPage = () => {

    const {projectId} = useParams();

    const [projectGraph, setProjectGraph] = useState<ProjectGraph>();

    useEffect(() => {
        console.log(projectId);
        if(!projectId) return;
        retrieveProjectGraph(projectId).then(graph => setProjectGraph(graph)).catch(e => console.log(e));
    }, []);

    return (
        <div>
            {projectGraph && <ProjectGraphComponent projectGraph={projectGraph} />}
        </div>
    );
}
 
export default ProjectPage; 