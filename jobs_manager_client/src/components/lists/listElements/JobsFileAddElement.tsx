import { addJobsFileToJobScript, JobScriptSimple, removeJobsFileFromJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileMenu from "../../JobsFileMenu";


export interface JobsFileAddElementContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>
    refreshJobScript : () => void,
    jobScript : JobScriptSimple
}

export interface JobsFileAddElementArgs{
    data : JobsFileSimple
    context : JobsFileAddElementContext
}

const JobsFileAddElement = ({data, context} : JobsFileAddElementArgs) => {


    function removeJobsFile(){
        removeJobsFileFromJobScript(data.project.id, data.jobNode.id, context.jobScript.id, data.id)
            .then(r => {
                context.refreshJobScript();
            }).catch(e => console.log(e));
    }

    function addJobsFile(){
        addJobsFileToJobScript(data.project.id, data.jobNode.id, context.jobScript.id, data.id)
            .then(r => {
                context.refreshJobScript();
            }).catch(e => console.log(e));
        ;

    }

    return (
        <div>
            <hr/>
            <h3>{data.jobDetails.name}</h3>
            <span>ID: {data.id}</span>
            <br/>
            <strong>Extension: {data.extension}</strong>
            <br/>
            <h5>Description:</h5>
            <p>
                {data.jobDetails.description}
            </p>
            <h5>Classes used:</h5>
            {data.allClasses.map(cl => <><i>{cl}</i> <br/></>)}
            
            <br/>
            {context.jobScript.jobsFiles.map(jf => jf.id).includes(data.id) ? 
                <button className="btn btn-danger" onClick={removeJobsFile}>Remove</button>
                :
                <button className="btn btn-success" onClick={addJobsFile}>Add</button>
            }
            

            <br/>
            <button className="btn btn-primary" onClick={e => context.setMenu((
                <JobsFileMenu
                    data={data}
                    setMenu={context.setMenu}
                    setJobsFileListDependency={context.setJobsFileListDependency}
                />
            ))}>More...</button>
            <hr/>
        </div>
    );
}

export default JobsFileAddElement;