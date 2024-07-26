import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";
import { JobEntityDetails } from "./jobEntityApi";


export enum QueueTypes{
    JOBS_QUEUE = "jobsQueue",
    TESTING_JOBS = "testingJobs"
}


export function removeJobEntityFromQueue(projectId : string, jobNodeId : string, queueType : QueueTypes, jobEntityId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/${queueType}/job_entities/${jobEntityId}`);
}


export function addJobEntityToQueue(projectId : string, jobNodeId : string, queueType : QueueTypes, jobScriptId : string,
    configuration : string, jobEntityDetails : JobEntityDetails
) : Promise<AxiosResponse<string>>{
    return apiClient.post(`/projects/${projectId}/job_nodes/${jobNodeId}/${queueType}/job_entities/${jobScriptId}${configuration == "" ? "" : 
        "?configuration=" + configuration}`, 
        jobEntityDetails
    );
}
