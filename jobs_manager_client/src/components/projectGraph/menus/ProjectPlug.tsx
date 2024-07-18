import { ChannelFullData } from "../../../api/abstraction/projectApi";


export interface ProjectPlugArgs{
    label : string, 
    channel : ChannelFullData
}


const ProjectPlug = ({label, channel} : ProjectPlugArgs) => {
    return (
        <div>
            <h3>{label}</h3>

            <div>
                <h5>Channel: {channel.channelDetails.name}</h5>
                <span>Type : {channel.channelDetails.type}</span>
                <br/>
                <span>Header : {channel.channelDetails.headers.join(", ")}</span>
                <hr/>

                {channel.inputJobs.length > 0 && <h6>Input Jobs:</h6>}
                {channel.inputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}
                <hr/>

                {channel.outputJobs.length > 0 && <h6>Output Jobs:</h6>}
                {channel.outputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}
            </div>  

            <button className="btn btn-danger">Delete</button>
        </div>
    );
}

export default ProjectPlug; 