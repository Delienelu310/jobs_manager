import { useEffect, useState } from "react";
import { JobEntitySimple, retrieveJobEntityById } from "../../../api/ilum_resources/jobEntityApi";
import JobScriptMenu from "./JobScriptMenu";
import { QueueTypes, removeJobEntityFromQueue } from "../../../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { NotificationType, useNotificator } from "../../notifications/Notificator";


export interface JobEntityMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
    queueType : string
}

export interface JobEntityMenuArgs{
    jobEntityId : string,
    context : JobEntityMenuContext
    
}

const JobEntityMenu = ({context, jobEntityId} : JobEntityMenuArgs) => {
    

    const {catchRequestError, pushNotification} = useNotificator();

    const [actualData, setActualData] = useState<JobEntitySimple | null>(null);
    
    function retrieve(){
        retrieveJobEntityById(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, jobEntityId)
            .then(response => {
                setActualData(response.data);
            })
            .catch(catchRequestError);
    }

    function deleteJob(){

        removeJobEntityFromQueue(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, context.queueType, jobEntityId)
            .then(r => {

                if(
                    context.jobNodePageRefresh.chosenResourceList && 
                    (
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_QUEUE &&
                        context.queueType == QueueTypes.JOBS_QUEUE
                        ||
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.TESTING_QUEUE &&
                        context.queueType == QueueTypes.TESTING_JOBS
                    )
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }

                context.jobNodePageRefresh.setMenu(null);

                pushNotification({
                    message: "Job entity was removed successfully",
                    time : 5,
                    type : NotificationType.INFO
                });
                
            }).catch(catchRequestError)
        ;
    }

    useEffect(() => {
        retrieve();
    }, []);


    return (
        <>
            {actualData ? 
                <div>
                    <h3>Job Entity Menu</h3>
                    
                    <hr/>

                    <h5>About:</h5>
                    <strong>Name : </strong> {actualData.jobEntityDetails.name}
                    <br/>
                    <strong>Author: </strong>{actualData.author.username}
                    <br/>
                    <strong>Description:</strong>
                    <p>{actualData.jobEntityDetails.description || "no description"}</p>
                    <hr/>

                    
                    <h5>Job Script: {actualData.jobScript.jobScriptDetails.name}</h5>
                    <strong>Class name:</strong>
                    <i>{actualData.jobScript.classFullName}</i>

                    <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptMenu
                        context={context}
                        data={actualData.jobScript.id}
                    />)}>Job scribt</button>
                    <br/>

                    <SecuredNode
                        projectPrivilegeConfig={null}
                        roles={null}
                        moderator={true}
                        alternative={null}
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges : [JobNodePrivilege.MANAGER, context.queueType == QueueTypes.JOBS_QUEUE ? JobNodePrivilege.SCRIPTER : JobNodePrivilege.TESTER]
                        }}
                    >
                        <hr/>

                        <button className="btn btn-danger m-2" onClick={deleteJob}>Remove</button>
                    </SecuredNode>
                   

                </div>
                :
                <h3>Data is Loading...</h3>    
            }
        </>
    );
}

export default JobEntityMenu;