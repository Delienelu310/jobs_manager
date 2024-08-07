
import { addPrivilegeToJobNodeUser, JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { AppUserSimple } from "../../../api/authorization/usersApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage"
import AppUserJobNodeMenu from "./AppUserJobNodeMenu";
import { FieldType } from "../../lists/Filter";
import AppUserAddElement, { AppUserAddElementContext } from "./AppUserAddElement";
import ServerBoundList from "../../lists/ServerBoundList";
import SecuredNode from "../../../authentication/SecuredNode";


export interface AppUserAdditionComponentContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserAdditionComponentArgs{
    context : AppUserAdditionComponentContext
}

const AppUserAdditionComponent = ({context} : AppUserAdditionComponentArgs) => {

    return (
        <SecuredNode
            roles={null}
            projectPrivilegeConfig={null}
            moderator
            alternative={<h5>You dont have privileges for this component</h5>}
            jobNodePrivilegeConfig={{
                jobNode: context.jobNodePageRefresh.jobNodeData,
                privileges: [JobNodePrivilege.MANAGER]

            }}
        >
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
        </SecuredNode>
       
    );
    
}

export default AppUserAdditionComponent;