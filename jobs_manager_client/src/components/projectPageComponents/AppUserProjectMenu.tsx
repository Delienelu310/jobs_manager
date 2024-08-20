import { useEffect, useState } from "react";
import { AppUserSimple, retrieveUser } from "../../api/authorization/usersApi";
import {  addPrivilegeToProjectUser, ProjectPrivilege, removeModeratorFromProject, removePrivilegeFromProjectUser,  removeUserFromProject,  retrieveProjectUserPrivileges} from "../../api/authorization/privilegesApi";
import SecuredNode from "../../authentication/SecuredNode";
import { ProjectPrivilegeListContext } from "./PrivilegeList";
import { useNotificator } from "../notifications/Notificator";


export interface AppUserJobNodeMenuArgs{   
    username : string,
    context : ProjectPrivilegeListContext
}

const AppUserProjectMenu = ({username, context} :AppUserJobNodeMenuArgs ) => {

    const {catchRequestError, pushNotification} = useNotificator();
    
    const [data, setData] = useState<AppUserSimple | null>(null);
    const [privileges, setPrivileges] = useState<ProjectPrivilege[]>([]);


    function getData(){
        retrieveUser(username)
            .then(response => {
                setData(response.data);
            }).catch(catchRequestError);
    }

    function getPrivileges(){
        retrieveProjectUserPrivileges(
            context.projectData.id,
            username
        ).then(response => {
            setPrivileges(response.data);
            context.setUsersListDependency(Math.random());
        }).catch(catchRequestError);
    }

    function removePrivilege(privilege : string){
        removePrivilegeFromProjectUser(
            context.projectData.id,
            username,
            privilege
        ).then(r => {
            getPrivileges();
            context.refresh();
        }).catch(catchRequestError);
    }

    function addPrivilege(privilege : string){
        addPrivilegeToProjectUser(
            context.projectData.id,
            username,
            privilege
        ).then(r => {
            getPrivileges();
            context.refresh();
        }).catch(catchRequestError);
    }

    function removeUser(){
        
        let removePromise;

        if(context.projectData.privileges[username] && context.projectData.privileges[username].list.includes(ProjectPrivilege.MODERATOR)){
            removePromise = removeModeratorFromProject(context.projectData.id, username);
        }else{
            removePromise = removeUserFromProject(context.projectData.id, username);
        }


        removePromise
            .then(r => {
               context.setMenu(null);
               context.setUsersListDependency(Math.random());
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
                    {Object.values(ProjectPrivilege).filter(p => !privileges.includes(p) && p != ProjectPrivilege.ADMIN && p != ProjectPrivilege.MODERATOR)
                    .map(p => <>
                        <strong>{p}</strong>
                        <SecuredNode
                            projectPrivilegeConfig={{
                                project: context.projectData,
                                privileges : [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR]
                            }}
                            roles={null}
                            alternative={null}
                            moderator
                            jobNodePrivilegeConfig={null}
                        >
                            <button className="btn btn-primary m-2" onClick={e => addPrivilege(p)}>Add</button>
                        </SecuredNode>
                      
                        <br/>
                    </>)}

                    <h5>Present privileges:</h5>

                    {privileges.map(privilege => <>
                        <strong>{privilege}</strong>
                        <SecuredNode
                            projectPrivilegeConfig={{
                                privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR],
                                project :  context.projectData
                            }}
                            roles={null}
                            alternative={null}
                            moderator
                            jobNodePrivilegeConfig={null}
                        >
                             <button className="btn btn-danger m-2" onClick={e => removePrivilege(privilege)}>X</button>
                        </SecuredNode>
                       
                        <br/>
                    </>)}

                    <SecuredNode
                        projectPrivilegeConfig={{
                            privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR],
                            project : context.projectData
                        }}
                        roles={null}
                        alternative={null}
                        moderator
                        jobNodePrivilegeConfig={null}
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

export default AppUserProjectMenu;