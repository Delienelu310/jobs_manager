import { useState } from "react";
import { JobResultSimple, retrieveJobResults, retrieveJobResultsCount } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import List, { SourceArg, SourceCountArg } from "../../lists/List";
import JobResultSuccessElement, { JobResultSuccessElementContext } from "./JobResultSuccessElement";
import { FieldType } from "../../lists/Filter";
import { VictoryAxis, VictoryBar, VictoryChart, VictoryLabel, VictoryTheme } from "victory";
import OpenerComponent from "../../OpenerComponent";


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
        <div className="m-2">

            <OpenerComponent
                closedLabel={<h5>{data}</h5>}
                openedElement={
                    <div>
                        <h5>{data}</h5>

                        <div>
                            <VictoryChart
                                domainPadding={20}
                            >
                                <VictoryBar
                                    data={jobResults.map(jobResult => {
                                        return {
                                            x: jobResult.target.jobScriptDetails.name , 
                                            y : new Number(jobResult.jobResultDetails.metrics[data])
                                        }
                                    
                                    }).sort((a,b ) => Number(b.y) - Number(a.y))}
                                    labels={({ datum }) => datum.y}
                                    style={{ data: { fill: "#c43a31" }, labels: { fill: "black", fontSize: "20px", fontWeight: "bolder" } }}
                                    labelComponent={<VictoryLabel dy={-10}/>}
                                />
                            </VictoryChart>
                        </div>

                        <List<JobResultSimple, JobResultSuccessElementContext>
                            Wrapper={JobResultSuccessElement}
                            context={{jobNodePageRefresh : context.jobNodePageRefresh, metric: data}}
                            dependencies={[]}
                            filter={{parameters : [
                                {label: "target_id", additionalData: [], fieldType: FieldType.SingleInput},
                                {label: "target_author", additionalData: [], fieldType: FieldType.SingleInput},
                                {label: "target_classname", additionalData: [], fieldType: FieldType.SingleInput},
                                {label: "from", additionalData: [], fieldType: FieldType.SingleDate},
                                {label : "to", additionalData: [], fieldType : FieldType.SingleDate}
                            ]}} 
                            pager={{defaultPageSize : 10}}
                            source={{
                                sourceData: sourceData,
                                sourceCount: sourceCount
                            }}   
                        />

                    </div>
                }
            />
        </div>
    );
}


export default MetricsJobResultList;