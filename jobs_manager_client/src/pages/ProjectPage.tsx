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
                                x : 0,
                                y : 0
                            }
                        },  
                        jobNodes : {
                            width : 200,
                            height : 100,
                            plugBarConfig : {
                                x : 10,
                                y : 0,
                                distanceBetween : 20,
                                plugHeight : 20,
                                plugWidth : 40, 
                                width : 30,
                                minHeight : 100
                            }
                        },
                        projectPlugs: {
                            x : 50,
                            y : 50,
                            distanceBetween : 30,
                            plugHeight : 30,
                            plugWidth : 80, 
                            width : 50,
                            minHeight : 300
                        },
                        channels : {
                            height: 50,
                            width : 50
                        }
                    }}
                />
            }

        </div>
    );
}
 
export default ProjectPage; 