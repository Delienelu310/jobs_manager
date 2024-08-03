import "../../../css/components/jobNodePageComponent/queueList/jobEntityElement.css"

import { JobEntitySimple } from "../../../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import JobEntityMenu from "../../JobEntityMenu";
import JobScriptElement from "../jobScriptList/JobScriptElement";
import JobScriptMenu from "../jobScriptList/JobScriptMenu";


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
        <div className="job_entity_element" onClick={e => context.jobNodePageRefresh.setMenu(<JobEntityMenu
            jobEntityId={data.id}
            context={context}
        />)}>
            <div className="job_entity_element_cell">
                <h3>{data.jobEntityDetails.name}</h3>
            </div>
           
            <div className="job_entity_element_cell">
                <span>{data.jobEntityDetails.description || "no description"}</span>
            </div>
            <div className="job_entity_element_cell job_entity_element_cell_special" 
                onClick={e => {
                    context.jobNodePageRefresh.setMenu(
                        <JobScriptMenu
                            context={context}
                            data={data.jobScript}
                        />
                    )
                    e.stopPropagation();
                }}
            >
                <h5>Job Script: {data.jobScript.jobScriptDetails.name}</h5>
            </div>
            <div className="job_entity_element_cell">
                <i>{data.jobScript.classFullName}</i>
            </div>
           
            <div className="job_entity_element_cell">
                <strong>Author: {data.author.username}</strong>
            </div>

        </div>
    )
}

export default JobEntityElement;