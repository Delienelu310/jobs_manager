import { JobEntitySimple } from "../../../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import JobEntityMenu from "../../JobEntityMenu";
import JobScriptElement from "./JobScriptElement";


export interface JobEntityElementContext{
    jobNodePageRefresh : JobNodePageRefresh
    queueType : QueueTypes,
}

export interface JobEntityElementArgs{
    data : JobEntitySimple,
    context : JobEntityElementContext
}

const JobEntityElement = ({data, context} : JobEntityElementArgs) => {
    return (
        <div>
            <h3>{data.jobEntityDetails.name}</h3>
            <strong>Author: {data.author.username}</strong>
            <br/>
            <h6>Description:</h6>
            <span>{data.jobEntityDetails.description}</span>
            <br/>
            <h5>Job Script: {data.jobScript.jobScriptDetails.name}</h5>
            <strong>Class name:</strong>
            <i>{data.jobScript.classFullName}</i>
            <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptElement
                context={context}
                data={data.jobScript}
            />)}>Job scribt</button>

            <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobEntityMenu
                data={data}
                context={context}
            />)}>More...</button>
        </div>
    )
}

export default JobEntityElement;