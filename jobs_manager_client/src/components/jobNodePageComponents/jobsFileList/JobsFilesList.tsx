import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileUploader from "./JobsFileUploader";
import { FieldType } from "../../lists/Filter";
import JobsFileElement, { JobsFileListContext } from "./JobsFileElement";
import ServerBoundList from "../../lists/ServerBoundList";
import OpenerComponent from "../../OpenerComponent";
import { JobNodeResourceListArgs } from "../JobNodeResourcesListPanel";


const JobsFilesList = ({context, dependency} : JobNodeResourceListArgs) => {



    return (
        <div>
          

            
            <OpenerComponent
                closedLabel="Upload file"
                openedElement={
                     <JobsFileUploader
                        projectId={context.projectId}
                        jobNodeId={context.jobNodeId}
                    />
                }
            />    
            
            <hr/>
            <h3>Jobs Files:</h3>
            
            
            <ServerBoundList<JobsFileSimple, JobsFileListContext>
                pager={{defaultPageSize: 10}}
                filter={{parameters: [
                    {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                ]}} 
                Wrapper={JobsFileElement}
                endpoint={{
                    resourse : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/jobs_files?`,
                    count : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/jobs_files/count?`
                }}
                context={{jobNodePageRefresh: context}}
                dependencies={[dependency]}
            />
        </div>
    );
}


export default JobsFilesList;