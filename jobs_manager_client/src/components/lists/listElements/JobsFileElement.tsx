import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";

export interface JobsFileElementArgs{
    data : JobsFileSimple
}

const JobsFileElement = ({data} : JobsFileElementArgs) => {
    return (
        <div>
            <hr/>
            <h3>{data.jobDetails.name}</h3>
            <span>ID: {data.id}</span>
            <br/>
            <strong>Extension: {data.extension}</strong>
            <br/>
            <h5>Description:</h5>
            <p>
                {data.jobDetails.description}
            </p>
            <h5>Classes used:</h5>
            {data.allClasses.map(cl => <><i>{cl}</i> <br/></>)}
            <hr/>
        
        </div>
    );
}


export default JobsFileElement;