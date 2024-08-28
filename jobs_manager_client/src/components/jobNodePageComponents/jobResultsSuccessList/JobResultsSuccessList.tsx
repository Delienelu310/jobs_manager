import { IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import { FieldType } from "../../lists/Filter";
import IlumGroupTestersList, { IlumGroupTestersListContext } from "./IlumGroupTestersList";
import ServerBoundList from "../../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "../JobNodeResourcesListPanel";
import { NotificationType, useNotificator } from "../../notifications/Notificator";
import { clearJobResults } from "../../../api/ilum_resources/jobResultApi";



const JobResultsSuccessList = ({context, dependency} : JobNodeResourceListArgs) => {
    
    const {catchRequestError, pushNotification} = useNotificator();
    
    function clear(){
        clearJobResults(context.projectId, context.jobNodeId, null, null, 
            true, false, false
        ).then(r => {
            context.setChosenResourceList(null);
            pushNotification({
                message: "Successfull job results were cleared",
                time: 5,
                type : NotificationType.INFO
            })
        }).catch(catchRequestError);
    }   

    return (
        <div>
            <button className="btn btn-danger m-2" onClick={clear}>Clear all</button>

            <ServerBoundList<IlumGroupOfJobResultData, IlumGroupTestersListContext>
                context={{jobNodePageRefresh : context}}
                Wrapper={IlumGroupTestersList}
                dependencies={[dependency]}
                endpoint={{
                    count : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups/count?` +
                        + `include_job_errors=false&include_tester_errors=false&include_successfull=true&`,
                    resourse : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups?` + 
                        `include_job_errors=false&include_tester_errors=false&include_successfull=true&`
                }}
                filter={{parameters : [
                    {label: "from", additionalData: [], fieldType: FieldType.SingleDate},
                    {label : "to", additionalData: [], fieldType : FieldType.SingleDate}
                ]}}
                pager={{defaultPageSize : 10}}
            />
        </div>
    );
}

export default JobResultsSuccessList;