import { JobEntitySimple } from "../../api/ilum_resources/jobEntityApi";
import { QueueTypes } from "../../api/ilum_resources/queueOperationsApi";
import { FieldType } from "../lists/Filter";
import JobEntityElement, { JobEntityElementContext } from "../lists/listElements/JobEntityElement";
import ServerBoundList from "../lists/ServerBoundList";
import { JobNodeResourceListArgs } from "./JobNodeResourcesListPanel";



const TestingJobsQueue = ({context, dependency} : JobNodeResourceListArgs) => {
    return (
        <div>
            <h3>Testing Jobs Queue:</h3>
                
            <ServerBoundList<JobEntitySimple, JobEntityElementContext>
                endpoint={{
                    resourse: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/queue/${QueueTypes.TESTING_JOBS}?`,
                    count: `/projects/${context.projectId}/job_nodes/${context.jobNodeId}/queue/${QueueTypes.TESTING_JOBS}/count?`
                }}
                filter={{parameters: [
                    {label: "author", additionalData: [], fieldType: FieldType.SingleInput}
                ]}}
                Wrapper={JobEntityElement}
                context={{
                    jobNodePageRefresh : context,
                    queueType : QueueTypes.TESTING_JOBS
                }}
                dependencies={[dependency]}
                pager={{defaultPageSize: 10}}
            
            />
        </div>
    );
}

export default TestingJobsQueue;