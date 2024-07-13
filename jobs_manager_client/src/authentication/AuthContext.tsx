import React, { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { ClientData, login } from "./authenticationApi";
import { AxiosResponse, InternalAxiosRequestConfig } from "axios";
import apiClient from "../api/ApiClient";


interface AuthContextType {
    token :  string | null;
    login ?: (clientData : ClientData) => Promise<AxiosResponse<string>>;
    logout: () => void;
}

const defaultAuthContext: AuthContextType = {
    token: null,
    logout: () => {
      console.warn('logout function is not implemented');
    }
};

const AuthContext = createContext<AuthContextType>( defaultAuthContext);

export const useAuth = () => useContext(AuthContext);

export default function AuthProvider({children} : {children : ReactNode})  {

    const [token, setToken] = useState<string | null>(null);
    const [requestInjector, setRequestInjector] = useState<number | null>(null);

    // useEffect(() => {
    //     setRequestInjector(apiClient.interceptors.request.use((config) => {
    //         config.headers.Authorization="Basic YWRtaW46YWRtaW4="
    //         return config;
    //     }));
    //     setToken("Basic YWRtaW46YWRtaW4=");
    // }, []);
    

    function logout() : void{
        setToken(null);
        if(requestInjector != null) apiClient.interceptors.request.eject(requestInjector);
        setRequestInjector(null);

    }

    
    return (
        <AuthContext.Provider value={{
            token, 
            login: (clientData : ClientData) => login(clientData, { setToken, setRequestInjector}), 
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}