import "../../../css/components/lists/commonListsElements.css"


import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "../menu/JobScriptMenu";
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
        <div className="list_table_element list_table_row_5" onClick={e => {
            context.jobNodePageRefresh.setMenu((
                <JobScriptMenu
                    data={data.id}
                    context={context}
                />
            ));
        }}>
            <div className="list_table_cell">
                <h3>{data.jobScriptDetails.name}</h3>
            </div>
            
            <div className="list_table_cell">
                <strong>{data.classFullName}</strong>
            </div>
            <div className="list_table_cell">
                <span>{data.id}</span>
            </div>
           
            <div className="list_table_cell">
                <strong>{data.extension}</strong>
            </div>

            <div className="list_table_cell">
                <span>{data.author.username}</span>
            </div>

            
          
        
        </div>
    );
};

export default JobScriptElement;