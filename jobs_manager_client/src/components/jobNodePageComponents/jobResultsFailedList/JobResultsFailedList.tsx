import { IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import { FieldType } from "../../lists/Filter";
import IlumGroupErrorsList, { IlumGroupErrorsContext } from "./IlumGroupErrorsList";
import ServerBoundList from "../../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "../JobNodeResourcesListPanel";
import { clearJobResults } from "../../../api/ilum_resources/jobResultApi";
import { NotificationType, useNotificator } from "../../notifications/Notificator";



const JobResultsFailedList = ({context, dependency} : JobNodeResourceListArgs) => {
    
    const {catchRequestError, pushNotification} = useNotificator();

    function clearAllErrors(){
        clearJobResults(context.projectId, context.jobNodeId, null, null, false, true, true)
            .then(r => {
                context.setChosenResourceList(null);
                pushNotification({
                    message: "All Error Job Results were cleared",
                    time : 5,
                    type : NotificationType.INFO
                })
            }).catch(catchRequestError);        
    }
    
    return (
        <div>
            <button className="btn btn-danger m-2" onClick={clearAllErrors}>Clear All</button>
             <ServerBoundList<IlumGroupOfJobResultData, IlumGroupErrorsContext>
                Wrapper={IlumGroupErrorsList}
                context={{jobNodePageRefresh : context}}
                pager={{defaultPageSize : 10}}
                dependencies={[dependency]}
                filter={{parameters : [
                    {label: "from", additionalData: [], fieldType: FieldType.SingleDate},
                    {label : "to", additionalData: [], fieldType : FieldType.SingleDate}
                ]}}
                endpoint={{
                    count : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups/count?`
                        + `include_job_errors=true&include_tester_errors=true&include_successfull=false&`,
                    resourse : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups?`
                        + `include_job_errors=true&include_tester_errors=true&include_successfull=false&`
                }}
            />
        </div>
    );
}


export default JobResultsFailedList;