import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { JobScriptSimple } from "./jobScriptsApi";

export interface JobEntityDetails{
    name : string,
    description : string
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

    }
}