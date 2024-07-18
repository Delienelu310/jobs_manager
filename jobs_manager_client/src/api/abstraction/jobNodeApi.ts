import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";


export async function deleteJobNode(projectId : string, jobNodeId : string) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}`);
}  

export async  function removeJobNodePlug(projectId : string, jobNodeId : string, rightOrientation : boolean, label : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/remove/${rightOrientation ? "output" : "input"}/${label}`);
}




