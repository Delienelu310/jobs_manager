import { JobScriptSimple, removeJobsFileFromJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import JobsFileMenu from "./JobsFileMenu";


export interface JobsFileRemoveElementContext{
    jobNodePageRefresh : JobNodePageRefresh,
    refreshJobScript : () => void,
    jobScript : JobScriptSimple
}

export interface JobsFileRemoveElementArgs{
    data : JobsFileSimple,
    context : JobsFileRemoveElementContext
}



const JobsFileRemoveElement = ({data, context} : JobsFileRemoveElementArgs) => {


    function removeJobsFile(){
        removeJobsFileFromJobScript(data.project.id, data.jobNode.id, context.jobScript.id, data.id)
            .then(r => {
                context.refreshJobScript();
            }).catch(e => console.log(e));
    }

    return (
        <div>
            <hr/>
            <h3>{data.jobDetails.name}</h3>
            <span>ID: {data.id}</span>
            <br/>
            <strong>Author: </strong> {data.publisher.username}
            <h5>Classes used:</h5>
            {data.allClasses.map(cl => <><i>{cl}</i> <br/></>)}
            <br/>
            
            

            <button className="btn btn-danger m-2" onClick={removeJobsFile}>Remove</button>
            <br/>


            <button className="btn btn-primary" onClick={e => context.jobNodePageRefresh.setMenu((
                <JobsFileMenu
                    data={data}
                    context={{
                        jobNodePageRefresh: context.jobNodePageRefresh
                    }}
                />
            ))}>More...</button>
            <hr/>
        </div>
    );
}


export default JobsFileRemoveElement;