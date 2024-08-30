import { AxiosResponse } from "axios";
import  apiClient from "../api/ApiClient";
import React from "react";
import { jwtDecode } from "jwt-decode";
import { Authentication } from "./AuthContext";

export interface JwtPayload{
    scope : string,
    sub : string
}

export interface ClientData{
    username: string,
    password: string
}

export function login(
    {username, password} : ClientData, 
    {setAuthentication, setRequestInjector, setCookie} : {
        setAuthentication : React.Dispatch<React.SetStateAction<Authentication | null>>,
        setRequestInjector : React.Dispatch<React.SetStateAction<number | null>>,
        setCookie : (name : string, value : string) => void
    }
) : Promise<AxiosResponse<string>>{

    const base64Token : string = btoa(`${username}:${password}`);

    return apiClient.post(`/authenticate`, null,  {
        headers: {
          'Authorization': `Basic ${base64Token}`,
        }
    }).then(response   => {
        
        console.log(response);
  
        const payload : JwtPayload = jwtDecode<JwtPayload>(response.data);
        setAuthentication({username : payload.sub, roles: payload.scope.split(" ")});

        const token = `Bearer ${response.data}`;
        setRequestInjector(apiClient.interceptors.request.use((config) => {
            config.headers.Authorization=token
            return config;
        }));

        setCookie("authentication-token", response.data);

        return response;
    });
}