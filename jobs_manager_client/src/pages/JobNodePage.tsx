import { useEffect } from "react";
import { useParams } from "react-router-dom";
import ServerBoundList from "../components/lists/ServerBoundList";
import { JobsFileSimple } from "../api/ilum_resources/jobsFilesApi";
import JobsFileElement from "../components/lists/listElements/JobsFileElement";
import { FieldType } from "../components/lists/Filter";


export interface JobNodePageInterface{

}


const JobNodePage = ({} : JobNodePageInterface) => {


    const {jobNodeId, projectId} = useParams();


    useEffect(() => {
        console.log(jobNodeId, projectId);
    }, []);


    return (
        <div>

            <h3>Jobs Files:</h3>
            <ServerBoundList<JobsFileSimple>
                pager={{defaultPageSize: 10}}
                filter={{parameters: [
                    {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                    {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                ]}} 
                Wrapper={JobsFileElement}
                endpoint={{
                    resourse : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files`,
                    count : `/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/count`
                }}
            />
        </div>
    );


}


export default JobNodePage;