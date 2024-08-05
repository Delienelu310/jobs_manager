
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import MetricsJobResultList, { MetricsJobResultListContext } from "./MetricsJobResultsList";
import ServerBoundList from "../../lists/ServerBoundList";
import OpenerComponent from "../../OpenerComponent";


export interface TesterMetricsListContext{
    jobNodePageRefresh : JobNodePageRefresh,
    ilumGroupId : string,
}

export interface TesterMetricsListArgs{
    context : TesterMetricsListContext,
    data : JobScriptSimple
}

const TesterMetricsList = ({context, data} : TesterMetricsListArgs) => {
    

    return (
        <div className="m-5">

            <OpenerComponent
                closedLabel={
                    <div>
                        <h3>{data.jobScriptDetails.name}</h3>
                        <strong>{data.classFullName}</strong>
                    </div>
                }
                openedElement={
                    <div>
                        <div>
                            <h3>{data.jobScriptDetails.name}</h3>
                            <strong>{data.classFullName}</strong>
                        </div>

                        <hr/>

                        <h3>Metrics</h3>

                        <ServerBoundList<string, MetricsJobResultListContext>
                            context={{jobNodePageRefresh : context.jobNodePageRefresh, ilumGroupId: context.ilumGroupId, testerId : data.id}}
                            Wrapper={MetricsJobResultList}
                            dependencies={[]}
                            filter={{parameters : []}}
                            pager={{defaultPageSize: 10}}
                            endpoint={{
                                resourse: `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}` + 
                                `/job_results/job_scripts/${data.id}/metrics?ilum_group_id=${context.ilumGroupId}&`,
                                count: `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}` + 
                                `/job_results/job_scripts/${data.id}/metrics/count?ilum_group_id=${context.ilumGroupId}&`
                            }}
                        />

                    </div>
                }
            />
     
        </div>
    );
}


export default TesterMetricsList;