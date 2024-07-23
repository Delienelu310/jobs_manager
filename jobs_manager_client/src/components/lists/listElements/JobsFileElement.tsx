import React from "react";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileMenu from "../../JobsFileMenu";

export interface JobsFileListContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>
}

export interface JobsFileElementArgs{
    data : JobsFileSimple,
    context : JobsFileListContext
}

const JobsFileElement = ({data, context} : JobsFileElementArgs) => {
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
            
            <br/>
            <button className="btn btn-primary" onClick={e => context.setMenu((
                <JobsFileMenu
                    data={data}
                    setMenu={context.setMenu}
                    setJobsFileListDependency={context.setJobsFileListDependency}
                />
            ))}>More...</button>
            <hr/>

            
        
        </div>
    );
}


export default JobsFileElement;