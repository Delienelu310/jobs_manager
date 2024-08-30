import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { ClientData, login } from "./authenticationApi";
import { AxiosResponse } from "axios";
import apiClient from "../api/ApiClient";
import { useCookies } from 'react-cookie';
import { jwtDecode } from "jwt-decode";
import { JwtPayload } from "./authenticationApi";

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

    const [cookies, setCookie, removeCookie] = useCookies(); 

    useEffect(() => {

        const token : string | undefined = cookies['authentication-token'];

        if(!token) return;

        
        try{
            const payload : JwtPayload = jwtDecode<JwtPayload>(token);
            setAuthentication({username : payload.sub, roles: payload.scope.split(" ")});
            
            setRequestInjector(apiClient.interceptors.request.use((config) => {
                config.headers.Authorization=`Bearer ${token}`
                return config;
            }));

        }catch(e){
            removeCookie("authentication-token");
        }

        

    }, []);
    

    function logout() : void{
        setAuthentication(null);
        if(requestInjector != null) apiClient.interceptors.request.eject(requestInjector);
        setRequestInjector(null);

        removeCookie("authentication-token");

    }

  

    return (
        <AuthContext.Provider value={{
            authentication, 
            login: (clientData : ClientData) => login(clientData, { setAuthentication, setRequestInjector, setCookie}), 
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}