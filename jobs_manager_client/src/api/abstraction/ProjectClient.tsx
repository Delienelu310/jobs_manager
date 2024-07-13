
import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";

export interface ProjectSimple{
    admin : string,
    id: string,
    projectDetails: {
        name : string,
        description: string
    }
}


export function retrieveProjects(
    query : string, 
    admin : string | null,
    pageSize : number,
    pageNumber : number
) : Promise<AxiosResponse<ProjectSimple>>{

    return apiClient.get("/projects?" +
        `query='${query}',` + 
        (admin == null ? `admin=${admin},` : "") + 
        `pageSize=${pageSize},` + 
        `pageNumber=${pageNumber}`
    );

}
