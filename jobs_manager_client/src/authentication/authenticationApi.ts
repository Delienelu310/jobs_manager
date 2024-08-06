import { AxiosResponse } from "axios";
import  apiClient from "../api/ApiClient";
import React from "react";
import { jwtDecode } from "jwt-decode";
import { Authentication } from "./AuthContext";

interface JwtPayload{
    scope : string,
    sub : string
}

export interface ClientData{
    username: string,
    password: string
}

export function login(
    {username, password} : ClientData, 
    {setAuthentication, setRequestInjector} : {
        setAuthentication : React.Dispatch<React.SetStateAction<Authentication | null>>,
        setRequestInjector : React.Dispatch<React.SetStateAction<number | null>>
    }
) : Promise<AxiosResponse<string>>{

    const base64Token : string = btoa(`${username}:${password}`);

    return apiClient.post(`/authenticate`, null,  {
        headers: {
          'Authorization': `Basic ${base64Token}`,
        }
    }).then(response   => {
        
    
  
        const payload : JwtPayload = jwtDecode<JwtPayload>(response.data);
        setAuthentication({username : payload.sub, roles: payload.scope.split(" ")});

        const token = `Bearer ${response.data}`;
        setRequestInjector(apiClient.interceptors.request.use((config) => {
            config.headers.Authorization=token
            return config;
        }));

        return response;
    });
}