import { useEffect, useState } from "react";
import { JobEntitySimple, retrieveJobEntityById } from "../api/ilum_resources/jobEntityApi";
import JobScriptMenu from "./JobScriptMenu";
import { QueueTypes, removeJobEntityFromQueue } from "../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh } from "../pages/JobNodePage";


export interface JobEntityMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
    queueType : QueueTypes
}

export interface JobEntityMenuArgs{
    data : JobEntitySimple,
    context : JobEntityMenuContext
    
}

const JobEntityMenu = ({data, context} : JobEntityMenuArgs) => {
    
    const [actualData, setActualData] = useState<JobEntitySimple | null>(null);
    
    function retrieve(){
        retrieveJobEntityById(data.project.id, data.jobNode.id, data.id)
            .then(response => {
                setActualData(response.data);
            })
            .catch(e => console.log(e));
    }

    function deleteJob(){
        if(!actualData) return;
        removeJobEntityFromQueue(data.project.id, data.jobNode.id, context.queueType, actualData.id)
            .then(r => {
                
                const setter = context.jobNodePageRefresh.dependenciesSetters.queueSetters.get(context.queueType);
                if(!setter) throw Error();
                setter(Math.random());

                context.jobNodePageRefresh.setMenu(null);
                
            })
            .catch(e => console.log(e))
        ;
    }

    useEffect(() => {
        retrieve();
    }, []);


    return (
        <>
            {actualData && <div>
                
                <h3>{data.jobEntityDetails.name}</h3>
                <strong>Author: {data.author.username}</strong>
                <br/>
                <h6>Description:</h6>
                <span>{data.jobEntityDetails.description}</span>
                <br/>

                
                <h5>Job Script: {data.jobScript.jobScriptDetails.name}</h5>
                <strong>Class name:</strong>
                <i>{data.jobScript.classFullName}</i>

                <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptMenu
                    context={context}
                    data={data.jobScript}
                />)}>Job scribt</button>
                <br/>

                <button className="btn btn-danger m-2" onClick={deleteJob}>Remove</button>

            </div>}
        </>
    );
}

export default JobEntityMenu;