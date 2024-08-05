import "../../../css/components/lists/commonListsElements.css"


import { JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import JobScriptMenu from "../menu/JobScriptMenu";
import JobErrorResultMenu from "../menu/JobResultMenu";


export interface JobResultErrorElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobResultErrorElementArgs{
    data : JobResultSimple,
    context : JobResultErrorElementContext
}

const JobResultErrorElement = ({data, context} : JobResultErrorElementArgs) => {
    return (
        <div className="list_table_element list_table_row_5" onClick={
            e => context.jobNodePageRefresh.setMenu((
                <JobErrorResultMenu
                    data={data}
                    context={context}
                />    
            ))
        }>
            <div className="list_table_cell list_table_cell_special"  onClick={e => {
                context.jobNodePageRefresh.setMenu(
                    <JobScriptMenu
                        context={context}
                        data={data.target}
                    />
                )
                e.stopPropagation();
            }}>
                <h4>{data.target.jobScriptDetails.name}</h4>
            </div>
            <div className="list_table_cell">
                {data.target.classFullName}
            </div>

            <div className="list_table_cell">
                Message : {data.jobResultDetails.errorMessage}
            </div>

            <div className="list_table_cell">
                Type : {data.tester ? "Tester" : "Job"}
            </div>

            <div className="list_table_cell">
                End Time : {new Date(Number(data.endTime)).toUTCString()}
            </div>
        </div>
    );
}

export default JobResultErrorElement;