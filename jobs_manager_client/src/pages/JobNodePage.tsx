import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { JobNodeWithIlumGroup, retrieveJobNodeWithIlumGroup } from "../api/abstraction/jobNodeApi";
import { IlumGroupConfiguration, IlumGroupDetails, startJobNode, stopJobNode } from "../api/ilum_resources/ilumGroupApi";
import JobNodeResourceListPanel, { JobNodeResourceListData } from "../components/jobNodePageComponents/JobNodeResourcesListPanel";
import JobsFilesList from "../components/jobNodePageComponents/jobsFileList/JobsFilesList";
import JobScriptsList from "../components/jobNodePageComponents/jobScriptList/JobScriptsList";
import JobsQueue from "../components/jobNodePageComponents/JobsQueue";
import TestingJobsQueue from "../components/jobNodePageComponents/TestingJobsQueue";
import PrivilegesList from "../components/jobNodePageComponents/PrivilegesList";
import JobResultsFailedList from "../components/jobNodePageComponents/JobResultsFailedList";
import JobResultsSuccessList from "../components/jobNodePageComponents/JobResultsSuccessList";



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
    
}

export interface JobNodePageInterface{
    
}
const JobNodePage = ({} : JobNodePageInterface) => {


    const {jobNodeId, projectId} = useParams();
    
    const [menu, setMenu] = useState<JSX.Element | null>(null);
    const [currentList, setCurrentList] = useState<JobNodeResourceListData | null>(null);

    
    const [jobsFilesListDependency, setJobsFileListDependency] = useState<number>(0);
    const [jobScriptsListDependency, setJobSciptsListDependency] = useState<number>(0);
    const [jobQueueDependency, setJobQueueDependency] = useState<number>(0);
    const [testJobsDependency, setTestJobsDependnency] = useState<number>(0);
    const [jobNodePrivilegesDependency, setJobNodePrivilegesDependency] = useState<number>(0);
    const [jobResultsFailedDependency, setJobResultsFailedDependency] = useState<number>(0);
    const [jobResultsSuccessDependency, setJobResultsSuccessDependency] = useState<number>(0);

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

                            {/* <h3>Current errors:</h3>
                            <IlumGroupErrorsList context={{jobNodePageRefresh: jobNodePageRefresh}} data={{
                                ilumGroupDetails: jobNodeData.ilumGroup.ilumGroupDetails,
                                ilumGroupId: jobNodeData.ilumGroup.ilumId
                            }}/> */}
                            
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
                }}
            />
            
        </div>
    );


}


export default JobNodePage;