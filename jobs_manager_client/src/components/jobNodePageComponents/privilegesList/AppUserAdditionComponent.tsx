
import { addPrivilegeToJobNodeUser, JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { AppUserSimple } from "../../../api/authorization/usersApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage"
import AppUserJobNodeMenu from "./AppUserJobNodeMenu";
import { FieldType } from "../../lists/Filter";
import AppUserAddElement, { AppUserAddElementContext } from "./AppUserAddElement";
import ServerBoundList from "../../lists/ServerBoundList";


export interface AppUserAdditionComponentContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserAdditionComponentArgs{
    context : AppUserAdditionComponentContext
}

const AppUserAdditionComponent = ({context} : AppUserAdditionComponentArgs) => {

    return (
        <div>
            
           <ServerBoundList<AppUserSimple, AppUserAddElementContext>
                Wrapper={AppUserAddElement}
                context={{ 
                    action : (username : string) => {

                        addPrivilegeToJobNodeUser(
                            context.jobNodePageRefresh.projectId,
                            context.jobNodePageRefresh.jobNodeId,
                            username,
                            JobNodePrivilege.VIEWER
                        ).then(r => {
                            context.jobNodePageRefresh.setMenu(
                                <AppUserJobNodeMenu
                                    username={username}
                                    context={context}
                                />        
                            );
                        }).catch(e => console.log(e));
                    }
                }}
                dependencies={[]}
                endpoint={{
                    count : `/users/count?`,
                    resourse: `/users?`
                }}
                filter={{parameters : [
                    {additionalData : [], fieldType : FieldType.SingleInput, label : "fullname"}
                ]}}
                pager={{defaultPageSize: 10}}

            />
        </div>
    );
    
}

export default AppUserAdditionComponent;