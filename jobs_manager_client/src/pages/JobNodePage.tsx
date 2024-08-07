import { useEffect, useState } from "react";
import { Navigate, useParams } from "react-router-dom";
import { JobNodeWithIlumGroup, retrieveJobNodeWithIlumGroup, retrieveQueueSize } from "../api/abstraction/jobNodeApi";
import { IlumGroupConfiguration, IlumGroupDetails, startJobNode, stopJobNode } from "../api/ilum_resources/ilumGroupApi";
import JobNodeResourceListPanel, { JobNodeResourceListData } from "../components/jobNodePageComponents/JobNodeResourcesListPanel";
import JobsFilesList from "../components/jobNodePageComponents/jobsFileList/JobsFilesList";
import JobScriptsList from "../components/jobNodePageComponents/jobScriptList/JobScriptsList";
import JobsQueue from "../components/jobNodePageComponents/queueList/JobsQueue";
import TestingJobsQueue from "../components/jobNodePageComponents/queueList/TestingJobsQueue";
import PrivilegesList from "../components/jobNodePageComponents//privilegesList/PrivilegesList";
import JobResultsFailedList from "../components/jobNodePageComponents/jobResultsFailedList/JobResultsFailedList";
import JobResultsSuccessList from "../components/jobNodePageComponents/jobResultsSuccessList/JobResultsSuccessList";
import JobNodeMenu from "../components/jobNodePageComponents/menu/JobNodeMenu";
import { QueueTypes } from "../api/ilum_resources/queueOperationsApi";
import JobEntityMenu from "../components/jobNodePageComponents/menu/JobEntityMenu";
import SecuredNode from "../authentication/SecuredNode";
import { JobNodeFullData } from "../api/abstraction/projectApi";
import { JobNodePrivilege } from "../api/authorization/privilegesApi";


export enum JobNodeResourceListsMembers{
    JOBS_FILES = "Jobs Files", 
    JOBS_SCRIPTS = "Job Scripts", 
    JOBS_QUEUE = "Jobs Queue", 
    TESTING_QUEUE = "Testers Queue", 
    PRIVILLEGES = "User Privileges", 
    JOB_RESULTS_ERRORS = "Job Errors", 
    JOB_RESULTS_SUCCESS = "Job Results"
}

export interface JobNodePageRefresh{
    projectId : string,
    jobNodeId : string

    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,

    chosenResourceList : JobNodeResourceListData | null,
    setChosenResourceList : React.Dispatch<React.SetStateAction<JobNodeResourceListData | null>>

    jobNodeData : JobNodeFullData
    
}

