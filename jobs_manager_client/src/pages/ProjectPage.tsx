import { useEffect, useState } from "react";
import ProjectGraphComponent from "../components/projectGraph/ProjectGraph";
import { ProjectGraph, retrieveProjectGraph } from "../api/ui/projectGraphApi";
import { useParams } from "react-router-dom";
import { ProjectFullData, retrieveProject } from "../api/abstraction/projectApi";

const ProjectPage = () => {

    const {projectId} = useParams();

    const [projectGraph, setProjectGraph] = useState<ProjectGraph>();
    const [projectFullData, setProjectFullData] = useState<ProjectFullData>();

    useEffect(() => {
        console.log(projectId);
        if(!projectId) return;

        retrieveProjectGraph(projectId).then(graph => setProjectGraph(graph)).catch(e => console.log(e));
        retrieveProject(projectId).then(projectFullData => { console.log(projectFullData);setProjectFullData(projectFullData)}).catch(e => console.log(e));
    }, []);

    return (
        <div>
            {projectGraph && projectFullData &&
                <ProjectGraphComponent 
                    projectGraph={projectGraph} 
                    projectFullData={projectFullData}
                    staticConfig={{
                        canvas : {
                            width : 900,
                            height : 500, 
                            padding : {
                                x : 20,
                                y : 20
                            }
                        },  
                        jobNodes : {
                            width : 100,
                            height : 50
                        },
                        projectPlugs: {
                            inputs: {
                                x : 20,
                                y : 20
                            },
                            outputs : {
                                x : 480,
                                y : 20
                            },
                            distanceBetween : 20,
                            plugSize : 20,
                            width : 30
                        }
                    }}
                />
            }

        </div>
    );
}
 
export default ProjectPage; 