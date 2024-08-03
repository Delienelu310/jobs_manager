import { IlumGroupOfJobResultData } from "../../api/ilum_resources/ilumGroupApi";
import { FieldType } from "../lists/Filter";
import IlumGroupErrorsList, { IlumGroupErrorsContext } from "../lists/listElements/IlumGroupErrorsList";
import ServerBoundList from "../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "./JobNodeResourcesListPanel";



const JobResultsFailedList = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
             <ServerBoundList<IlumGroupOfJobResultData, IlumGroupErrorsContext>
                Wrapper={IlumGroupErrorsList}
                context={{jobNodePageRefresh : context}}
                pager={{defaultPageSize : 10}}
                dependencies={[dependency]}
                filter={{parameters : [
                    {label: "from", additionalData: [], fieldType: FieldType.SingleInput},
                    {label : "to", additionalData: [], fieldType : FieldType.SingleInput}
                ]}}
                endpoint={{
                    count : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups/count?`,
                    resourse : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups?`
                }}
            />
        </div>
    );
}


export default JobResultsFailedList;