import { useEffect, useState } from "react";
import { checkJobsFileState, deleteJobsFile, JobsFileDetails, JobsFileExtension, JobsFileSimple, JobsFileState, retrieveJobsFile, updateJobsFileDetails, updateJobsFileFile } from "../../../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";



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
            {fullData == null ?
                <h3>Loading...</h3>
                :
                <div>
                    <h3>Jobs File</h3>
                    <hr/>

                    <div>
                        <h5>About:</h5>

                        <strong>Name: {fullData.jobDetails.name}</strong>
                        <strong>Jobs File ID: {fullData.id}</strong>
                        <br/>
                        <strong>Author:</strong> {fullData.publisher.username}
                        <br/>
                        <OpenerComponent
                            closedLabel={<strong>Classes Used:</strong>}
                            openedElement={<>{fullData.allClasses.map(className => <><i>{className}</i> <br/> </>)}</>}
                        />
                        
                        <br/>
                       
                    </div>
                    

                    <hr/>

                    <strong>Current State: {state}</strong>
                    <br/>
                    <button className="btn btn-primary m-3" onClick={e => checkState()}>Check state</button>

               
                    <SecuredNode
                        moderator={true}
                        projectPrivilegeConfig={null}
                        alternative={null}
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.SCRIPTER]
                        }}
                        roles={null}
                    >
                        <hr/>
                        
                        
                        <strong>New File:</strong>
                        <input className="form-control m-2" type="file" onChange={e => {
                            if(e.target.files && e.target.files[0]){
                                setNewFile(e.target.files[0]);
                            }
                        }}/>
                        <strong>Extension:</strong>
                        <select className="form-control m-2" value={newExtension} onChange={e => setNewExtension(e.target.value)}>
                            {Object.values(JobsFileExtension).map(type => <option value={type}>{type}</option>)}
                        </select>

                        <button className="btn btn-success m-3" onClick={e => updateFile()}>Update file</button>


                        <hr/>

                        <strong>Name:</strong>
                        <input className="form-control m-2" value={newJobsFileDetails.name} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, name : e.target.value})}/>


                        <strong>Description:</strong>
                        <input className="form-control m-2" value={newJobsFileDetails.description} onChange={e => setNewJobsFileDetails({...newJobsFileDetails, description : e.target.value})}/>


                        <button className="btn btn-success m-3" onClick={e => updateDetails()}>Update details</button>
                        <hr/>
                        <button className="btn btn-danger m-3" onClick={e => deleteJob()}>Delete</button>

                        
                    </SecuredNode>
                   
                </div>

            }
                    
        </div>
    );

}


export default  JobsFileMenu;