import {  AxiosResponse } from "axios";
import apiClient from "../ApiClient";



export enum JobNodePrivilege{
    MANAGER = "MANAGER",
    VIEWER = "VIEWER",
    TESTER = "TESTER",
    SCRIPTER = "SCRIPTER"
}

export enum ProjectPrivilege{
    ADMIN = "ADMIN",
    MODERATOR = "MODERATOR",
    TESTER = "TESTER",
    VIEWER = "VIEWER",
    ARCHITECT = "ARCHITECT",
    SCRIPTER = "SCRIPTER"
}

export function retrieveProjectUserPrivileges(projectId : string, username : string

) : Promise<AxiosResponse<ProjectPrivilege[]>>{
    return apiClient.get(`/projects/${projectId}/privileges/users/${username}`);
}

export function retrieveJobNodeUserPrivileges(projectId : string, jobNodeId : string, username : string

) : Promise<AxiosResponse<JobNodePrivilege[]>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/privileges/users/${username}`);
}


export function addPrivilegeToJobNodeUser(projectId : string, jobNodeId : string, username : string, privilege : string

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/privileges/users/${username}/${privilege}`);
}

export function removePrivilegeFromJobNodeUser(projectId : string, jobNodeId : string, username : string, privilege : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/privileges/users/${username}/${privilege}`);
}

export function removeUserFromJobNode(projectId: string, jobNodeId : string, username : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/privileges/users/${username}`);
}




export function addPrivilegeToProjectUser(projectId : string, username : string, privilege : string

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/privileges/users/${username}/${privilege}`);
}

export function removePrivilegeFromProjectUser(projectId : string, username : string, privilege : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/privileges/users/${username}/${privilege}`);
}

export function removeUserFromProject(projectId : string, useranem : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/privileges/users/${useranem}`);
}

export function removeModeratorFromProject(projectId : string, username : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/privileges/moderators/${username}`);
}

export function addModeratorToProject(projectId : string, username : string

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/privileges/moderators/${username}`);
}