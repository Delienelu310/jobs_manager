import { useState } from "react";
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { JobResultSimple } from "../../../api/ilum_resources/jobResultApi";
import List, { SourceArg, SourceCountArg } from "../List";
import JobResultSuccessElement, { JobResultSuccessElementContext } from "./JobResultSuccessElement";
import MetricsJobResultList, { MetricsJobResultListContext } from "./MetricsJobResultsList";
import ServerBoundList from "../ServerBoundList";


export interface TesterMetricsListContext{
    jobNodePageRefresh : JobNodePageRefresh,
    ilumGroupId : string,
}

export interface TesterMetricsListArgs{
    context : TesterMetricsListContext,
    data : JobScriptSimple
}

const TesterMetricsList = ({context, data} : TesterMetricsListArgs) => {
    
    const [isListOpened, setIsListOpened] = useState<boolean>(false);

    
    
    return (
        <div>
            <h3>{data.jobScriptDetails.name}</h3>
            <strong>{data.classFullName}</strong>
            <button className="btn btn-primary m-2" onClick={e => setIsListOpened(!isListOpened)}>{isListOpened ? "Close" : "Open"}</button>
       
            {isListOpened && <>
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
            </>}
        </div>
    );
}


export default TesterMetricsList;