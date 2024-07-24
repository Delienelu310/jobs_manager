import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ServerBoundList from "../components/lists/ServerBoundList";
import { JobsFileSimple } from "../api/ilum_resources/jobsFilesApi";
import JobsFileElement, { JobsFileListContext } from "../components/lists/listElements/JobsFileElement";
import { FieldType } from "../components/lists/Filter";
import JobsFileUploader from "../components/JobsFileUploader";
import { JobScriptSimple } from "../api/ilum_resources/jobScriptsApi";
import JobScriptElement, { JobScriptListContext } from "../components/lists/listElements/JobScriptElement";
import JobScriptCreator from "../components/JobScriptCreator";


export interface JobNodePageInterface{

}


const JobNodePage = ({} : JobNodePageInterface) => {


    const {jobNodeId, projectId} = useParams();

    const [menu, setMenu] = useState<JSX.Element | null>(null);
    const [jobsFilesListDependency, setJobsFileListDependency] = useState<number>(0);
    const [jobScriptsListDependency, setJobSciptsListDependency] = useState<number>(0);


    useEffect(() => {
        console.log(jobNodeId, projectId);
    }, []);

    return (
        <div>

            <div>
                <h3>Current menu</h3>
                {menu}
            </div>

            <hr/>

            {/* <h3>Jobs Files:</h3>
            
            {projectId && jobNodeId && <JobsFileUploader
                projectId={projectId}
                jobNodeId={jobNodeId}
            />}
            
            <hr/>

            
            <ServerBoundList<JobsFileSimple, JobsFileListContext>
                pager={{defaultPageSize: 10}}
                filter={{parameters: [
                    {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                ]}} 
                Wrapper={JobsFileElement}
                endpoint={{
                    resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files`,
                    count : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/count`
                }}
                context={{setMenu, setJobsFileListDependency}}
                dependencies={[jobsFilesListDependency]}
            /> */}


            {projectId && jobNodeId && <JobScriptCreator
                projectId={projectId}
                jobNodeId={jobNodeId}
                setJobSciptsListDependency={setJobSciptsListDependency}
            />}
            <hr/>

            <h3>List of Job Scripts</h3>

            <ServerBoundList<JobScriptSimple, JobScriptListContext>
                pager={{defaultPageSize: 10}}
                endpoint={{
                    resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts`,
                    count: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/count`
                }}
                dependencies={[jobScriptsListDependency]}
                context={{setMenu, setJobSciptsListDependency, setJobsFileListDependency}}
                Wrapper={JobScriptElement}
                filter={{ parameters: [
                    {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection},
                    
                ]}}
            />



            
        </div>
    );


}


export default JobNodePage;