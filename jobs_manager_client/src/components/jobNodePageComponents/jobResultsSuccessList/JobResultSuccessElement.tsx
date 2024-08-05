import { JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";

export interface JobResultSuccessElementContext{
    jobNodePageRefresh : JobNodePageRefresh
    metric : string
}

export interface JobResultSuccessElementArgs{
    context : JobResultSuccessElementContext,
    data : JobResultSimple
}

const JobResultSuccessElement = ({data, context} : JobResultSuccessElementArgs) => {
    return (
        <div className="list_table_element list_table_row_6">

            <div className="list_table_cell list_table_cell_special">
                Target:{data.target.jobScriptDetails.name}
            </div>
            <div className="list_table_cell">
                <i>{data.target.classFullName}</i>
            </div>

            <div className="list_table_cell">
                {data.target.author.username}
            </div>

            <div className="list_table_cell">
                {data.jobResultDetails.metrics[context.metric] || "null"}
            </div>
            <div className="list_table_cell">
                {new Date(Number(data.startTime)).toUTCString()}
            </div>
            <div className="list_table_cell">
                {new Date(Number(data.endTime)).toUTCString()}
            </div>
    
        </div>
    );
}

export default JobResultSuccessElement;