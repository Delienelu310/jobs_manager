
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import MetricsJobResultList, { MetricsJobResultListContext } from "./MetricsJobResultsList";
import ServerBoundList from "../../lists/ServerBoundList";
import OpenerComponent from "../../OpenerComponent";
import { useNotificator } from "../../notifications/Notificator";
import { clearJobResults } from "../../../api/ilum_resources/jobResultApi";


export interface TesterMetricsListContext{
    jobNodePageRefresh : JobNodePageRefresh,
    ilumGroupId : string,
}

export interface TesterMetricsListArgs{
    context : TesterMetricsListContext,
    data : JobScriptSimple
}

const TesterMetricsList = ({context, data} : TesterMetricsListArgs) => {
    
    const {catchRequestError} = useNotificator();


    function clearTesterResutls(){
        clearJobResults(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, context.ilumGroupId, data.id,
            true, false, false
        ).then(r => {
            context.jobNodePageRefresh.chosenResourceList?.setDependency(Math.random());
        }).catch(catchRequestError);
    }

    return (
        <div className="m-5">

            <OpenerComponent
                closedLabel={
                    <div>
                        <h3>{data.jobScriptDetails.name}</h3>
                        <strong>{data.classFullName}</strong>
                        <button className="btn btn-danger m-2" onClick={clearTesterResutls}>Clear</button>
                    </div>
                }
                openedElement={
                    <div>
                        <div>
                            <h3>{data.jobScriptDetails.name}</h3>
                            <strong>{data.classFullName}</strong>                        
                            <button className="btn btn-danger m-2" onClick={clearTesterResutls}>Clear</button>
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