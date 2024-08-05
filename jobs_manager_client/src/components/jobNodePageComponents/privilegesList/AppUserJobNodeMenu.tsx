import { useEffect, useState } from "react";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { AppUserSimple, retrieveUser } from "../../../api/authorization/usersApi";
import { addPrivilegeToJobNodeUser, JobNodePrivilege, removePrivilegeFromJobNodeUser, retrieveJobNodeUserPrivileges } from "../../../api/authorization/privilegesApi";


export interface AppUserJobNodeMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserJobNodeMenuArgs{   
    username : string,
    context : AppUserJobNodeMenuContext
}

const AppUserJobNodeMenu = ({username, context} :AppUserJobNodeMenuArgs ) => {
    
    const [data, setData] = useState<AppUserSimple | null>(null);
    const [privileges, setPrivileges] = useState<JobNodePrivilege[]>([]);


    function getData(){
        retrieveUser(username)
            .then(response => {
                setData(response.data);
            }).catch(e => console.log(e));
    }

    function getPrivileges(){
        retrieveJobNodeUserPrivileges(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username
        ).then(response => {
            setPrivileges(response.data);
        }).catch(e => console.log(e));
    }

    function removePrivilege(privilege : string){
        removePrivilegeFromJobNodeUser(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username,
            privilege
        ).then(r => {
            getPrivileges();
        }).catch(e => console.log(e));
    }

    function addPrivilege(privilege : string){
        addPrivilegeToJobNodeUser(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username,
            privilege
        ).then(r => {
            getPrivileges();
        }).catch(e => console.log(e));
    }


    useEffect(() => {
        getData();
        getPrivileges();
        
    }, []);

    return (
        <>
            {data ? 
                <div>
                    <h3>{data.username}</h3>
                    <strong>Full Name: </strong>
                    <br/>


                    <h5>Absent privileges:</h5>
                    {Object.values(JobNodePrivilege).filter(p => !privileges.includes(p) && p != JobNodePrivilege.MANAGER).map(p => <>
                        <strong>{p}</strong>
                        <button className="btn btn-primary m-2" onClick={e => addPrivilege(p)}>Add</button>
                        <br/>
                    </>)}

                    <h5>Present privileges:</h5>

                    {privileges.map(privilege => <>
                        <strong>{privilege}</strong>
                        <button className="btn btn-danger m-2" onClick={e => removePrivilege(privilege)}>X</button>
                        <br/>
                    </>)}
                </div>
                :
                <div>Loading...</div>
            }
        
        </>
       
    )
}

export default AppUserJobNodeMenu;