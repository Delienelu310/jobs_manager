import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import JobsFileMenu from "../../JobsFileMenu";


export interface JobsFileRemoveElementContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>
    refreshJobScript : () => void
}

export interface JobsFileRemoveElementArgs{
    data : JobsFileSimple,
    context : JobsFileRemoveElementContext
}



const JobsFileRemoveElement = ({data, context} : JobsFileRemoveElementArgs) => {


    function removeJobsFileFromJobScript(){
        
    }

    return (
        <div>
            <hr/>
            <h3>{data.jobDetails.name}</h3>
            <span>ID: {data.id}</span>
            <h5>Classes used:</h5>
            {data.allClasses.map(cl => <><i>{cl}</i> <br/></>)}
            <br/>

            <button className="btn btn-danger">Remove</button>
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


export default JobsFileRemoveElement;