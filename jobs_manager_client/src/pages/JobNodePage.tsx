import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ServerBoundList from "../components/lists/ServerBoundList";
import { JobsFileSimple } from "../api/ilum_resources/jobsFilesApi";
import JobsFileElement, { JobsFileListContext } from "../components/lists/listElements/JobsFileElement";
import { FieldType } from "../components/lists/Filter";
import JobsFileUploader from "../components/JobsFileUploader";
import { JobScriptSimple } from "../api/ilum_resources/jobScriptsApi";
import JobScriptElement, { JobScriptListContext } from "../components/lists/listElements/JobScriptElement";
import JobScriptCreator from "../components/JobScriptCreator";
import JobEntityElement, { JobEntityElementContext } from "../components/lists/listElements/JobEntityElement";
import { JobEntitySimple } from "../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../api/ilum_resources/queueOperationsApi";
import { AppUserSimple } from "../api/authorization/usersApi";
import AppUserElement, { AppUserElementContext } from "../components/lists/listElements/AppUserElement";
import { JobNodePrivilege } from "../api/authorization/privilegesApi";
import AppUserAdditionComponent from "../components/AppUserAdditionComponent";
import { JobNodeWithIlumGroup, retrieveJobNodeWithIlumGroup } from "../api/abstraction/jobNodeApi";
import { IlumGroupConfiguration, IlumGroupDetails, IlumGroupOfJobResultData, startJobNode, stopJobNode } from "../api/ilum_resources/ilumGroupApi";
import IlumGroupErrorsList, {  IlumGroupErrorsContext } from "../components/lists/listElements/IlumGroupErrorsList";
import IlumGroupTestersList, { IlumGroupTestersListContext } from "../components/lists/listElements/IlumGroupTestersList";


interface JobNodePageDependenciesSetters{
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>,
    setJobSciptsListDependency : React.Dispatch<React.SetStateAction<number>>,
    queueSetters : Map<string, React.Dispatch<React.SetStateAction<number>>>
}

interface JobNodePageDependencies{
    jobsFilesListDependency : number,
    jobScriptsListDependency : number, 
    queueDependencies : Map<string, number>
}

export interface JobNodePageRefresh{
    projectId : string,
    jobNodeId : string

    dependenciesSetters : JobNodePageDependenciesSetters,
    dependencies : JobNodePageDependencies
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,
    
}

export interface JobNodePageInterface{
    
}

