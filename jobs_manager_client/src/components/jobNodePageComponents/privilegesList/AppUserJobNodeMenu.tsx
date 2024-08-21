import { useEffect, useState } from "react";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import { AppUserSimple, retrieveUser } from "../../../api/authorization/usersApi";
import { addPrivilegeToJobNodeUser, JobNodePrivilege, removePrivilegeFromJobNodeUser, removeUserFromJobNode, retrieveJobNodeUserPrivileges } from "../../../api/authorization/privilegesApi";
import SecuredNode from "../../../authentication/SecuredNode";
import { NotificationType, useNotificator } from "../../notifications/Notificator";


export interface AppUserJobNodeMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserJobNodeMenuArgs{   
    username : string,
    context : AppUserJobNodeMenuContext
}

const AppUserJobNodeMenu = ({username, context} :AppUserJobNodeMenuArgs ) => {
    

    const {catchRequestError, pushNotification} = useNotificator();

    const [data, setData] = useState<AppUserSimple | null>(null);
    const [privileges, setPrivileges] = useState<JobNodePrivilege[]>([]);


    function getData(){
        retrieveUser(username)
            .then(response => {
                setData(response.data);
            }).catch(catchRequestError);
    }

    function getPrivileges(){
        retrieveJobNodeUserPrivileges(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username
        ).then(response => {
            setPrivileges(response.data);
        }).catch(catchRequestError);
    }

    function removePrivilege(privilege : string){
        removePrivilegeFromJobNodeUser(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username,
            privilege
        ).then(r => {
            getPrivileges();
        }).catch(catchRequestError);
    }

    function addPrivilege(privilege : string){
        addPrivilegeToJobNodeUser(
            context.jobNodePageRefresh.projectId,
            context.jobNodePageRefresh.jobNodeId,
            username,
            privilege
        ).then(r => {
            getPrivileges();
        }).catch(catchRequestError);
    }

    function removeUser(){
        removeUserFromJobNode(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, username)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(context.jobNodePageRefresh.chosenResourceList 
                    && context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.PRIVILLEGES 
                ) context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());

                pushNotification({
                    message : "The user was removed successfully",
                    type : NotificationType.INFO,
                    time : 5
                });

            }).catch(catchRequestError);
        ;
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
                    <strong>Full Name: {data.appUserDetails.fullname} </strong>
                    
                    
                    <hr/>

                    <h5>Absent privileges:</h5>
                    {Object.values(JobNodePrivilege).filter(p => !privileges.includes(p) && p != JobNodePrivilege.MANAGER).map(p => <>
                        <strong>{p}</strong>
                        <SecuredNode
                            projectPrivilegeConfig={null}
                            roles={null}
                            alternative={null}
                            moderator
                            jobNodePrivilegeConfig={{
                                jobNode: context.jobNodePageRefresh.jobNodeData,
                                privileges: [JobNodePrivilege.MANAGER]
                            }}
                        >
                            <button className="btn btn-primary m-2" onClick={e => addPrivilege(p)}>Add</button>
                        </SecuredNode>
                      
                        <br/>
                    </>)}

                    <h5>Present privileges:</h5>

                    {privileges.map(privilege => <>
                        <strong>{privilege}</strong>
                        <SecuredNode
                            projectPrivilegeConfig={null}
                            roles={null}
                            alternative={null}
                            moderator
                            jobNodePrivilegeConfig={{
                                jobNode: context.jobNodePageRefresh.jobNodeData,
                                privileges: [JobNodePrivilege.MANAGER]
                            }}
                        >
                             <button className="btn btn-danger m-2" onClick={e => removePrivilege(privilege)}>X</button>
                        </SecuredNode>
                       
                        <br/>
                    </>)}

                    <SecuredNode
                        projectPrivilegeConfig={null}
                        roles={null}
                        alternative={null}
                        moderator
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges: [JobNodePrivilege.MANAGER]
                        }}
                    >
                        <hr/>
                    
                        <button className="btn btn-danger m-2" onClick={removeUser}>Remove User</button>
                    </SecuredNode>
                  
                </div>
                :
                <div>Loading...</div>
            }
        
        </>
       
    )
}

export default AppUserJobNodeMenu;