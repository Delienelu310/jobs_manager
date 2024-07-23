import { ProjectDetails } from "./projectApi";

export interface ProjectSimple{
    admin : string | null ,
    id: string,
    projectDetails: ProjectDetails
}
