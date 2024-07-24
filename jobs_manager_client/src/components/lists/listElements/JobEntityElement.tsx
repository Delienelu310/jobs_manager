import { JobEntitySimple } from "../../../api/ilum_resources/jobEntityApi";


export interface JobEntityElementContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,
    setQueueDependency :  React.Dispatch<React.SetStateAction<number>>,
}

export interface JobEntityElementArgs{
    data : JobEntitySimple,
    context : JobEntityElementContext
}

const JobEntityElement = ({data, context} : JobEntityElementArgs) => {
    return (
        <div>
            <h3>{data.jobEntityDetails.name}</h3>
            <strong>Author: {data.author.username}</strong>
            <br/>
            <h6>Description:</h6>
            <span>{data.jobEntityDetails.description}</span>
            <br/>
            <button className="btn btn-success m-2">More...</button>
            <br/>
            <button className="btn btn-danger m-2">Remove</button>
        </div>
    )
}

export default JobEntityElement;