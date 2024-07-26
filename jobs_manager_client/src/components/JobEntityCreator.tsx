import { useEffect, useState } from "react";
import { addJobEntityToQueue, QueueTypes } from "../api/ilum_resources/queueOperationsApi";
import { JobEntityDetails } from "../api/ilum_resources/jobEntityApi";
import { JobScriptSimple, retreiveJobScript } from "../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "./JobScriptMenu";
import { JobNodePageRefresh } from "../pages/JobNodePage";
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
                
                const setter = context.jobNodePageRefresh.dependenciesSetters.queueSetters.get(queueType);
                if(!setter) throw Error();
                setter(Math.random());
                
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
            {jobScriptData ? 
                <>
                    <h3>Chosen script data:</h3>
                    <h5> {jobScriptData.jobScriptDetails.name}</h5>
                    <strong>Class name:</strong>
                    <i>{jobScriptData.classFullName}</i>
                    <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptMenu
                        context={context}
                        data={jobScriptData}
                    />)}>Job scribt</button>

                </>
                :
                <span>Job Script data is loading...</span>

            }
           
            <h3>Job Entity Data:</h3>
            <label>Queue type: 
                <select value={chosenQueueType} onChange={e => setChosenQueueType(e.target.value)}>
                    {Object.values(QueueTypes).map(type => <option value={type}>{type}</option>)}
                </select>

            </label>
            <label>
                <strong>Name:</strong>
                <input value={jobEntityDetails.name} onChange={e => setJobEntityDetails({...jobEntityDetails, name : e.target.value})}/>
            </label>
            <br/>
            <label>
                <strong>Description:</strong>
                <input value={jobEntityDetails.description} onChange={e => setJobEntityDetails({...jobEntityDetails, description : e.target.value})}/>
            </label>
            <br/>
            <label>
                <strong>Configuration</strong>
                <input value={configuration} onChange={e => setConfiguration(e.target.value)}/>
            </label>


            <button className="btn btn-success m-2" onClick={addJob}>Add</button>
        </div>
    );
}

export default JobEntityCreator;