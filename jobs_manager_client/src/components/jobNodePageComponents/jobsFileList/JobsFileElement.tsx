import "../../../css/components/jobNodePageComponent/jobsFileList/jobsFileElement.css"


import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileMenu from "../../jobNodePageComponents/jobsFileList/JobsFileMenu";
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
        <div className="jobs_file_element" onClick={e => context.jobNodePageRefresh.setMenu((
            <JobsFileMenu
                data={data}
                context={{
                    jobNodePageRefresh : context.jobNodePageRefresh
                }}
            />
        ))}>
            <div className="jobs_file_element_cell">
                <h4>{data.jobDetails.name}</h4>
            </div>

            <div className="jobs_file_element_cell">
                <strong>{data.extension}</strong>
            </div>
            <div className="jobs_file_element_cell">
               <span> {data.id}</span>
            </div>
            <div className="jobs_file_element_cell">
                {data.jobDetails.description || "no description"}
            </div>
            <div className="jobs_file_element_cell">
                 {data.allClasses.map(cl => <><i>{cl}</i> </>)}
            </div>
            <div className="jobs_file_element_cell">
                <strong>{data.publisher.username}</strong>
            </div>
        
        </div>
    );
}


export default JobsFileElement;