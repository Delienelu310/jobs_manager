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
import { NotificationType, useNotificator } from "../components/notifications/Notificator";
import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from 'yup'


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

    const {catchRequestError, pushNotification} = useNotificator();
    

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


    function getJobNodeData(){
        if(!(projectId && jobNodeId)) return;
        retrieveJobNodeWithIlumGroup(projectId, jobNodeId)
            .then(response => {
                setJobNodeData(response.data);
            })
            .catch(catchRequestError)
        ;

        retrieveQueueSize(projectId, jobNodeId, QueueTypes.JOBS_QUEUE)
            .then(r => {
                setCount([r.data, testCount]);
                retrieveQueueSize(projectId, jobNodeId, QueueTypes.TESTING_JOBS)
                    .then(r2 => {
                        setCount([r.data, r2.data])
                    }).catch(catchRequestError);
            }).catch(catchRequestError);
    }

    function start(ilumGroupConfig : IlumGroupConfiguration, ilumGroupDetails : IlumGroupDetails){
        if(!(projectId && jobNodeId)) return;
        startJobNode(projectId, jobNodeId, {ilumGroupConfiguration:  ilumGroupConfig, ilumGroupDetails : ilumGroupDetails})
            .then(response => {
                getJobNodeData();
                pushNotification({
                    message: "The Ilum Group is created and lifecycle is started",
                    type: NotificationType.INFO,
                    time : 5
                })
            }).catch(catchRequestError);

    }

    function stop(){
        if(!(projectId && jobNodeId)) return;
        stopJobNode(projectId, jobNodeId)
            .then(response => {
                getJobNodeData();
                pushNotification({
                    message: "The Ilum Group is Stopped and deleted",
                    time: 5,
                    type: NotificationType.INFO
                });
            }).catch(catchRequestError);

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
                            <button className="btn btn-primary m-2" onClick={getJobNodeData}>Refersh</button>
                            {jobNodeData.ilumGroup ? 
                                <div>
                                    <h5>Job is running</h5>
                                    <strong>Mod {jobNodeData.ilumGroup.mod}</strong>
                                    <br/>
                                    <strong>Jobs Progress:</strong><i>{jobNodeData.ilumGroup.currentIndex}/{jobsCount}</i><br/>
                                    <strong>Tests Progress:</strong><i>{jobNodeData.ilumGroup.currentTestingIndex}/{testCount}</i><br/>
                                    
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
                                        <Formik
                                            initialValues={{
                                                name : "",
                                                description : null as ( null | string),
                                                maxJobDuration : 60
                                            }}

                                            onSubmit={values => {
                                                start(
                                                    {maxJobDuration : values.maxJobDuration}, 
                                                    {name : values.name, description : values.description, startTime: null}
                                                )
                                            }}
                                            validationSchema={Yup.object({
                                                name : Yup.string()
                                                    .min(3)
                                                    .max(50)
                                                    .required()
                                                    .nonNullable()
                                                ,
                                                description : Yup.string()
                                                    .notRequired()
                                                    .nullable()
                                                    .min(3)
                                                    .max(500)
                                                ,
                                                maxJobDuration : Yup.number()
                                                    .min(1)
                                                    .required()
                                            })}
                                        >
                                            {() => (
                                                <Form>
                                                    
                                                    <h5>Details:</h5>

                                                    <div>
                                                        <strong><label htmlFor="name">Name:</label> </strong>
                                                        <Field className="form-control m-2" name="name" id="name"/>
                                                        <ErrorMessage name="name" component="div" className="text-danger"/>
                                                    </div>

                                                    <div>
                                                        <strong><label htmlFor="description">Description:</label> </strong>
                                                        <Field className="form-control m-2" name="description" id="description" as="textarea"/>
                                                        <ErrorMessage name="description" component="div" className="text-danger"/>
                                                    </div>

                                                    <hr/>

                                                    <h5>Configuration: </h5>
                                                    <div>
                                                        <strong><label htmlFor="maxJobDuration">Max Job Duration Time:</label> </strong>
                                                        <Field type="number" className="form-control m-2" name="maxJobDuration" id="maxJobDuration"/>
                                                        <ErrorMessage name="maxJobDuration" component="div" className="text-danger"/>
                                                    </div>
                                                    
                                                    <button className="btn btn-primary m-2" type="submit">Start</button>


                                                </Form>
                                            )}
                                        </Formik>

                          
                                        
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