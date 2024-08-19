import React, { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { ClientData, login } from "./authenticationApi";
import { AxiosResponse, InternalAxiosRequestConfig } from "axios";
import apiClient from "../api/ApiClient";


export interface Authentication{
    username : string,
    roles : string[]
}


interface AuthContextType {
    authentication :  Authentication | null;
    login ?: (clientData : ClientData) => Promise<AxiosResponse<string>>;
    logout: () => void;
}

const defaultAuthContext: AuthContextType = {
    authentication: null,
    logout: () => {
      console.warn('logout function is not implemented');
    }
};

const AuthContext = createContext<AuthContextType>( defaultAuthContext);

export const useAuth = () => useContext(AuthContext);

export default function AuthProvider({children} : {children : ReactNode})  {

    const [authentication, setAuthentication] = useState<Authentication | null>(null);
    const [requestInjector, setRequestInjector] = useState<number | null>(null);

    useEffect(() => {
        setRequestInjector(apiClient.interceptors.request.use((config) => {
            config.headers.Authorization="Basic YWRtaW46YWRtaW4="
            return config;
        }));
        setAuthentication({username: "admin", roles: ["ROLE_ADMIN"]})
    }, []);
    

    function logout() : void{
        setAuthentication(null);
        if(requestInjector != null) apiClient.interceptors.request.eject(requestInjector);
        setRequestInjector(null);

    }

    
    return (
        <AuthContext.Provider value={{
            authentication, 
            login: (clientData : ClientData) => login(clientData, { setAuthentication, setRequestInjector}), 
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}