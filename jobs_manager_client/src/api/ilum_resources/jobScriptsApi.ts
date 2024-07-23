import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { AppUserSimple } from "../authorization/users";
import { JobsFileExtension, JobsFileSimple } from "./jobsFilesApi";


export interface JobScriptSimple{
    id : string,
    jar : JobsFileExtension,
    classFullName : string,
    jobsFiles : JobsFileSimple[]
    project : {
        id : string,
        projectDetails : ProjectDetails
    }
    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails
    }
    author : AppUserSimple
}