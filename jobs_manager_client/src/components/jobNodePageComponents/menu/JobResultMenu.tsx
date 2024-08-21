import { act, useEffect, useState } from "react";
import { deleteJobResult, JobResultSimple, retrieveJobResultById } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import OpenerComponent from "../../OpenerComponent";
import JobScriptMenu from "./JobScriptMenu";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { NotificationType, useNotificator } from "../../notifications/Notificator";

export interface JobErrorResultMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobErrorResultMenuAgs{
    context : JobErrorResultMenuContext;
    data : JobResultSimple
}

const JobErrorResultMenu = ({data, context} : JobErrorResultMenuAgs) => {

    const {catchRequestError, pushNotification} = useNotificator();

    const [actualData, setActualData] = useState<JobResultSimple | null>(null);

    function getActualData (){
        retrieveJobResultById(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data.id)
            .then(data => setActualData(data.data))
            .catch(catchRequestError)
        ;
    
    }

    function deleteResult(){
        deleteJobResult(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data.id)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(context.jobNodePageRefresh.chosenResourceList?.label == JobNodeResourceListsMembers.JOB_RESULTS_ERRORS){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }

                pushNotification({
                    message: "Job Result was deleted",
                    type : NotificationType.INFO,
                    time : 5
                })
            }).catch(catchRequestError)
        ;
    }

    useEffect(() => {
        getActualData();
    }, []);

    return (
        <div>
            {actualData ? 
                <div>
                    <h3>Job Result Menu</h3>

                    <hr/>

                    <h5>About:</h5>
                    <strong>Result Type: {
                        actualData.tester ? 
                            ( actualData.jobResultDetails.errorMessage ? 
                                "Tester Error" :
                                "Successful Job"
                            )  :
                            "Job Error"
                    }</strong>
                    <br/>
                    {actualData.jobResultDetails.errorMessage && <>
                        <strong>Error Message:</strong>
                        <p>
                            {actualData.jobResultDetails.errorMessage}
                        </p>
                    </>}
                   
                    <strong>Job Result Id: {actualData.id}</strong>
                    <br/>
                    <strong>Ilum Id :</strong> <i>{actualData.ilumId}</i>
                    <br/>

                    <hr/>

                    <h5>Ilum Group Data</h5>
                    <strong>Ilum Group Id:</strong> <i>{actualData.ilumGroupId}</i>
                    <br/>
                    <strong>Ilum Group Name </strong>{actualData.ilumGroupDetails.name} <br/>
                    <strong>Start Time ms</strong> {actualData.ilumGroupDetails.startTime || "not specified"}
                    <br/>
                    <strong>Start Time </strong> {actualData.ilumGroupDetails.startTime ? 
                        new Date(actualData.ilumGroupDetails.startTime).toUTCString() : "not specified"}
                    <br/>
                    <strong>Description: </strong>
                    <p>{actualData.ilumGroupDetails.description || "no description"}</p>
                
                    <hr/>

                    <h5>Timing:</h5>

                    <strong>Start time ms:</strong> <i>{String(actualData.startTime)}</i>
                    <br/>
                    <strong>Start time: </strong> <i>{ new Date(actualData.startTime).toUTCString()}</i>
                    <br/>
                    <strong>End time ms:</strong> <i>{String(actualData.endTime)}</i>
                    <br/>
                    <strong>Start time: </strong> <i>{ new Date(actualData.endTime).toUTCString()}</i>
                    <br/>
                    <strong>Time taken: </strong> <i>{actualData.endTime - actualData.startTime} ms</i>

                    <hr/>

                    <OpenerComponent
                        closedLabel={<h5>Show Target</h5>}
                        openedElement={
                            <div>
                                <h5>Target</h5>
                                <strong>Name: </strong> {actualData.target.jobScriptDetails.name}<br/>
                                <strong>Class Full Name: </strong><i>{actualData.target.classFullName}</i><br/>
                                <strong>Extension : {actualData.target.classFullName}</strong> <br/>
                                <strong>Id: {actualData.target.id}</strong><br/>
                                <strong>Author: </strong> {actualData.target.author.username}<br/>

                                <button className="btn btn-success m-2" onClick={() => context.jobNodePageRefresh.setMenu(
                                    <JobScriptMenu
                                        context={context}
                                        data={actualData.target.id}
                                    />
                                )}>More...</button>
                                <br/>

                                <strong>Target Configuration:</strong>
                                <p>
                                    {actualData.targetConfiguration || "no configuration"}
                                </p>
                            </div>
                        }
                    />
                    <hr/>

                    {actualData.tester && 
                        <>
                            <OpenerComponent
                                closedLabel={<h5>Show Tester</h5>}
                                openedElement={
                                    <div>
                                        <h5>Tester</h5>
                                        <strong>Name: </strong> {actualData.tester.jobScriptDetails.name}<br/>
                                        <strong>Class Full Name: </strong><i>{actualData.tester.classFullName}</i><br/>
                                        <strong>Extension : {actualData.tester.classFullName}</strong> <br/>
                                        <strong>Id: {actualData.tester.id}</strong><br/>
                                        <strong>Author: </strong> {actualData.tester.author.username}<br/>

                                        <button className="btn btn-success m-2" onClick={() => actualData.tester && context.jobNodePageRefresh.setMenu(
                                            <JobScriptMenu
                                                context={context}
                                                data={actualData.tester.id}
                                            />
                                        )}>More...</button>
                                    </div>
                                }
                            />
                            <hr/>
                        </>
                    }

                    <SecuredNode
                        alternative={null}
                        roles={null}
                        projectPrivilegeConfig={null}
                        moderator
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges: [JobNodePrivilege.MANAGER]
                        }}
                    >
                        <h5>Actions</h5>
                        <button className="btn btn-danger m-2" onClick={deleteResult}>Delete</button>
                        <hr/>
                    </SecuredNode>
                   

                  

                    {actualData.jobResultDetails.errorMessage ? 
                        <>
                            <h5>Stack trace:</h5>
                            <p>{actualData.jobResultDetails.errorStackTrace}</p>
                        </>
                        :
                        <>
                            <h5>Results:</h5>
                            {Object.entries(actualData.jobResultDetails.metrics).map(([metric, value]) => <>
                                <strong>{metric}: </strong>
                                <i>{value}</i>
                                <br/>
                            </>)}

                            <hr/>
                            
                            <h5>Target Raw Result</h5>
                            <p>{actualData.jobResultDetails.resultStr}</p>

                            
                            
                        </>
                    }
                    

                </div>
                :
                <h3>Loading...</h3>
            }

        </div>
    );
}

export default JobErrorResultMenu; 