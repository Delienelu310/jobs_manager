import React from "react";
import { JobNodeFullData, ProjectFullData } from "../api/abstraction/projectApi";
import { JobNodePrivilege, ProjectPrivilege } from "../api/authorization/privilegesApi";
import { Roles } from "../api/authorization/usersApi";
import { useAuth } from "./AuthContext";


export interface ProjectPrivilegesConfig{
    project : ProjectFullData,
    privileges : ProjectPrivilege[] | null
}

export interface JobNodePrivilegesConfig{
    jobNode : JobNodeFullData
    privileges : JobNodePrivilege[] | null
}

export interface SecuredNodeArgs{
    children : React.ReactNode,
    alternative : JSX.Element | null,
    moderator : boolean
    roles :  Roles[] | null,
    jobNodePrivilegeConfig : JobNodePrivilegesConfig | null,
    projectPrivilegeConfig : ProjectPrivilegesConfig | null

}



const SecuredNode = ({
    moderator = true,
    roles,
    jobNodePrivilegeConfig,
    projectPrivilegeConfig,
    children,
    alternative
} : SecuredNodeArgs) => {

   
    const {authentication} = useAuth();
    
    
    function isAuthorized() : boolean{
        if(!authentication) return false;

        if(
            authentication.roles.includes("ROLE_ADMIN") ||
            authentication.roles.includes("ROLE_MODERATOR") && moderator
        ) return true;

        if(roles != null){
            if( !(authentication.roles.filter(role => roles.map(r => Roles[r] as string).includes(role)).length > 0) ){
                return false;
            }
        }

        if(projectPrivilegeConfig){
            if(!projectPrivilegeConfig.project.privileges[authentication.username]) return false;

            if(projectPrivilegeConfig.privileges != null){
                if(
                    ! 
                    (projectPrivilegeConfig.project.privileges[authentication.username].list.filter(
                        privilege => projectPrivilegeConfig && 
                            projectPrivilegeConfig.privileges &&
                            projectPrivilegeConfig.privileges.map(p => ProjectPrivilege[p] as string).includes(privilege)
                    ).length > 0)
                ) return false;
            }
        }

        if(jobNodePrivilegeConfig){
            if(!jobNodePrivilegeConfig.jobNode.privileges[authentication.username]) return false;

            if(jobNodePrivilegeConfig.privileges != null){
                if(
                    ! 
                    (jobNodePrivilegeConfig.jobNode.privileges[authentication.username].list.filter(
                        privilege => jobNodePrivilegeConfig &&
                            jobNodePrivilegeConfig.privileges &&
                            jobNodePrivilegeConfig.privileges.map(p => JobNodePrivilege[p] as string).includes(privilege)
                    ).length > 0)
                ) return false;
            }
        }

        return true;

    }

    if(isAuthorized()){
        return (<>
            {children}
        </>)
    }else{
        return <>{alternative}</>
    }

    
}

export default SecuredNode;