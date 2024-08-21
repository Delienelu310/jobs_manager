import { AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { AppUserSimple } from "../authorization/usersApi";
import { JobScriptSimple } from "./jobScriptsApi";

export interface JobEntityDetails{
    name : string,
    description : string | null
}

export interface JobEntitySimple{
    id : string,
    ilumId : string,

    configuration : string,

    jobEntityDetails : JobEntityDetails,

    jobScript : JobScriptSimple,

    project : {
        id : string, 
        projectDetails : ProjectDetails
    },
    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails

    },
    author : AppUserSimple
}



export function retrieveJobEntityById(projectId : string, jobNodeId : string, jobEntityId : string

) : Promise<AxiosResponse<JobEntitySimple>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/job_entities/${jobEntityId}`);
}

