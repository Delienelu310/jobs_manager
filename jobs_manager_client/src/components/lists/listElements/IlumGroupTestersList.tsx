import { useState } from "react";
import { IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import ServerBoundList from "../ServerBoundList";
import TesterMetricsList, { TesterMetricsListContext } from "./TesterMetricsList";
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import { FieldType } from "../Filter";



export interface IlumGroupTestersListContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface  IlumGroupTestersListArgs{
    data : IlumGroupOfJobResultData,
    context : IlumGroupTestersListContext
}

const IlumGroupTestersList = ({data, context} : IlumGroupTestersListArgs) => {
    
    const [isListOpened, setIsListOpened] = useState<boolean>(false);
    
    return (
        <div>
            <h3>{data.ilumGroupDetails.name}</h3>
            {data.ilumGroupDetails.startTime}

            <button className="btn btn-primary m-2" onClick={e => setIsListOpened(!isListOpened)}>{isListOpened ? "Close" : "Open"}</button>
            {isListOpened && <>
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
                        {label: "from", additionalData: [], fieldType: FieldType.SingleInput},
                        {label : "to", additionalData: [], fieldType : FieldType.SingleInput}
                    ]}}
                    pager={{defaultPageSize : 10}}
                />
            </>}
            

        </div>
    );
}


export default IlumGroupTestersList;