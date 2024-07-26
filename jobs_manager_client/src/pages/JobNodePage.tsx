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



interface JobNodePageDependenciesSetters{
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>,
    setJobSciptsListDependency : React.Dispatch<React.SetStateAction<number>>,
    queueSetters : Map<QueueTypes, React.Dispatch<React.SetStateAction<number>>>
}

interface JobNodePageDependencies{
    jobsFilesListDependency : number,
    jobScriptsListDependency : number, 
    queueDependencies : Map<QueueTypes, number>
}

export interface JobNodePageRefresh{
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

    const [showJobsFiles, setShowJobsFiles] = useState<boolean>(false);
    const [showJobScripts, setShowJobScripts] = useState<boolean>(false);
    const [showJobsQueue, setShowJobsQueue] = useState<boolean>(false);
    const [showTestJobsQueue, setTestJobsQueue] = useState<boolean>(false);

    const [jobNodePageRefresh, setJobNodePageRefresh] = useState<JobNodePageRefresh>({
        setMenu : setMenu,
        dependenciesSetters : {
            queueSetters : new Map<QueueTypes, React.Dispatch<React.SetStateAction<number>>>([
                [QueueTypes.JOBS_QUEUE, setJobQueueDependency],
                [QueueTypes.TESTING_JOBS, setTestJobsDependnency]
            ]),
            setJobSciptsListDependency : setJobSciptsListDependency,
            setJobsFileListDependency : setJobsFileListDependency
        },
        dependencies : {
            queueDependencies : new Map<QueueTypes, number>([
                [QueueTypes.JOBS_QUEUE, jobQueueDependency],
                [QueueTypes.TESTING_JOBS, testJobsDependency]
            ]),
            jobsFilesListDependency : jobsFilesListDependency,
            jobScriptsListDependency : jobScriptsListDependency
        }
    });

    useEffect(() => {
  
    }, []);



    return (
        <div>

            <div>
                <h3>Current menu</h3>
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
                        resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files`,
                        count : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/count`
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
                    projectId={projectId}
                    jobNodeId={jobNodeId}
                    context={{
                        jobNodePageRefresh : jobNodePageRefresh
                    }}
                />}
                <hr/>

                <h3>List of Job Scripts</h3>

                <ServerBoundList<JobScriptSimple, JobScriptListContext>
                    pager={{defaultPageSize: 10}}
                    endpoint={{
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts`,
                        count: `/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/count`
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
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}`,
                    count: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}/count`
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
                        resourse: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.TESTING_JOBS}`,
                        count: `/projects/${projectId}/job_nodes/${jobNodeId}/queue/${QueueTypes.TESTING_JOBS}/count`
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


            
        </div>
    );


}


export default JobNodePage;