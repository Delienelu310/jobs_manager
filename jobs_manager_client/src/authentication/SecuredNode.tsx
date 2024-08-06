import { JobNodeFullData, ProjectFullData } from "../api/abstraction/projectApi";
import { JobNodePrivilege, ProjectPrivilege } from "../api/authorization/privilegesApi";
import { Roles } from "../api/authorization/usersApi";
import { useAuth } from "./AuthContext";


export interface ProjectPrivilegesConfig{
    project : ProjectFullData,
    privileges : ProjectPrivilege[]
}

export interface JobNodePrivilegesConfig{
    jobNode : JobNodeFullData
    privileges : JobNodePrivilege[]
}

export interface SecuredNodeArgs{
    node : JSX.Element
    moderator : boolean
    roles :  Roles[],
    jobNodePrivilegeConfig : JobNodePrivilegesConfig | null,
    projectPrivilegeConfig : ProjectPrivilegesConfig | null

}

const SecuredNode = ({
    moderator = true,
    roles,
    jobNodePrivilegeConfig,
    projectPrivilegeConfig,
    node
} : SecuredNodeArgs) => {

    const {authentication} = useAuth();


    if(!authentication) return null;
    if(
        !authentication.roles.includes("ROLE_ADMIN") 
        &&
        ! (authentication.roles.includes("ROLE_MODERATOR") && moderator)
        &&
        ! (authentication.roles.filter(role => roles.map(r => Roles[r] as string).includes(role)).length > 0)
    ) return null;

    if(projectPrivilegeConfig){
        if(!projectPrivilegeConfig.project.privileges[authentication.username]) return null;

        if(
            ! 
            (projectPrivilegeConfig.project.privileges[authentication.username].list.filter(
                privilege => projectPrivilegeConfig.privileges.map(p => ProjectPrivilege[p] as string).includes(privilege)
            ).length > 0)
        ) return null;

    }

    if(jobNodePrivilegeConfig){
        if(!jobNodePrivilegeConfig.jobNode.privileges[authentication.username]) return null;

        if(
            ! 
            (jobNodePrivilegeConfig.jobNode.privileges[authentication.username].list.filter(
                privilege => jobNodePrivilegeConfig.privileges.map(p => JobNodePrivilege[p] as string).includes(privilege)
            ).length > 0)
        ) return null;
    }


    return node
}

export default SecuredNode;