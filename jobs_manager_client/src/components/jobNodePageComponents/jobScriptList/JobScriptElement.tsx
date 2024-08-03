import "../../../css/components/jobNodePageComponent/jobScriptList/jobScriptElement.css"

import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "./JobScriptMenu";
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
        <div  className="job_script_element" onClick={e => {
            context.jobNodePageRefresh.setMenu((
                <JobScriptMenu
                    data={data}
                    context={context}
                />
            ));
        }}>
            <div className="job_script_element_cell">
                <h3>{data.jobScriptDetails.name}</h3>
            </div>
            
            <div className="job_script_element_cell">
                <strong>{data.classFullName}</strong>
            </div>
            <div className="job_script_element_cell">
                <span>{data.id}</span>
            </div>
           
            <div className="job_script_element_cell">
                <strong>{data.extension}</strong>
            </div>

            <div className="job_script_element_cell">
                <span>{data.author.username}</span>
            </div>

            
          
        
        </div>
    );
};

export default JobScriptElement;