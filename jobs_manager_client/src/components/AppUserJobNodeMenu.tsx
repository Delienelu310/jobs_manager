import { useEffect, useState } from "react";
import { JobNodePageRefresh } from "../pages/JobNodePage";
import { AppUserSimple } from "../api/authorization/usersApi";


export interface AppUserJobNodeMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserJobNodeMenuArgs{   
    username : string,
    context : AppUserJobNodeMenuContext
}

const AppUserJobNodeMenu = ({username, context} :AppUserJobNodeMenuArgs ) => {
    
    const [data, setData] = useState<AppUserSimple | null>(null);
    
    useEffect(() => {

    }, []);

    return (
        <>
            {data ? 
                <div>
                    <h3>{data.username}</h3>

                </div>
                :
                <div>Loading...</div>
            }
        
        </>
       
    )
}

export default AppUserJobNodeMenu;