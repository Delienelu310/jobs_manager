import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "../../JobScriptMenu";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";

export interface JobScriptListContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobScriptElementArgs{
    data : JobScriptSimple,
    context : JobScriptListContext
} 

const JobScriptElement = ({data, context} : JobScriptElementArgs) => {
    return (
        <div>
            <h3>{data.jobScriptDetails.name}</h3>
            <span>ID: {data.id}</span>
            <br/>
            <span>Author : {data.author.username}</span>
            <br/>
            <strong>Extension: {data.extension}</strong>
            <br/>
            <h4>Class full name:</h4>
            <strong>{data.classFullName}</strong>

            <button className="btn btn-success" onClick={e => {
                context.jobNodePageRefresh.setMenu((
                    <JobScriptMenu
                        data={data}
                        context={context}
                    />
                ));
            }}>More...</button>
        </div>
    );
};

export default JobScriptElement;