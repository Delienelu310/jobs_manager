import { useEffect, useState } from "react";
import { checkJobsFileState, deleteJobsFile, JobsFileDetails, JobsFileExtension, JobsFileSimple, JobsFileState, retrieveJobsFile, updateJobsFileDetails, updateJobsFileFile } from "../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../pages/JobNodePage";



export interface JobsFileMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobsFileMenuArgs{
    data : JobsFileSimple,
    context : JobsFileMenuContext
}

const JobsFileMenu = ({data, context} : JobsFileMenuArgs) => {


    const [fullData, setFullData] = useState<JobsFileSimple | null>(null);

    const [newJobsFileDetails, setNewJobsFileDetails] = useState<JobsFileDetails>({
        name : "",
        description: ""
    });
    
    const [newFile, setNewFile] = useState<File | null>(null);
    const [newExtension, setNewExtension] = useState<string>(JobsFileExtension.JAR);
    const [state, setState] = useState<JobsFileState>(JobsFileState.UNKNOWN);

    function refresh(){
        retrieveJobsFile(data.project.id, data.jobNode.id, data.id)
            .then(response => setFullData(response.data))
            .catch(e => console.log(e));
    }

    function checkState(){
        checkJobsFileState(data.project.id, data.jobNode.id, data.id)
            .then(response => {
                console.log(response.data);
                setState(response.data)
            })
            .catch(e => console.log(e));
    }

    function deleteJob(){
        deleteJobsFile(data.project.id, data.jobNode.id, data.id)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                
            })
            .catch(e => console.log(e));
    }

    function updateDetails(){
        updateJobsFileDetails(data.project.id, data.jobNode.id, data.id, newJobsFileDetails)
            .then(response => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                            }).catch(e => console.log(e));
    }

    function updateFile(){
        if(!newFile) return;

        updateJobsFileFile(data.project.id, data.jobNode.id, data.id, newExtension, newFile)
            .then(r => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
            }).catch(e => console.log(e));
    }


    useEffect(() => {
        refresh();
    }, []);

    return (
        <div>
            <h3>{fullData && fullData.jobDetails.name}</h3>

            <button className="btn btn-danger" onClick={e => deleteJob()}>Delete</button>
            <br/>

            <strong>{state}</strong>
            <button className="btn btn-primary" onClick={e => checkState()}>Check state</button>
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
            <label>
                Extension:
                <select value={newExtension} onChange={e => setNewExtension(e.target.value)}>
                    {Object.values(JobsFileExtension).map(type => <option value={type}>{type}</option>)}
                </select>
            </label>
            <br/>
            <button className="btn btn-success" onClick={e => updateFile()}>Update file</button>
            <br/>

            <label>
                Name:
                <input value={newJobsFileDetails.name} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, name : e.target.value})}/>
            </label>
            
            <label>
                Description:
                <input value={newJobsFileDetails.description} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, description : e.target.value})}/>
            </label>


            <button className="btn btn-success" onClick={e => updateDetails()}>Update details</button>
    
            <hr/>

        </div>
    );

}


export default  JobsFileMenu;