import "../../../css/components/jobNodePageComponent/jobsFileList/jobsFileUploader.css"

import { useState } from "react";
import { JobsFileDetails, JobsFileExtension, uploadJobsFile } from "../../../api/ilum_resources/jobsFilesApi";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";


export interface JobsFileUplaoderArgs{
    projectId : string,
    jobNodeId : string,
    jobNodePageRefresh : JobNodePageRefresh
}

const JobsFileUploader = ({projectId, jobNodeId, jobNodePageRefresh} : JobsFileUplaoderArgs) => {

    const [file, setFile] = useState<File | null>(null);
    const [extension, setExtension] = useState<string>(JobsFileExtension.JAR);
    const [jobsFileDetails, setJobsFileDetails] = useState<JobsFileDetails>({
        name : "",
        description: ""
    });


    return (
        <SecuredNode
            roles={null}
            projectPrivilegeConfig={null}
            alternative={<h5>You are not privileged to upload files</h5>}
            moderator
            jobNodePrivilegeConfig={{
                jobNode: jobNodePageRefresh.jobNodeData,
                privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
            }}
        >
            <div className="jobs_file_uploader">
            <strong>Name: </strong> 
                <input className="form-control m-2" value={jobsFileDetails.name} onChange={e => setJobsFileDetails({...jobsFileDetails, name : e.target.value})}/>
                <strong>Extension: </strong>
                <select className="form-control m-2" value={extension} onChange={e => setExtension(e.target.value)}>
                    {Object.values(JobsFileExtension).map(extension => (
                        <option value={extension}>
                            {extension}
                        </option>
                    ))}
                </select>
                

                <strong>Description:</strong> 
                <textarea className="form-control m-2" value={jobsFileDetails.description} onChange={e => setJobsFileDetails({...jobsFileDetails, description : e.target.value})}/>
        
                <strong>JobsFile: </strong>
                <input className="form-control m-2" type="file" onChange={e => {
                    if(!e.target.files) return;
                    if(!e.target.files[0]) return;

                    setFile(e.target.files[0])
                }}/>
                
                <button className="btn btn-success" onClick={e => {
                    if(file == null) return;

                    uploadJobsFile(projectId, jobNodeId, extension, jobsFileDetails, file)
                        .then(r => alert(r.data))
                        .catch(e => console.log(e))
                    ;
                    
                }}>Upload</button>
            </div>
        </SecuredNode>
     
    );
}


export default JobsFileUploader;