const JobNodePage = ({} : JobNodePageInterface) => {


    const {jobNodeId, projectId} = useParams();

    const [menu, setMenu] = useState<JSX.Element | null>(null);
    const [jobsFilesListDependency, setJobsFileListDependency] = useState<number>(0);
    const [jobScriptsListDependency, setJobSciptsListDependency] = useState<number>(0);
    const [jobQueueDependency, setJobQueueDependency] = useState<number>(0);
    const [testJobsDependency, setTestJobsDependnency] = useState<number>(0);
    const [jobNodePrivilegesDependency, setJobNodePrivilegesDependency] = useState<number>(0);

    const [showJobsFiles, setShowJobsFiles] = useState<boolean>(false);
    const [showJobScripts, setShowJobScripts] = useState<boolean>(false);
    const [showJobsQueue, setShowJobsQueue] = useState<boolean>(false);
    const [showTestJobsQueue, setTestJobsQueue] = useState<boolean>(false);
    const [showJobNodePrivileges, setShowJobNodePrivileges] = useState<boolean>(false);
    
    const [showJobResultsFailed, setShowJobResultsFailed] = useState<boolean>(false);
    const [showSuccessfulJobResults, setShowSuccessfulJobResults] = useState<boolean>(false);


    const [jobNodePageRefresh, setJobNodePageRefresh] = useState<JobNodePageRefresh>({
        projectId : projectId ?? "",
        jobNodeId : jobNodeId ?? "",
        setMenu : setMenu,
        dependenciesSetters : {
            queueSetters : new Map<string, React.Dispatch<React.SetStateAction<number>>>([
                [QueueTypes.JOBS_QUEUE, setJobQueueDependency],
                [QueueTypes.TESTING_JOBS, setTestJobsDependnency]
            ]),
            setJobSciptsListDependency : setJobSciptsListDependency,
            setJobsFileListDependency : setJobsFileListDependency
        },
        dependencies : {
            queueDependencies : new Map<string, number>([
                [QueueTypes.JOBS_QUEUE, jobQueueDependency],
                [QueueTypes.TESTING_JOBS, testJobsDependency]
            ]),
            jobsFilesListDependency : jobsFilesListDependency,
            jobScriptsListDependency : jobScriptsListDependency
        }
    });

    const [jobNodeData, setJobNodeData] = useState<JobNodeWithIlumGroup | null>(null);
    const [ilumGroupConfig, setIlumGroupConfig] = useState<IlumGroupConfiguration>({
        maxJobDuration : 60
    });
    const [ilumGroupDetails, setIlumGroupDetails] = useState<IlumGroupDetails>({
        name : "",
        description : "",
        startTime: null
    });

    function getJobNodeData(){
        if(!(projectId && jobNodeId)) return;
        retrieveJobNodeWithIlumGroup(projectId, jobNodeId)
            .then(response => {
                setJobNodeData(response.data);
            })
            .catch(e => console.log(e))
        ;
    }

    function start(){
        if(!(projectId && jobNodeId)) return;
        startJobNode(projectId, jobNodeId, {ilumGroupConfiguration:  ilumGroupConfig, ilumGroupDetails : ilumGroupDetails})
            .then(response => {
                getJobNodeData();
            }).catch(e => console.log(e));

    }

    function stop(){
        if(!(projectId && jobNodeId)) return;
        stopJobNode(projectId, jobNodeId)
            .then(response => {
                getJobNodeData();
            }).catch(e => console.log(e));

    }

    useEffect(() => {
        getJobNodeData();
    }, []);



    return (
        <div>


            {/* Main panel */}

            {jobNodeData ? <div>
                    <h3>Job Node : {jobNodeData?.jobNodeDetails.name}</h3>

                    <h4>State:</h4>
                    {jobNodeData.ilumGroup ? 
                        <div>
                            <h5>Job is running</h5>
                            <strong>Mod {jobNodeData.ilumGroup.mod}</strong>
                            <br/>
                            <strong>Current Job Entity</strong>
                            <br/>
                            <i>{jobNodeData.ilumGroup.currentJob.jobScript.classFullName}</i>

                            <button className="btn btn-danger m-2" onClick={stop}>Stop</button>

                            <h3>Current errors:</h3>
                            <IlumGroupErrorsList context={{jobNodePageRefresh: jobNodePageRefresh}} data={{
                                ilumGroupDetails: jobNodeData.ilumGroup.ilumGroupDetails,
                                ilumGroupId: jobNodeData.ilumGroup.ilumId
                            }}/>
                            
                        </div>
                        :
                        <div>
                            <h5>Job is not runnning</h5>
                            <strong>Details:</strong>
                            <br/>
                            <label>Name: <input value={ilumGroupDetails.name} 
                                onChange={e => setIlumGroupDetails({...ilumGroupDetails, name : e.target.value})}/>
                            </label>
                            <br/>
                            <label>Description: <input value={ilumGroupDetails.description} 
                                onChange={e => setIlumGroupDetails({...ilumGroupDetails, description : e.target.value})}/>
                            </label>
                            <br/>

                            <strong>Configuration: </strong>
                            <br/>
                            <label>
                                Max Job Duration: 
                                <input type="number" value={ilumGroupConfig.maxJobDuration} onChange={e => 
                                    setIlumGroupConfig({...ilumGroupConfig, maxJobDuration : Number(e.target.value)})
                                }/>
                            </label>
                            <br/>
                            

                            <button className="btn btn-primary m-2" onClick={start}>Start</button>
                        </div>
                    }

                </div>
                :
                <div>Loading...</div>            
            }

            <div>
                <h3>Current menu</h3>
                {menu && <button className="btn btn-danger" onClick={e => setMenu(null)}>Close</button>}
                {menu || <div>No menu</div>}
            </div>

            <hr/>

        
            <button className="m-3 btn btn-primary" onClick={e => setShowJobsFiles(!showJobsFiles)}>
                {showJobsFiles ? "Close Jobs Files" : "Show Jobs Files"}
            </button>
            {showJobsFiles && <>
                <h3>Jobs Files:</h3>
                    
                {projectId && jobNodeId && <JobsFileUploader
                    projectId={projectId}
                    jobNodeId={jobNodeId}
                />}
                
                <hr/>

                
                <ServerBoundList<JobsFileSimple, JobsFileListContext>
                    pager={{defaultPageSize: 10}}
                    filter={{parameters: [
                        {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                    ]}} 
                    Wrapper={JobsFileElement}
                    endpoint={{
                        resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files?`,
                        count : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/count?`
                    }}
                    context={{jobNodePageRefresh: jobNodePageRefresh}}
                    dependencies={[jobsFilesListDependency]}
                />

                <hr/>
            </>}

            <br/>

            <button className="m-3 btn btn-primary" onClick={e => setShowJobScripts(!showJobScripts)}>
                {showJobScripts ? "Close Job Scripts" : "Show Job Scripts"}
            </button>
            {showJobScripts && <>
                {projectId && jobNodeId && <JobScriptCreator
                    context={{
                        jobNodePageRefresh : jobNodePageRefresh
                    }}
                />}
                <hr/>

                <h3>List of Job Scripts</h3>

                <ServerBoundList<JobScriptSimple, JobScriptListContext>
                    pager={{defaultPageSize: 10}}
                    endpoint={{
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts?`,
                        count: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/count?`
                    }}
                    dependencies={[jobScriptsListDependency]}
                    context={{
                        jobNodePageRefresh : jobNodePageRefresh
                    }}
                    Wrapper={JobScriptElement}
                    filter={{ parameters: [
                        {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                        {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection},
                        
                    ]}}
                />
            </>}


            <br/>

            <button className="m-3 btn btn-primary" onClick={e => setShowJobsQueue(!showJobsQueue)}>
                {showJobsQueue ? "Close Jobs Queue" : "Show Jobs Queue"}
            </button>

            {showJobsQueue && <>
                <hr/>
                <h3>Jobs Queue:</h3>
                
                <ServerBoundList<JobEntitySimple, JobEntityElementContext>
                    endpoint={{
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}?`,
                    count: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}/count?`
                    }}
                    filter={{parameters: [
                        {label: "author", additionalData: [], fieldType: FieldType.SingleInput}
                    ]}}
                    Wrapper={JobEntityElement}
                    context={{
                        jobNodePageRefresh: jobNodePageRefresh,
                        queueType : QueueTypes.JOBS_QUEUE
                    }}
                    dependencies={[jobQueueDependency]}
                    pager={{defaultPageSize: 10}}
                
                />
                <hr/>
            </>}

            <br/>

            <button className="m-3 btn btn-primary" onClick={e => setTestJobsQueue(!showTestJobsQueue)}>
                {showTestJobsQueue ? "Close Test Jobs Queue" : "Show Test Jobs Queue"}
            </button>

            {showTestJobsQueue && <>
                <hr/>
                <h3>Jobs Queue:</h3>
                
                <ServerBoundList<JobEntitySimple, JobEntityElementContext>
                    endpoint={{
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.TESTING_JOBS}?`,
                        count: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.TESTING_JOBS}/count?`
                    }}
                    filter={{parameters: [
                        {label: "author", additionalData: [], fieldType: FieldType.SingleInput}
                    ]}}
                    Wrapper={JobEntityElement}
                    context={{
                        jobNodePageRefresh : jobNodePageRefresh,
                        queueType : QueueTypes.TESTING_JOBS
                    }}
                    dependencies={[testJobsDependency]}
                    pager={{defaultPageSize: 10}}
                
                />
                <hr/>
            </>}

            <br/>

            <button className="m-3 btn btn-primary" onClick={e => setShowJobNodePrivileges(!showJobNodePrivileges)}>
                {showJobNodePrivileges ? "Close Privilege List" : "Open Privilege List "}
            </button>
            {showJobNodePrivileges && 
                <>
                    <hr/>
                    <h3>Job Node Privilege List</h3>

                    <hr/>
                    <AppUserAdditionComponent
                        context={{
                            jobNodePageRefresh : jobNodePageRefresh
                        }}
                    />
                    <hr/>

                    <ServerBoundList<AppUserSimple, AppUserElementContext>
                        Wrapper={AppUserElement}
                        dependencies={[jobNodePrivilegesDependency]}
                        context={{jobNodePageRefresh}}
                        filter={{parameters : [
                            {label : "jobNodePrivileges", additionalData : Object.values(JobNodePrivilege), fieldType : FieldType.MultipleSelection}
                        ]}}
                        pager={{defaultPageSize:10}}
                        endpoint={{
                            resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/privileges?`,
                            count: `/projects/${projectId}/job_nodes/${jobNodeId}/privileges/count?`
                        }}
                    />

                    <hr/>
                </>
            }
            <br/>

            <button className="m-3 btn btn-primary" onClick={e => setShowJobResultsFailed(!showJobResultsFailed)}>
                {showJobResultsFailed ? "Close" : "Show"} Job Errors
            </button>

            {showJobResultsFailed && <>
                <ServerBoundList<IlumGroupOfJobResultData, IlumGroupErrorsContext>
                    Wrapper={IlumGroupErrorsList}
                    context={{jobNodePageRefresh : jobNodePageRefresh}}
                    pager={{defaultPageSize : 10}}
                    dependencies={[]}
                    filter={{parameters : [
                        {label: "from", additionalData: [], fieldType: FieldType.SingleInput},
                        {label : "to", additionalData: [], fieldType : FieldType.SingleInput}
                    ]}}
                    endpoint={{
                        count : `/projects/${projectId}/job_nodes/${jobNodeId}/job_results/ilum_groups/count?`,
                        resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/job_results/ilum_groups?`
                    }}
                />
            </>}

            <br/>
            <button className="m-3 btn btn-primary" onClick={e => setShowSuccessfulJobResults(!showSuccessfulJobResults)}>
                {showSuccessfulJobResults ? "Close" : "Open"} Successful Job Results
            </button>

            {showSuccessfulJobResults && <>
                <ServerBoundList<IlumGroupOfJobResultData, IlumGroupTestersListContext>
                    context={{jobNodePageRefresh : jobNodePageRefresh}}
                    Wrapper={IlumGroupTestersList}
                    dependencies={[]}
                    endpoint={{
                        count : `/projects/${projectId}/job_nodes/${jobNodeId}/job_results/ilum_groups/count?`,
                        resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/job_results/ilum_groups?`
                    }}
                    filter={{parameters : [
                        {label: "from", additionalData: [], fieldType: FieldType.SingleInput},
                        {label : "to", additionalData: [], fieldType : FieldType.SingleInput}
                    ]}}
                    pager={{defaultPageSize : 10}}
                />
            </>}

            {/* {jobNodeData && jobNodeData.ilumGroup && <>
                <button className="btn btn-primary" onClick={e => setShowJobResultsFailed(!showJobResultsFailed)}>
                    {showJobResultsFailed ? "Close Current Failed Jobs" : "Open Current Failed Jobs"}
                </button>

                {showJobResultsFailed && <>
                    <List<JobResultSimple, JobResultElementContext>
                        Wrapper={JobResultElement}
                        context={{jobNodePageRefresh}}
                        dependencies={[]}
                        filter={{parameters : [
                            {additionalData : ["job_errors", "tester_errors"], fieldType : FieldType.MultipleSelection, label : "error_types"},
                            {label : "tester_name", fieldType : FieldType.SingleInput, additionalData : []},
                            {label : "tester_author", fieldType : FieldType.SingleInput, additionalData : []},
                            {label : "tester_class", fieldType : FieldType.SingleInput, additionalData : []},

                            {label : "target_author", fieldType : FieldType.SingleInput, additionalData : []},
                            {label : "target_class", fieldType : FieldType.SingleInput, additionalData : []},

                            ...(jobNodeData && jobNodeData.ilumGroup ? 
                                    [{label : "ilum_group_id", fieldType: FieldType.MultipleSelection, additionalData : [jobNodeData.ilumGroup.id]}]
                                    : 
                                    []
                                )
                        ]}}
                        source={{
                            sourceCount : getCurrentQueueErrorsCount,
                            sourceData: getCurrentQueueErrors
                        }}
                        pager={{defaultPageSize : 10}}
                    
                    />
                </>}

            </>} */}
            
        </div>
    );


}


export default JobNodePage;