import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { AppUserSimple } from "../../../api/authorization/usersApi";
import AppUserAdditionComponent from "./AppUserAdditionComponent";
import { FieldType } from "../../lists/Filter";
import AppUserElement, { AppUserElementContext } from "./AppUserElement";
import ServerBoundList from "../../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "./../JobNodeResourcesListPanel";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";



const PrivilegesList = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
            <SecuredNode
                projectPrivilegeConfig={null}
                roles={null}
                alternative={null}
                moderator
                jobNodePrivilegeConfig={{
                    jobNode: context.jobNodeData,
                    privileges : [JobNodePrivilege.MANAGER]
                }}
            >
                <div className="m-3">
                    <OpenerComponent
                        closedLabel={<h5>Add User To JobNode</h5>}
                        openedElement={
                            <AppUserAdditionComponent
                                context={{
                                    jobNodePageRefresh : context
                                }}
                            />
                        }
                    />
                </div>
            </SecuredNode>
            
            
           
            <h3>Job Node Privilege List</h3>
            <ServerBoundList<AppUserSimple, AppUserElementContext>
                Wrapper={AppUserElement}
                dependencies={[dependency]}
                context={{jobNodePageRefresh : context}}
                filter={{parameters : [
                    {label : "jobNodePrivileges", additionalData : Object.values(JobNodePrivilege), fieldType : FieldType.MultipleSelection}
                ]}}
                pager={{defaultPageSize:10}}
                endpoint={{
                    resourse: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/privileges?`,
                    count: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/privileges/count?`
                }}
            />
        </div>
    );
}


export default PrivilegesList;