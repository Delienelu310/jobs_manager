import "../../../css/components/lists/commonListsElements.css"



import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileMenu from "../menu/JobsFileMenu";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";

export interface JobsFileListContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobsFileElementArgs{
    data : JobsFileSimple,
    context : JobsFileListContext
}

const JobsFileElement = ({data, context} : JobsFileElementArgs) => {
    return (
        <div className="list_table_element list_table_row_6" onClick={e => context.jobNodePageRefresh.setMenu((
            <JobsFileMenu
                data={data}
                context={{
                    jobNodePageRefresh : context.jobNodePageRefresh
                }}
            />
        ))}>
            <div className="list_table_cell">
                <h4>{data.jobDetails.name}</h4>
            </div>

            <div className="list_table_cell">
                <strong>{data.extension}</strong>
            </div>
            <div className="list_table_cell">
               <span> {data.id}</span>
            </div>
            <div className="list_table_cell">
                {data.jobDetails.description || "no description"}
            </div>
            <div className="list_table_cell">
                 {data.allClasses.map(cl => <><i>{cl}</i> </>)}
            </div>
            <div className="list_table_cell">
                <strong>{data.publisher.username}</strong>
            </div>
        
        </div>
    );
}


export default JobsFileElement;