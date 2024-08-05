import "../../../css/components/lists/commonListsElements.css"

import { JobEntitySimple } from "../../../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import JobEntityMenu from "../../JobEntityMenu";
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
        <div className="list_table_row_5 list_table_element" onClick={e => context.jobNodePageRefresh.setMenu(<JobEntityMenu
            jobEntityId={data.id}
            context={context}
        />)}>
            <div className="list_table_cell">
                <h3>{data.jobEntityDetails.name}</h3>
            </div>
           
            <div className="list_table_cell">
                <span>{data.jobEntityDetails.description || "no description"}</span>
            </div>
            <div className="list_table_cell list_table_cell_special" 
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
            <div className="list_table_cell">
                <i>{data.jobScript.classFullName}</i>
            </div>
           
            <div className="list_table_cell">
                <strong>Author: {data.author.username}</strong>
            </div>

        </div>
    )
}

export default JobEntityElement;