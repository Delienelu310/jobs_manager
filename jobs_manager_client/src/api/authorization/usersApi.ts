import { AxiosResponse } from "axios"
import apiClient from "../ApiClient"


export enum Roles{
    MANAGER = "MANAGER", WORKER = "WORKER"
}

export interface AppUserDetails{
    fullname : string
}

export interface AppUserSimple{
    username : string,
    appUserDetails : AppUserDetails,
    authorities : {authority : string}[]
}

export interface AppUserRequestBody{
    username : string,
    passwordEncoded : string,
    appUserDetails : AppUserDetails,
    roles : string[]

}

export function retrieveUser(username : string) : Promise<AxiosResponse<AppUserSimple>>{
    return apiClient.get(`/users/${username}`);
}


export function deleteUser(username : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/users/${username}`);
}

export function deleteModerator(username : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/moderators/${username}`);
}


export function createUser(appUserSimple : AppUserRequestBody,

) : Promise<AxiosResponse<void>>{
    return apiClient.post(`/users`, appUserSimple);
}

export function createModerator(appUserSimple : AppUserRequestBody

) : Promise<AxiosResponse<void>>{
    return apiClient.post(`/moderators`, appUserSimple);
}


export function updatePassword(username : string, newPassword : string

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/users/${username}/password`, btoa(newPassword) as string,  {headers: {
        'Content-Type': 'text/plain'
    }});
}

export function updateRoles(username : string, newRoles : string[]

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/users/${username}/roles`, newRoles);
}

export function updateDetails(username : string, newDetails : AppUserDetails

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/users/${username}/details`, newDetails);
}

export function updateModeratorPassword(username : string, newPassword : string

) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/moderators/${username}/password`, btoa(newPassword) as string,  {headers: {
        'Content-Type': 'text/plain'
    }});
}
