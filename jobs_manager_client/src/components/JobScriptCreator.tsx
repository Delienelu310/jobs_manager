import { useState } from "react";
import { createJobScript, JobScriptDTO } from "../api/ilum_resources/jobScriptsApi";
import { JobsFileExtension } from "../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh } from "../pages/JobNodePage";


export interface JobScriptCreatorContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobScriptCreatorArgs{
    projectId : string,
    jobNodeId : string,
    context : JobScriptCreatorContext
}


const JobScriptCreator = ({projectId, jobNodeId, context} : JobScriptCreatorArgs) => {
    
    const [jobScriptDTO, setJobScriptDTO] = useState<JobScriptDTO>({
        extension : JobsFileExtension.JAR,
        jobScriptDetails : {
            name : ""
        },
        classFullName : ""
    });

    function create(){
        createJobScript(projectId, jobNodeId, jobScriptDTO)
            .then(response => {
                context.jobNodePageRefresh.dependenciesSetters.setJobSciptsListDependency(Math.random());
                console.log(response.data);
            })
            .catch(e => console.log(e));
    }
    
    return (
        <div>

            <h3>Create Job Script:</h3>
           
            <label className="m-2">
                Name: 
                <input value={jobScriptDTO.jobScriptDetails.name} onChange={e => setJobScriptDTO({
                    ...jobScriptDTO, 
                    jobScriptDetails : {
                        ...(jobScriptDTO.jobScriptDetails),
                        name : e.target.value
                    }
                })}/>
            </label>
            <br/>
             <label className="m-2">
                Class full name
                <input value={jobScriptDTO.classFullName} onChange={e => setJobScriptDTO({...jobScriptDTO, classFullName : e.target.value})}/>
            </label>
            <br/>
            <label className="m-2">
                Extension: 
                <select value={jobScriptDTO.extension} onChange={e => setJobScriptDTO({...jobScriptDTO, extension: e.target.value})}>
                    {Object.values(JobsFileExtension).map(val => <option value={val}>{val}</option>)}
                </select>
            </label>
           
            <br/>

            <button className="btn btn-success" onClick={create}>Create</button>
        </div>
    );
}

export default JobScriptCreator;