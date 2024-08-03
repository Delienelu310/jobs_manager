import { JobEntitySimple } from "../../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../../api/ilum_resources/queueOperationsApi";
import { FieldType } from "../lists/Filter";
import JobEntityElement, { JobEntityElementContext } from "../lists/listElements/JobEntityElement";
import ServerBoundList from "../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "./JobNodeResourcesListPanel";



const JobsQueue = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
             <h3>Jobs Queue:</h3>
                
            <ServerBoundList<JobEntitySimple, JobEntityElementContext>
                endpoint={{
                    resourse: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}?`,
                count: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/queue/${QueueTypes.JOBS_QUEUE}/count?`
                }}
                filter={{parameters: [
                    {label: "author", additionalData: [], fieldType: FieldType.SingleInput}
                ]}}
                Wrapper={JobEntityElement}
                context={{
                    jobNodePageRefresh: context,
                    queueType : QueueTypes.JOBS_QUEUE
                }}
                dependencies={[dependency]}
                pager={{defaultPageSize: 10}}
            
            />
        </div>
    );
}


export default JobsQueue;