export interface JobNodePageInterface{
    
}
const JobNodePage = ({} : JobNodePageInterface) => {
    
    const {jobNodeId, projectId} = useParams();
    

    //context

    //dependencies for all lists
    const [jobsFilesListDependency, setJobsFileListDependency] = useState<number>(0);
    const [jobScriptsListDependency, setJobSciptsListDependency] = useState<number>(0);
    const [jobQueueDependency, setJobQueueDependency] = useState<number>(0);
    const [testJobsDependency, setTestJobsDependnency] = useState<number>(0);
    const [jobNodePrivilegesDependency, setJobNodePrivilegesDependency] = useState<number>(0);
    const [jobResultsFailedDependency, setJobResultsFailedDependency] = useState<number>(0);
    const [jobResultsSuccessDependency, setJobResultsSuccessDependency] = useState<number>(0);


    const [menu, setMenu] = useState<JSX.Element | null>(null);
    const [currentList, setCurrentList] = useState<JobNodeResourceListData | null>(null);

   
    //data form server
    const [jobNodeData, setJobNodeData] = useState<JobNodeWithIlumGroup | null>(null);
    const [[jobsCount, testCount], setCount] = useState<[number, number]>([0, 0]);


    //user input
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

        retrieveQueueSize(projectId, jobNodeId, QueueTypes.JOBS_QUEUE)
            .then(r => {
                setCount([r.data, testCount]);
                retrieveQueueSize(projectId, jobNodeId, QueueTypes.TESTING_JOBS)
                    .then(r2 => {
                        setCount([r.data, r2.data])
                    });
            }).catch(e => console.log(e));
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
            {jobNodeData ? 
                <SecuredNode
                    projectPrivilegeConfig={null}
                    jobNodePrivilegeConfig={{
                        jobNode : jobNodeData,
                        privileges : null
                    }}
                    moderator={true}
                    roles={null}
                    alternative={<Navigate to="/"/>}                    
                >
                    <JobNodeMenu
                        currentMenu={menu}
                        setCurrentMenu={setMenu}
                    />

                    <main style={{
                        width: menu == null ? "100%" : "65%"
                    }}>
                        {/* Main panel */}

                        {jobNodeData ? <div style={{margin: "30px 10%"}}>
                            <h3>Job Node</h3>
                            <hr/>

                            <h5>Details:</h5>
                            <strong>Name: </strong> {jobNodeData.jobNodeDetails.name}<br/>
                            <strong>Id : {jobNodeData.id}</strong>
                            <hr/>

                            <h5>State:</h5>
                            {jobNodeData.ilumGroup ? 
                                <div>
                                    <h5>Job is running</h5>
                                    <strong>Mod {jobNodeData.ilumGroup.mod}</strong>
                                    <br/>
                                    <strong>Jobs Progress:</strong><i>{jobNodeData.ilumGroup.currentIndex + 1}/{jobsCount}</i><br/>
                                    <strong>Tests Progress:</strong><i>{jobNodeData.ilumGroup.currentTestingIndex + 1}/{testCount}</i><br/>
                                    
                                    <hr/>
                                    <h5>Current Job Entity</h5>
                                    <strong>Name: </strong>{jobNodeData.ilumGroup.currentJob.jobEntityDetails.name} <br/>
                                
                                    <strong>Class Name: </strong><i>{jobNodeData.ilumGroup.currentJob.jobScript.classFullName}</i>
                                    <br/>
                                    <button className="btn btn-success m-2" onClick={() => jobNodeData.ilumGroup && setMenu(<JobEntityMenu
                                        context={{
                                            jobNodePageRefresh: {
                                                projectId : projectId ?? "",
                                                jobNodeId : jobNodeId ?? "",
                                                setMenu : setMenu,
                                                setChosenResourceList : setCurrentList,
                                                chosenResourceList : currentList,
                                                jobNodeData
                                            },
                                            queueType: jobNodeData.ilumGroup.mod == "NORMAL" ? QueueTypes.JOBS_QUEUE : QueueTypes.TESTING_JOBS
                                        }}
                                        jobEntityId={jobNodeData.ilumGroup.currentJob.id}
                                    />)}>More...</button>

                                    <SecuredNode
                                        projectPrivilegeConfig={null}
                                        roles={null}
                                        alternative={null}
                                        moderator
                                        jobNodePrivilegeConfig={{
                                            jobNode: jobNodeData,
                                            privileges: [JobNodePrivilege.MANAGER]
                                        }}
                                    >
                                        <hr/>
                                        <h5>Actions:</h5>
                                        <button className="btn btn-danger m-2" onClick={stop}>Stop</button>
                                    </SecuredNode>
                                  


                                    <hr/>

                                    <h5>Ilum Group Details:</h5>
                                    <strong>Name: </strong> {jobNodeData.ilumGroup.ilumGroupDetails.name} <br/>
                                    <strong>Ilum Group Id :{jobNodeData.ilumGroup.ilumId}  </strong> <br/>
                                    <strong>Start Time ms: </strong>{jobNodeData.ilumGroup.ilumGroupDetails.startTime || "not specified"} <br/>
                                    <strong>Start Time</strong>{jobNodeData.ilumGroup.ilumGroupDetails.startTime ? 
                                        new Date(jobNodeData.ilumGroup.ilumGroupDetails.startTime).toUTCString() : 
                                        "not specified"
                                    } <br/>
                                    <strong>Description: </strong>
                                    <p>{jobNodeData.ilumGroup.ilumGroupDetails.description || "no description"}</p>
                                    {jobNodeData.ilumGroup.ilumGroupConfiguration && <>
                                        <hr/>
                                        <h5>Configuration: </h5>

                                        <strong>Max Job Duration</strong>{jobNodeData.ilumGroup.ilumGroupConfiguration.maxJobDuration} <br/>
                                    </>}
                                
                                </div>
                                :
                                <div>
                                    <h5>Job is not runnning</h5>

                                    <SecuredNode
                                        projectPrivilegeConfig={null}
                                        roles={null}
                                        alternative={null}
                                        moderator
                                        jobNodePrivilegeConfig={{
                                            jobNode: jobNodeData,
                                            privileges: [JobNodePrivilege.MANAGER]
                                        }}
                                    >

                                         <hr/>

                                        <h5>Details:</h5>
                                        <strong>Name: </strong>
                                        <input className="form-control m-2" value={ilumGroupDetails.name} 
                                            onChange={e => setIlumGroupDetails({...ilumGroupDetails, name : e.target.value})}/>
                                        
                                        <strong>Description: </strong>
                                        <textarea className="form-control m-2" value={ilumGroupDetails.description} 
                                            onChange={e => setIlumGroupDetails({...ilumGroupDetails, description : e.target.value})}/>
                                    
                                        <hr/>

                                        <h5>Configuration: </h5>
                                    
                                        <strong>Max Job Duration: </strong>
                                        <input className="form-control m-2" type="number" value={ilumGroupConfig.maxJobDuration} onChange={e => 
                                            setIlumGroupConfig({...ilumGroupConfig, maxJobDuration : Number(e.target.value)})
                                        }/>

                                        <button className="btn btn-primary m-2" onClick={start}>Start</button>
                                    </SecuredNode>
                                   
                                </div>
                                }

                            </div>
                            :
                            <div>Loading...</div>            
                        }
                            
                        <JobNodeResourceListPanel
                            choices={[
                                {label: JobNodeResourceListsMembers.JOBS_FILES, Component: JobsFilesList, 
                                    dependency : jobsFilesListDependency, setDependency : setJobsFileListDependency},
                                {label : JobNodeResourceListsMembers.JOBS_SCRIPTS, Component : JobScriptsList,
                                    dependency : jobScriptsListDependency, setDependency : setJobSciptsListDependency},
                                {label : JobNodeResourceListsMembers.JOBS_QUEUE, Component : JobsQueue,
                                    dependency : jobQueueDependency, setDependency : setJobQueueDependency},
                                {label : JobNodeResourceListsMembers.TESTING_QUEUE, Component : TestingJobsQueue,
                                    dependency : testJobsDependency, setDependency: setTestJobsDependnency},
                                {label : JobNodeResourceListsMembers.PRIVILLEGES, Component : PrivilegesList,
                                    dependency : jobNodePrivilegesDependency, setDependency : setJobNodePrivilegesDependency},
                                {label : JobNodeResourceListsMembers.JOB_RESULTS_ERRORS, Component : JobResultsFailedList,
                                    dependency : jobResultsFailedDependency, setDependency : setJobResultsFailedDependency},
                                {label : JobNodeResourceListsMembers.JOB_RESULTS_SUCCESS, Component: JobResultsSuccessList, 
                                    dependency : jobResultsSuccessDependency, setDependency : setJobResultsSuccessDependency}
                            ]}
                            context={{
                                projectId : projectId ?? "",
                                jobNodeId : jobNodeId ?? "",
                                setMenu : setMenu,
                                setChosenResourceList : setCurrentList,
                                chosenResourceList : currentList,
                                jobNodeData
                            }}
                        />
                    </main>

                </SecuredNode>
                :
                <h3>Loading...</h3>
            }
          
        </div>
    );


}


export default JobNodePage;