import { useEffect, useState } from "react";
import ProjectGraphComponent from "../components/projectGraph/ProjectGraph";
import { ProjectGraph, retrieveProjectGraph } from "../api/ui/projectGraphApi";
import { Navigate, useParams } from "react-router-dom";
import { ProjectFullData, retrieveProject } from "../api/abstraction/projectApi";
import SecuredNode from "../authentication/SecuredNode";
import { ProjectPrivilege } from "../api/authorization/privilegesApi";
import { Roles } from "../api/authorization/usersApi";
import PrivilegeList from "../components/projectPageComponents/PrivilegeList";
import { useNotificator } from "../components/notifications/Notificator";


export interface AllProjectData{
    projectGraph : ProjectGraph,
    projectFullData : ProjectFullData
}

const ProjectPage = () => {

    const {projectId} = useParams();

    const {catchRequestError} = useNotificator();

    const [allProjectData, setAllProjectData] = useState<AllProjectData>();



    function refresh(){
        if(!projectId) return;

        retrieveProjectGraph(projectId)
            .then(graph => {
                return retrieveProject(projectId)
                    .then(projectFullData => setAllProjectData({projectFullData : projectFullData, projectGraph : graph}))
            }).catch(catchRequestError);
    }

    useEffect(() => {
        refresh();
    }, []);

    return (
        <div>
            {allProjectData && 
                
                <SecuredNode
                    jobNodePrivilegeConfig={null}
                    projectPrivilegeConfig={{
                        project: allProjectData.projectFullData,
                        privileges: null
                    }}
                    moderator={true}
                    roles={null}
                    alternative={<Navigate to={"/welcome"}/>}
                >
                    <ProjectGraphComponent 
                        refresh={refresh}
                        projectGraph={allProjectData.projectGraph} 
                        setProjectGraph={(projectGraph) => setAllProjectData({...allProjectData, projectGraph : projectGraph})}
                        projectFullData={allProjectData.projectFullData}
                        staticConfig={{
                            canvas : {
                                width : 1200,
                                height : 700, 
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

                    <hr/>


                    <PrivilegeList
                        projectData={allProjectData.projectFullData}
                        refresh={refresh}
                    />

                </SecuredNode>
            
            }

        </div>
    );
}
 
export default ProjectPage; 