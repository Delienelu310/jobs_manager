import { deleteJobResult, JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";


export interface JobResultElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobResultElementArgs{
    data : JobResultSimple
    context : JobResultElementContext
}

const JobResultElement = ({data, context} : JobResultElementArgs) => {

    function deleteJobResultElement(){
        deleteJobResult(data.project.id, data.jobNode.id, data.id)
            .then(r => {
                // context.jobNodePageRefresh...
            })
            .catch(e => console.log());
    }

    return (
        <div>
            <h3>Result:</h3>
            {data.jobResultDetails.errorMessage ? <div>
                <h5>Error on {data.tester ? "Tester" : "Job"}</h5>    
                <strong>Message : <i>{data.jobResultDetails.errorMessage}</i></strong>
                <br/>
                <strong>Stack trace:</strong><br/>
                <i>{data.jobResultDetails.errorStackTrace}</i>
                <br/>
            </div> : <div>
                <h5>Success!</h5>
                {Object.keys(data.jobResultDetails.metrics).map(key => <div>{key} : {data.jobResultDetails.metrics[key]}</div>)}    
            </div>}
            <h3>Target:</h3>
            <div>
                <h5>{data.target.jobScriptDetails.name}</h5>
                <i>{data.target.classFullName}</i>
                <br/>
                Author : {data.target.author.username} 
            </div>
            {data.tester && <>
              <h3>Tester:</h3>
                <div>
                    <h5>{data.tester.jobScriptDetails.name}</h5>
                    <i>{data.tester.classFullName}</i>
                    <br/>
                    Author : {data.tester.author.username}
                </div>
            </>}

            <button className="btn btn-danger" onClick={deleteJobResultElement}>Delete</button>
          
        </div>
    );
}

export default JobResultElement;