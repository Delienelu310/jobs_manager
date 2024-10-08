
import {  IlumGroupOfJobResultData } from "../../../api/ilum_resources/ilumGroupApi";
import List, { SourceArg, SourceCountArg } from "../../lists/List";
import JobResultErrorElement, { JobResultErrorElementContext } from "./JobResultErrorElement";
import { clearJobResults, JobResultSimple, retrieveJobResults, retrieveJobResultsCount } from "../../../api/ilum_resources/jobResultApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { FieldType } from "../../lists/Filter";
import OpenerComponent from "../../OpenerComponent";
import { useNotificator } from "../../notifications/Notificator";

export interface IlumGroupErrorsContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface IlumGroupErrorsArgs{
    data : IlumGroupOfJobResultData,
    context : IlumGroupErrorsContext
}


const IlumGroupErrorsList = ({data, context} : IlumGroupErrorsArgs) => {


    const {catchRequestError} = useNotificator();
    
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

    function clearIlumGroup(){
        console.log(data.ilumGroupId);
        clearJobResults(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data.ilumGroupId, null, 
            false, true, true
        ).then(r => {
            console.log(r);
            context.jobNodePageRefresh.chosenResourceList?.setDependency(Math.random());
        }).catch(catchRequestError)
    }

    
    return (
        <div style={{paddingBottom: "10px", borderBottom: "1px solid black"}}>

            <OpenerComponent
                closedLabel={
                    <div>
                        <h3 className="m-2">{data.ilumGroupDetails.name || "No name specified"}</h3>
                        <strong>Ilum Group ID: </strong> {data.ilumGroupId}
                        <br/>
                        <strong>When created: </strong> <i>{data.ilumGroupDetails.startTime ? 
                            new Date(Number(data.ilumGroupDetails.startTime)).toUTCString()
                            : "undefined"
                        }</i>
                        <button className="btn btn-danger m-2" onClick={clearIlumGroup}>Clear</button>
                    </div>
                }
                openedElement={
                    <div>

                        <div>
                            <h3 className="m-2">{data.ilumGroupDetails.name || "No name specified"}</h3>
                            <strong>Ilum Group ID: </strong> {data.ilumGroupId}
                            <br/>
                            <strong>When created: </strong> <i>{data.ilumGroupDetails.startTime ? 
                                new Date(Number(data.ilumGroupDetails.startTime)).toUTCString()
                                : "undefined"
                            }</i>
                            <br/>
            
                            <strong>Description: </strong>
                            <p>
                                {data.ilumGroupDetails.description || "No description"}
                            </p>
                            <button className="btn btn-danger m-2" onClick={clearIlumGroup}>Clear</button>
                        </div>

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
                                sourceCount : getJobErrorsCount,
                                catchCount: e => console.log(e),
                                catchData : e => console.log(e)
                            }}
                        
                        />
                    </div>
                }
            />

           

        </div>
    );
}

export default IlumGroupErrorsList;