import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { addJobsFileToJobScript, JobScriptSimple, removeJobsFileFromJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { useNotificator } from "../../notifications/Notificator";
import JobsFileMenu from "./JobsFileMenu";


export interface JobsFileAddElementContext{
    jobNodePageRefresh : JobNodePageRefresh,
    refreshJobScript : () => void,
    jobScript : JobScriptSimple
}

export interface JobsFileAddElementArgs{
    data : JobsFileSimple
    context : JobsFileAddElementContext
}

const JobsFileAddElement = ({data, context} : JobsFileAddElementArgs) => {

    const {catchRequestError} = useNotificator();

    function removeJobsFile(){
        removeJobsFileFromJobScript(data.project.id, data.jobNode.id, context.jobScript.id, data.id)
            .then(r => {
                context.refreshJobScript();
            }).catch(catchRequestError);
    }

    function addJobsFile(){
        addJobsFileToJobScript(data.project.id, data.jobNode.id, context.jobScript.id, data.id)
            .then(r => {
                context.refreshJobScript();
            }).catch(catchRequestError);
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

            <strong>Author: </strong> {data.publisher.username}

            <h5>Classes used:</h5>
            <div style={{
                maxHeight: "400px",
                overflow: "scroll"
            }}>
                {data.allClasses.map(cl => <><i>{cl}</i> <br/></>)}
            </div>
           
            <SecuredNode 
                alternative={null}
                projectPrivilegeConfig={null}
                roles={null}
                moderator={true}
                jobNodePrivilegeConfig={{
                    jobNode: context.jobNodePageRefresh.jobNodeData,
                    privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
                }}
            >
                {context.jobScript.jobsFiles.map(jf => jf.id).includes(data.id) ? 
                    <button className="btn btn-danger" onClick={removeJobsFile}>Remove</button>
                    :
                    <button className="btn btn-success" onClick={addJobsFile}>Add</button>
                }
            </SecuredNode>
            
            

            <br/>
            <button className="btn btn-primary m-2" onClick={e => context.jobNodePageRefresh.setMenu((
                <JobsFileMenu
                    data={data}
                    context={{
                        jobNodePageRefresh : context.jobNodePageRefresh
                    }}
                />
            ))}>More...</button>
            <hr/>
        </div>
    );
}

export default JobsFileAddElement;