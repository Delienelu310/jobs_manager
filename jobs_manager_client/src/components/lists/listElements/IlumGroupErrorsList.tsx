import { useEffect, useState } from "react";
import { IlumGroupDetails, IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import List, { SourceArg, SourceCountArg } from "../List";
import JobResultErrorElement, { JobResultErrorElementContext } from "./JobResultErrorElement";
import { JobResultSimple, retrieveJobResults, retrieveJobResultsCount } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { FieldType } from "../Filter";


export interface IlumGroupErrorsContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface IlumGroupErrorsArgs{
    data : IlumGroupOfJobResultData,
    context : IlumGroupErrorsContext
}


const IlumGroupErrorsList = ({data, context} : IlumGroupErrorsArgs) => {
    
    
    const [isListOpened, setIsListOpened] = useState<boolean>(false);

    function getJobErrors(arg : SourceArg) : Promise<JobResultSimple[]>{

        console.log("It is here");
        return retrieveJobResults(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId,
            [
                ["ilum_group_id", data.ilumGroupId],
                ["include_successfull", "false"],
                ...(["job_errors", "tester_errors"].filter(type => !(arg.filter.values.get("error_types") ?? []).includes(type)).map(type => ["include_" + type, "false"] as [string, string])),
                ...(arg.filter.values.get("error_types") ?? []).map(type => ["include_" + type, "true"] as [string, string]),
                ...(Array.from(arg.filter.values.entries())
                    .filter( ([key, value]) => key == "error_types")
                    .map(([key, value]) => [key, value.join(",")] as [string, string]) 
                )
            ]

        ).then(r => r.data);
    }

    function getJobErrorsCount(arg : SourceCountArg) : Promise<number>{
        return retrieveJobResultsCount(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId,
            [
                ["ilum_group_id", data.ilumGroupId],
                ["include_successfull", "false"],
                ...(["job_errors", "tester_errors"].map(type => ["include_" + type, "false"] as [string, string])),
                ...(arg.filter.values.get("error_types") ?? []).map(type => ["include_" + type, "true"] as [string, string]),
                ...(Array.from(arg.filter.values.entries())
                    .filter( ([key, value]) => key == "error_types")
                    .map(([key, value]) => [key, value.join(",")] as [string, string]) 
                )
            ]

        ).then(r => r.data);
    }

    
    return (
        <div style={{paddingBottom: "10px", borderBottom: "1px solid black"}}>

            <h3>{data.ilumGroupDetails.name}</h3>
            <strong>{data.ilumGroupId}</strong>
            <br/>
            <strong>When created: </strong> <i>{data.ilumGroupDetails.startTime ?? "undefined"}</i>
            <button className="btn btn-success m-2" onClick={e => setIsListOpened(!isListOpened)}>
                {isListOpened ? "Close" : "Open"}
            </button>
            <br/>

            {isListOpened &&
                <List<JobResultSimple, JobResultErrorElementContext>
                    context={{jobNodePageRefresh : context.jobNodePageRefresh}}
                    Wrapper={JobResultErrorElement}
                    dependencies={[]}
                    pager={{defaultPageSize: 10}}
                    filter={{parameters : [
                        {additionalData : ["job_errors", "tester_errors"], fieldType : FieldType.MultipleSelection, label : "error_types"},
                        {label : "tester_name", fieldType : FieldType.SingleInput, additionalData : []},
                        {label : "tester_author", fieldType : FieldType.SingleInput, additionalData : []},
                        {label : "tester_class", fieldType : FieldType.SingleInput, additionalData : []},

                        {label : "target_author", fieldType : FieldType.SingleInput, additionalData : []},
                        {label : "target_class", fieldType : FieldType.SingleInput, additionalData : []},
                    ]}}
                    source={{
                        sourceData: getJobErrors,
                        sourceCount : getJobErrorsCount
                    }}
                
                />
            }

        </div>
    );
}

export default IlumGroupErrorsList;