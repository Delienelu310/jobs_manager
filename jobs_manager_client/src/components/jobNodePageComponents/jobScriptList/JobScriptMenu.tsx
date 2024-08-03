import {  useEffect, useState } from "react";
import { deleteJobScript, JobScriptDetails, JobScriptSimple, retreiveJobScript, updateJobScriptDetails } from "../../../api/ilum_resources/jobScriptsApi";
import List, { SourceArg, SourceCountArg } from "../../lists/List";
import JobsFileRemoveElement, { JobsFileRemoveElementContext } from "./JobsFileRemoveElement";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import { FieldType } from "../../lists/Filter";
import ServerBoundList from "../../lists/ServerBoundList";
import JobsFileAddElement, { JobsFileAddElementContext } from "./JobsFileAddElement";
import JobEntityCreator from "../../JobEntityCreator";
import { QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";


export interface JobScriptMenuContext{
    jobNodePageRefresh : JobNodePageRefresh,
}

export interface JobScriptMenu{
    data : JobScriptSimple,
    context : JobScriptMenuContext,
}


const JobScriptMenu = ({
    data, 
    context
} : JobScriptMenu) => {
    
    const [actualData, setActualData] = useState<JobScriptSimple | null>(null);
    
    const [newDetails, setNewDetails] = useState<JobScriptDetails>({
        name : ""
    });

    const [jobSearchListOpened, setJobSearchListOpened] = useState<boolean>(false);

    function refresh(){
        retreiveJobScript(data.project.id, data.jobNode.id, data.id)
            .then(response => {
                setActualData(response.data);
            })
            .catch(e => console.log(e));
    }

    function deleteJobScriptElement(){
        deleteJobScript(data.project.id, data.jobNode.id, data.id)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }
                
            }).catch(e => console.log(e));   
    }

    function updateDetails(){
        updateJobScriptDetails(data.project.id, data.jobNode.id, data.id, newDetails)
            .then(r => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }
            })
            .catch(e => console.log(e));

    }

    function getJobsFilesUsedList({filter, search, pager} : SourceArg) : Promise<JobsFileSimple[]>{

        return new Promise<JobsFileSimple[]>((resolve, reject) => {

            if(!actualData) {
                resolve([]);
                return;
            };
            let result : JobsFileSimple[] = actualData.jobsFiles;
            result = result.filter(jobsFile => jobsFile.jobDetails.name.startsWith(search));
      
            const className : string = (filter.values.get("classname") ?? [""])[0];
            if(className != "") result = result.filter(jobsFile => jobsFile.allClasses.includes(className))

        
            const publisher : string = (filter.values.get("publisher") ?? [""])[0];
            if(publisher != "") result = result.filter(jobsFile => jobsFile.publisher.username == publisher);

            let offset : number = pager.pageSize * pager.pageChosen
    
            resolve(result
                .filter((jobsFile, index) => index >= offset && index < offset + pager.pageSize)
            );
        });
    }

    function getJobsFilesUsedCount({filter, search} : SourceCountArg): Promise<number>{

        return new Promise<number>((resolve, reject) => {
            
            if(!actualData){
                resolve(0);
                return;
            }

            let result : JobsFileSimple[] = actualData?.jobsFiles;
            result = result.filter(jobsFile => jobsFile.jobDetails.name.startsWith(search));

            const className : string = (filter.values.get("classname") ?? [""])[0];
            if(className != "") result = result.filter(jobsFile => jobsFile.allClasses.includes(className))

        
            const publisher : string = (filter.values.get("publisher") ?? [""])[0];
            if(publisher != "") result = result.filter(jobsFile => jobsFile.publisher.username == publisher);
            
            resolve(result.length);
        });
    }
    

    
    useEffect(() => {
        refresh();
    }, []);

    return (
        <>
            {actualData ? 
                <div>

                    {/* current job script data: */}
                    <h3>{actualData.jobScriptDetails.name}</h3>
                    <span>ID: {actualData.id}</span>
                    <br/>
                    <span>Author : {actualData.author.username}</span>
                    <br/>
                    <strong>Extension: {actualData.extension}</strong>
                    <br/>
                    <h4>Class full name:</h4>
                    <strong>{actualData.classFullName}</strong>
                    <br/>

                    {/* create job entity */}

                    <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobEntityCreator
                        context={{
                            jobNodePageRefresh : context.jobNodePageRefresh
                        }}
                        projectId={data.project.id}
                        jobNodeId={data.jobNode.id}
                        jobScriptId={data.id}
                    />)}>Add to Queue</button>

                    <br/>

                    {/* update job script details */}
                    <label>
                        New name:
                        <input value={newDetails.name} onChange={e => setNewDetails({...newDetails, name : e.target.value})}/>
                    </label>
                    <br/>
                    <button className="btn btn-success" onClick={updateDetails}>Update details</button>
                    <br/>

                    {/* delete job script */}

                    <button className="btn btn-danger m-2" onClick={deleteJobScriptElement}>Delete</button>
                    <br/>
                    {/* browse the list of jobs files not used by job script and add it */}
                   

                    <button className="btn btn-primary m-2" onClick={e => setJobSearchListOpened(!jobSearchListOpened)}>
                        {jobSearchListOpened ? "Close" : "Add jobs file"}
                    </button>
                    
                    {jobSearchListOpened && <ServerBoundList<JobsFileSimple, JobsFileAddElementContext>
                        endpoint={{
                            resourse: `/projects/${data.project.id}/job_nodes/${data.jobNode.id}/jobs_files`,
                            count :  `/projects/${data.project.id}/job_nodes/${data.jobNode.id}/jobs_files/count`
                        }}
                        Wrapper={JobsFileAddElement}
                        pager={{defaultPageSize: 10}}
                        context={{
                            jobNodePageRefresh : context.jobNodePageRefresh,
                            refreshJobScript: refresh,
                            jobScript : actualData
                        }}
                        dependencies={[]}
                        filter={{parameters: [
                            {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                            {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                            {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                        ]}}
                    />}



                    {/* browse the list of jobs files used */}

                    <h4>Jobs Files used:</h4>
                    <List<JobsFileSimple, JobsFileRemoveElementContext>
                        Wrapper={JobsFileRemoveElement}
                        pager={{defaultPageSize : 10}}
                        source={{
                            sourceData: getJobsFilesUsedList,
                            sourceCount: getJobsFilesUsedCount
                            
                        }}
                        context={{
                            jobNodePageRefresh : context.jobNodePageRefresh,
                            refreshJobScript: refresh,
                            jobScript : actualData
                        }}
                        dependencies={[actualData]}
                        filter={{parameters: [
                            {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                            {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                        ]}}
                    />





                </div>
                :
                <h4>Loading...</h4>
            }
        
        </>
        
    );
}

export default JobScriptMenu;