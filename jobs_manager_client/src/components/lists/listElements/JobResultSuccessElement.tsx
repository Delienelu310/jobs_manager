import { JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";

export interface JobResultSuccessElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobResultSuccessElementArgs{
    context : JobResultSuccessElementContext,
    data : JobResultSimple
}

const JobResultSuccessElement = ({data, context} : JobResultSuccessElementArgs) => {
    return (
        <div>
            <h3>Result:</h3>
            <div>
                <h5>Success!</h5>
                {Object.keys(data.jobResultDetails.metrics).map(key => <div>{key} : {data.jobResultDetails.metrics[key]}</div>)}    
            </div>
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


        </div>
    );
}

export default JobResultSuccessElement;