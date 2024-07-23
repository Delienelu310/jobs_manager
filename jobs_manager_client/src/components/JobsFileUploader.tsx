import { useState } from "react";
import { JobsFileDetails, JobsFileExtension, uploadJobsFile } from "../api/ilum_resources/jobsFilesApi";


export interface JobsFileUplaoderArgs{
    projectId : string,
    jobNodeId : string
}

const JobsFileUploader = ({projectId, jobNodeId} : JobsFileUplaoderArgs) => {

    const [file, setFile] = useState<File | null>(null);
    const [extension, setExtension] = useState<string>("jar");
    const [jobsFileDetails, setJobsFileDetails] = useState<JobsFileDetails>({
        name : "",
        description: ""
    });


    return (
        <div>
            <label>
                Name: 
                <input value={jobsFileDetails.name} onChange={e => setJobsFileDetails({...jobsFileDetails, name : e.target.value})}/>
            </label>
            <br/>
            <label>
                Extension: 
                    <select value={extension} onChange={e => setExtension(e.target.value)}>
                    {Object.values(JobsFileExtension).map(extension => (
                        <option value={extension}>
                            {extension}
                        </option>
                    ))}
                </select>
            </label>
            
            <br/>
            <label>
                Description: 
                <input value={jobsFileDetails.description} onChange={e => setJobsFileDetails({...jobsFileDetails, description : e.target.value})}/>
            </label>
            <br/>
            <label>
                JobsFile: 
                <input type="file" onChange={e => {
                    if(!e.target.files) return;
                    if(!e.target.files[0]) return;

                    setFile(e.target.files[0])
                }}/>
            </label>
            <br/>
            <button className="btn btn-success" onClick={e => {
                if(file == null) return;

                uploadJobsFile(projectId, jobNodeId, extension, jobsFileDetails, file)
                    .then(r => alert(r.data))
                    .catch(e => console.log(e))
                ;
                
            }}>Upload</button>
        </div>
    );
}


export default JobsFileUploader;