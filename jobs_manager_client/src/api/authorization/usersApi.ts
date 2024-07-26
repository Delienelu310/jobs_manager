import { AxiosResponse } from "axios"
import apiClient from "../ApiClient"


export interface AppUserSimple{
    username : string,
    appUserDetails : {
        fullname : string
    },
    authorities : string[]
}

export function retrieveUser(username : string) : Promise<AxiosResponse<AppUserSimple>>{
    return apiClient.get(`/users/${username}`);
}


