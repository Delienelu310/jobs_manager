import { AxiosResponse } from "axios";
import  apiClient from "../api/ApiClient";
import React from "react";


export interface ClientData{
    username: string,
    password: string
}

export function login(
    {username, password} : ClientData, 
    {setToken} : {setToken : React.Dispatch<React.SetStateAction<string | null>>}
) : Promise<AxiosResponse<string>>{

    const base64Token : string = btoa(`${username}:${password}`);

    return apiClient.post(`/authenticate`, null,  {
        headers: {
          'Authorization': `Basic ${base64Token}`,
        }
    }).then(response   => {

        setToken(`Bearer ${response.data}`);

        return response;
    });
}