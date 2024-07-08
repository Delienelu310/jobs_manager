import React, { createContext, ReactNode, useContext, useState } from "react";
import { ClientData, login } from "./authenticationApi";
import { AxiosResponse } from "axios";


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

    const [token, setToken] = useState<string | null>("Basic YWRtaW46YWRtaW4=");

    function logout() : void{
        setToken(null);
    }

    
    return (
        <AuthContext.Provider value={{
            token, 
            login: (clientData : ClientData) => login(clientData, { setToken}), 
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}