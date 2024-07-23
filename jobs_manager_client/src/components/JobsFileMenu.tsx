import { useState } from "react";
import { JobsFileDetails, JobsFileSimple, JobsFileState } from "../api/ilum_resources/jobsFilesApi";


export interface JobsFileMenu{
    data : JobsFileSimple
}

const JobsFileMenu = ({data} : JobsFileMenu) => {



    const [newJobsFileDetails, setNewJobsFileDetails] = useState<JobsFileDetails>({
        name : "",
        description: ""
    });
    
    const [newFile, setNewFile] = useState<File | null>(null);
    const [state, setState] = useState<JobsFileState>(JobsFileState.UNKNOWN);


    return (
        <div>
            <h3>{data.jobDetails.name}</h3>

            <button className="btn btn-danger">Delete</button>
            <br/>

            <strong>{state}</strong>
            <button className="btn btn-primary">Check state</button>
            <br/>
            
            <label>
                <strong>New File:</strong>
                <input type="file" onChange={e => {
                    if(e.target.files && e.target.files[0]){
                        setNewFile(e.target.files[0]);
                    }
                }}/>
            </label>
            <br/>
            <button className="btn btn-success">Update file</button>
            <br/>

            <label>
                Name:
                <input value={newJobsFileDetails.name} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, name : e.target.value})}/>
            </label>
            
            <label>
                Description:
                <input value={newJobsFileDetails.name} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, description : e.target.value})}/>
            </label>


            <button className="btn btn-success">Update details</button>
    
        </div>
    );

}


export default  JobsFileMenu;