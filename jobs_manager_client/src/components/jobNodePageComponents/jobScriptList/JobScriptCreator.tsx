import "../../../css/components/jobNodePageComponent/jobScriptList/jobScriptCreator.css"


import { useState } from "react";
import { createJobScript, JobScriptDTO } from "../../../api/ilum_resources/jobScriptsApi";
import { JobsFileExtension } from "../../../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";


export interface JobScriptCreatorContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobScriptCreatorArgs{
    context : JobScriptCreatorContext
}


const JobScriptCreator = ({context} : JobScriptCreatorArgs) => {
    
    const [jobScriptDTO, setJobScriptDTO] = useState<JobScriptDTO>({
        extension : JobsFileExtension.JAR,
        jobScriptDetails : {
            name : ""
        },
        classFullName : ""
    });

    function create(){
        createJobScript(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, jobScriptDTO)
            .then(response => {
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }
            })
            .catch(e => console.log(e));
    }
    
    return (
        <div className="job_script_creator">

            <h3>Create Job Script:</h3>
           
            <strong>Name: </strong>
            <input className="form-control m-2" value={jobScriptDTO.jobScriptDetails.name} onChange={e => setJobScriptDTO({
                ...jobScriptDTO, 
                jobScriptDetails : {
                    ...(jobScriptDTO.jobScriptDetails),
                    name : e.target.value
                }
            })}/>

            <strong>Class full name</strong>
            <input className="form-control m-2" value={jobScriptDTO.classFullName} onChange={e => setJobScriptDTO({...jobScriptDTO, classFullName : e.target.value})}/>

            <strong>Extension: </strong>
            <select className="form-control m-2" value={jobScriptDTO.extension} onChange={e => setJobScriptDTO({...jobScriptDTO, extension: e.target.value})}>
                {Object.values(JobsFileExtension).map(val => <option value={val}>{val}</option>)}
            </select>
           
            
            <button className="btn btn-success" onClick={create}>Create</button>
        </div>
    );
}

export default JobScriptCreator;