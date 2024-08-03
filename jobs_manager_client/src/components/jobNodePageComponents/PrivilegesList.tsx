import { JobNodePrivilege } from "../../api/authorization/privilegesApi";
import { AppUserSimple } from "../../api/authorization/usersApi";
import AppUserAdditionComponent from "../AppUserAdditionComponent";
import { FieldType } from "../lists/Filter";
import AppUserElement, { AppUserElementContext } from "../lists/listElements/AppUserElement";
import ServerBoundList from "../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "./JobNodeResourcesListPanel";



const PrivilegesList = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
            <h3>Job Node Privilege List</h3>

            <hr/>
            <AppUserAdditionComponent
                context={{
                    jobNodePageRefresh : context
                }}
            />
            <hr/>

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