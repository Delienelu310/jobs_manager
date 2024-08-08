
import { useState } from "react";
import { ProjectFullData } from "../../api/abstraction/projectApi";
import { addPrivilegeToProjectUser, ProjectPrivilege } from "../../api/authorization/privilegesApi";
import { AppUserSimple } from "../../api/authorization/usersApi";
import AppUserAddElement, { AppUserAddElementContext } from "../jobNodePageComponents/privilegesList/AppUserAddElement";
import ServerBoundList from "../lists/ServerBoundList";
import AppUserProjectMenu from "./AppUserProjectMenu";
import SecuredNode from "../../authentication/SecuredNode";
import { FieldType } from "../lists/Filter";
import AppUserElement from "./AppUserElement";
import OpenerComponent from "../OpenerComponent";


export interface PrivilegeListArgs{
    projectData : ProjectFullData,
    refresh : () => void
}


export interface ProjectPrivilegeListContext{
    projectData : ProjectFullData,
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>

    usersListDependency : number,
    setUsersListDependency : React.Dispatch<React.SetStateAction<number>>

    refresh : () => void

}

const PrivilegeList = ({projectData, refresh} : PrivilegeListArgs) => {

    const [menu, setMenu] = useState<JSX.Element | null>(null);
    const [usersListDependency, setUsersListDependency] = useState<number>(0);

    return (
        <div>

            {}

            <SecuredNode
                roles={null}
                projectPrivilegeConfig={{
                    project: projectData,
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR]  
                }}
                moderator
                alternative={null}
                jobNodePrivilegeConfig={null}
            >
                <OpenerComponent
                    closedLabel={<h3>Add User To Project</h3>}
                    openedElement={
                        <>
                            <h3>Add User To Project</h3>
                            <hr/>

                            <ServerBoundList<AppUserSimple, AppUserAddElementContext>
                                Wrapper={AppUserAddElement}
                                context={{ 
                                    action : (username : string) => {

                                        if(projectData.privileges[username] != undefined) return;

                                        addPrivilegeToProjectUser(
                                            projectData.id,
                                            username,
                                            ProjectPrivilege.VIEWER
                                        ).then(r => {
                                            setMenu(
                                                <AppUserProjectMenu
                                                    key={"menu_"+username}
                                                    username={username}
                                                    context={{
                                                        refresh:refresh,
                                                        projectData: projectData,
                                                        setMenu : setMenu,
                                                        setUsersListDependency: setUsersListDependency,
                                                        usersListDependency: usersListDependency
                                                    }}
                                                />        
                                            );
                                            setUsersListDependency(Math.random())
                                            refresh();
                                        }).catch(e => console.log(e));
                                    },
                                    isPreseent: (username : string) => projectData.privileges[username] != undefined
                                }}
                                dependencies={[usersListDependency]}
                                endpoint={{
                                    count : `/users/count?`,
                                    resourse: `/users?`
                                }}
                                filter={{parameters : [
                                    {additionalData : [], fieldType : FieldType.SingleInput, label : "fullname"}
                                ]}}
                                pager={{defaultPageSize: 10}}

                            />
                        </>
                    }
                />
       
            </SecuredNode>
            {menu && <>
                <hr/>
                <button className="btn btn-danger m-2" onClick={() => setMenu(null)}>Close Menu</button>
                <br/>
                {menu}
            
            </>}
           

            <hr/>

            <ServerBoundList<AppUserSimple, ProjectPrivilegeListContext>
                Wrapper={AppUserElement}
                context={{refresh: refresh, setMenu: setMenu, projectData : projectData, usersListDependency : usersListDependency, setUsersListDependency: setUsersListDependency}}
                dependencies={[usersListDependency]}
                endpoint={{
                    resourse: `/projects/${projectData.id}/privileges?`,
                    count: `/projects/${projectData.id}/privileges/count?`
                }}
                pager={{defaultPageSize: 10}}
                filter={{parameters: [
                    {label: "projectPrivileges", additionalData: Object.values(ProjectPrivilege), fieldType : FieldType.MultipleSelection}
                ]}}

            
            />

        </div>
    )

}

export default PrivilegeList;