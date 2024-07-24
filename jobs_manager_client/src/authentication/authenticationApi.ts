import { AxiosResponse } from "axios";
import  apiClient from "../api/ApiClient";
import React from "react";


export interface ClientData{
    username: string,
    password: string
}

export function login(
    {username, password} : ClientData, 
    {setToken, setRequestInjector} : {
        setToken : React.Dispatch<React.SetStateAction<string | null>>,
        setRequestInjector : React.Dispatch<React.SetStateAction<number | null>>
    }
) : Promise<AxiosResponse<string>>{

    const base64Token : string = btoa(`${username}:${password}`);

    return apiClient.post(`/authenticate`, null,  {
        headers: {
          'Authorization': `Basic ${base64Token}`,
        }
    }).then(response   => {
        const token = `Bearer ${response.data}`;
        setToken(token);

        setRequestInjector(apiClient.interceptors.request.use((config) => {
            config.headers.Authorization=token
            return config;
        }));

        return response;
    });
}