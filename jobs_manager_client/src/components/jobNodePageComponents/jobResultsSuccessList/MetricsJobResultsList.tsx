import { useState } from "react";
import { JobResultSimple, retrieveJobResults, retrieveJobResultsCount } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import List, { SourceArg, SourceCountArg } from "../../lists/List";
import JobResultSuccessElement, { JobResultSuccessElementContext } from "./JobResultSuccessElement";
import { FieldType } from "../../lists/Filter";
import { VictoryAxis, VictoryBar, VictoryChart, VictoryLabel, VictoryTheme } from "victory";


export interface MetricsJobResultListContext{
    jobNodePageRefresh : JobNodePageRefresh,
    ilumGroupId : string,
    testerId : string
}

export interface MetricsJobResultListArgs{
    data : string,
    context : MetricsJobResultListContext
}  

const MetricsJobResultList = ({data, context } : MetricsJobResultListArgs) => {
    

    const [isListOpened, setIsListOpened] = useState<boolean>(false);

    const [jobResults, setJobResutls] = useState<JobResultSimple[]>([]);


    function sourceData(args : SourceArg) : Promise<JobResultSimple[]>{


        return retrieveJobResults(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId,
            [
                ["ilum_group_id", context.ilumGroupId],
                ["tester_id", context.testerId],
                ["include_successfull", "true"],
                ["include_job_errors", "false"],
                ["include_tester_errors", "false"],
                ["sort_metric", data],
                ...(Array.from<[string, string[]]>(args.filter.values.entries())
                    .map(([key, value]) => [key, value.join(",")] as [string, string]) 
                )
            ]
            

        ).then(r => {
            console.log(r.data);
            setJobResutls(r.data);
            return r.data;
        });
    }

    function sourceCount(args : SourceCountArg) : Promise<number>{
        return retrieveJobResultsCount(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, [
            
            ["ilum_group_id", context.ilumGroupId],
            ["tester_id", context.testerId],
            ["include_successfull", "true"],
            ["include_job_errors", "false"],
            ["include_tester_errors", "false"],
            ...(Array.from<[string, string[]]>(args.filter.values.entries())
                .map(([key, value]) => [key, value.join(",")] as [string, string]) 
            )
            
        ]).then(r => r.data);
    }
    
    return (
        <div>
            <h5>{data}</h5>
            <button className="btn btn-primary m-2" onClick={e => setIsListOpened(!isListOpened)}>{isListOpened ? "Close" : "Open"}</button>

            <div style={{width: "40%", marginLeft: "30%"}}>
                <VictoryChart
                    theme={VictoryTheme.material}
                    domainPadding={20}
                >
                    <VictoryAxis
                    tickValues={[1, 2, 3, 4]}
                    tickFormat={["Q1", "Q2", "Q3", "Q4"]}
                    />
                    <VictoryAxis
                    dependentAxis
                    tickFormat={(x) => `$${x / 1000}k`}
                    />
                    <VictoryBar
                    data={jobResults.map(jobResult => {
                        return {
                            x: jobResult.target.jobScriptDetails.name , 
                            y : new Number(jobResult.jobResultDetails.metrics[data])
                        }
                    
                    })}
                    // x="quarter"
                    // y="earnings"
                    // labels={({ datum }) => `$${datum.earnings}`}
                    style={{ data: { fill: "#c43a31" }, labels: { fill: "white" } }}
                    labelComponent={<VictoryLabel dy={30}/>}
                    />
                </VictoryChart>
            </div>
           

            {isListOpened && 
                <List<JobResultSimple, JobResultSuccessElementContext>
                    Wrapper={JobResultSuccessElement}
                    context={{jobNodePageRefresh : context.jobNodePageRefresh}}
                    dependencies={[]}
                    filter={{parameters : [
                        {label: "target_id", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "target_author", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "target_classname", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "from", additionalData: [], fieldType: FieldType.SingleInput},
                        {label : "to", additionalData: [], fieldType : FieldType.SingleInput}
                    ]}} 
                    pager={{defaultPageSize : 10}}
                    source={{
                        sourceData: sourceData,
                        sourceCount: sourceCount
                    }}   
                />
            }
        
        </div>
    );
}


export default MetricsJobResultList;