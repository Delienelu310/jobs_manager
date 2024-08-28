
import { IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import ServerBoundList from "../../lists/ServerBoundList";
import TesterMetricsList, { TesterMetricsListContext } from "./TesterMetricsList";
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import { FieldType } from "../../lists/Filter";
import OpenerComponent from "../../OpenerComponent";
import { useNotificator } from "../../notifications/Notificator";
import { clearJobResults } from "../../../api/ilum_resources/jobResultApi";



export interface IlumGroupTestersListContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface  IlumGroupTestersListArgs{
    data : IlumGroupOfJobResultData,
    context : IlumGroupTestersListContext
}

const IlumGroupTestersList = ({data, context} : IlumGroupTestersListArgs) => {

    const {catchRequestError} = useNotificator();

    function clearGroup(){
        clearJobResults(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data.ilumGroupId, null, 
            true, false, false
        ).then(r => {
            context.jobNodePageRefresh.chosenResourceList?.setDependency(Math.random())
        }).catch(catchRequestError);
    }

    return (
        <div className="m-5">

            <OpenerComponent
                closedLabel={
                    <div>
                        <h3 className="m-2">{data.ilumGroupDetails.name || "No name specified"}</h3>
                        <strong>Ilum Group ID: </strong>{data.ilumGroupId}
                        <br/>
                        <strong>When started: </strong>{new Date(Number(data.ilumGroupDetails.startTime)).toUTCString()}
                        <button className="btn btn-danger m-2" onClick={clearGroup}> Clear Group</button>
                    </div>
                }
                openedElement={
                    <div>
                        <div>
                            <h3 className="m-2">{data.ilumGroupDetails.name || "No name specified"}</h3>
                            <strong>Ilum Group ID: </strong>{data.ilumGroupId}
                            <br/>
                            <strong>When started: </strong>{new Date(Number(data.ilumGroupDetails.startTime)).toUTCString()}
                            <br/>
                            <strong>Description: </strong>
                            <p>{data.ilumGroupDetails.description || "No description"}</p>
                            <button className="btn btn-danger m-2" onClick={clearGroup}> Clear Group</button>
                        </div>

                        <hr/>

                        <ServerBoundList<JobScriptSimple, TesterMetricsListContext>
                            Wrapper={TesterMetricsList}
                            context={{jobNodePageRefresh : context.jobNodePageRefresh, ilumGroupId : data.ilumGroupId}}
                            dependencies={[]}
                            endpoint={{
                                count : `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}` + 
                                `/job_results/job_scripts/count?ilum_group_id=${data.ilumGroupId}&`,
                                resourse : `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}` + 
                                `/job_results/job_scripts?ilum_group_id=${data.ilumGroupId}&`
                            }}
                            filter={{parameters : [
                                {label: "tester_author", additionalData : [], fieldType : FieldType.SingleInput},
                                {label: "tester_classname", additionalData : [], fieldType : FieldType.SingleInput},
                                {label: "from", additionalData: [], fieldType: FieldType.SingleDate},
                                {label : "to", additionalData: [], fieldType : FieldType.SingleDate}
                            ]}}
                            pager={{defaultPageSize : 10}}
                        />
                    </div>
                }
            />
            

        </div>
    );
}


export default IlumGroupTestersList;