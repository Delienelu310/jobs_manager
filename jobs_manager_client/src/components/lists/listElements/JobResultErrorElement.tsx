import { JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";


export interface JobResultErrorElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobResultErrorElementArgs{
    data : JobResultSimple,
    context : JobResultErrorElementContext
}

const JobResultErrorElement = ({data, context} : JobResultErrorElementArgs) => {
    return (
        <div>
            <h3>{data.tester ? "Tester" : "Job"} Error!</h3>
            Message : {data.jobResultDetails.errorMessage}
            <br/>
            <button onClick={e => context.jobNodePageRefresh.setMenu(<div></div>)}>More...</button>
        </div>
    );
}

export default JobResultErrorElement;