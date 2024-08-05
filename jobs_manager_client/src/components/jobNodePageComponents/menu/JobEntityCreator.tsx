import { useEffect, useState } from "react";
import { addJobEntityToQueue, QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobEntityDetails } from "../../../api/ilum_resources/jobEntityApi";
import { JobScriptSimple, retreiveJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "./JobScriptMenu";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import JobEntityMenu from "./JobEntityMenu";


export interface JobEntityCreatorContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobEntityCreatorArgs{
    projectId : string,
    jobNodeId : string,
    jobScriptId : string,
    context : JobEntityCreatorContext
}

const JobEntityCreator = ({
    projectId,
    jobNodeId,
    jobScriptId,
    context
} : JobEntityCreatorArgs) => {
    


    const [jobEntityDetails, setJobEntityDetails] =  useState<JobEntityDetails>({
        name : "",
        description : ""
    });
    const [configuration, setConfiguration] = useState<string>("");
    const [chosenQueueType, setChosenQueueType] = useState<string>(QueueTypes.JOBS_QUEUE);


    const [jobScriptData, setJobScriptData] = useState<JobScriptSimple | null>(null);

    function getJobScript(){
        retreiveJobScript(projectId, jobNodeId, jobScriptId)
            .then(response => {
                setJobScriptData(response.data);
            })
            .catch(e => console.log(e))
        ;
    }

    function addJob(){
        const queueType : string = chosenQueueType;

        addJobEntityToQueue(projectId, jobNodeId, queueType, jobScriptId, {configuration : configuration, details : jobEntityDetails})
            .then(response => {
                
                if(
                    context.jobNodePageRefresh.chosenResourceList && 
                    (
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_QUEUE &&
                        queueType == QueueTypes.JOBS_QUEUE
                        ||
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.TESTING_QUEUE &&
                        queueType == QueueTypes.TESTING_JOBS
                    )
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                
                context.jobNodePageRefresh.setMenu(<JobEntityMenu
                    jobEntityId={response.data}
                    context={{
                        jobNodePageRefresh: context.jobNodePageRefresh,
                        queueType: queueType
                    }}
                />)
            }).then()
            .catch(e => console.log(e));
        ;
    }

    useEffect(() => {
        getJobScript();
    }, []);
    
    return (
        <div>
            <h3>Job Entity Menu</h3>

            <hr/>

            {jobScriptData ? 
                <>
                    <h5>Chosen script data:</h5>
                    <h5> {jobScriptData.jobScriptDetails.name}</h5>
                    <strong>Class name:</strong>
                    <i>{jobScriptData.classFullName}</i>
                    <br/>
                    <strong>Author: </strong> {jobScriptData.author.username}
                    <br/>
                    <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptMenu
                        context={context}
                        data={jobScriptData.id}
                    />)}>More... </button>

                </>
                :
                <span>Job Script data is loading...</span>

            }

            <hr/>
           
            <h5>Job Entity Data:</h5>
            <strong>Queue type: </strong>
            <select className="form-control m-2" value={chosenQueueType} onChange={e => setChosenQueueType(e.target.value)}>
                {Object.values(QueueTypes).map(type => <option value={type}>{type}</option>)}
            </select>

            <strong>Name:</strong>
            <input className="form-control m-2" value={jobEntityDetails.name} onChange={e => setJobEntityDetails({...jobEntityDetails, name : e.target.value})}/>
        
            <strong>Description:</strong>
            <textarea className="form-control m-2" value={jobEntityDetails.description} onChange={e => setJobEntityDetails({...jobEntityDetails, description : e.target.value})}/>
       
            <strong>Configuration</strong>
            <textarea className="form-control m-2" value={configuration} onChange={e => setConfiguration(e.target.value)}/>
          


            <button className="btn btn-success m-2" onClick={addJob}>Add</button>
        </div>
    );
}

export default JobEntityCreator;