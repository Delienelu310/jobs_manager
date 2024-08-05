import { IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import { FieldType } from "../../lists/Filter";
import IlumGroupTestersList, { IlumGroupTestersListContext } from "./IlumGroupTestersList";
import ServerBoundList from "../../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "../JobNodeResourcesListPanel";



const JobResultsSuccessList = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
            <ServerBoundList<IlumGroupOfJobResultData, IlumGroupTestersListContext>
                context={{jobNodePageRefresh : context}}
                Wrapper={IlumGroupTestersList}
                dependencies={[dependency]}
                endpoint={{
                    count : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups/count?`,
                    resourse : `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_results/ilum_groups?`
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