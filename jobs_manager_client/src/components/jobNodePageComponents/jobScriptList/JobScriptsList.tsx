import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptCreator from "./JobScriptCreator";
import { FieldType } from "../../lists/Filter";
import JobScriptElement, { JobScriptListContext } from "./JobScriptElement";
import ServerBoundList from "../../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "../JobNodeResourcesListPanel";
import OpenerComponent from "../../OpenerComponent";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import SecuredNode from "../../../authentication/SecuredNode";



const JobScriptsList = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
            <SecuredNode
                alternative={null}
                projectPrivilegeConfig={null}
                roles={null}
                moderator
                jobNodePrivilegeConfig={{
                    jobNode: context.jobNodeData,
                    privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
                }}
            >
                <OpenerComponent
                    closedLabel={<h4>Create Job Script</h4>}
                    openedElement={
                    <JobScriptCreator
                        context={{
                            jobNodePageRefresh : context
                        }}
                    />}
                />
                
                <hr/>
            </SecuredNode>
          
            
          

            <h3>List of Job Scripts</h3>

            <ServerBoundList<JobScriptSimple, JobScriptListContext>
                pager={{defaultPageSize: 10}}
                endpoint={{
                    resourse: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_scripts?`,
                    count: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/job_scripts/count?`
                }}
                dependencies={[dependency]}
                context={{
                    jobNodePageRefresh : context
                }}
                Wrapper={JobScriptElement}
                filter={{ parameters: [
                    {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection},
                    
                ]}}
            />
        </div>
    );
}

export default JobScriptsList;