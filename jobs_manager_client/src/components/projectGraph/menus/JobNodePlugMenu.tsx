
import { useState } from "react";
import { JobNodeElement } from "../gof/JobNodeElement";
import { PlugBarElement } from "../gof/PlugBarElement";
import { PlugElement } from "../gof/PlugElement";
import { ChannelList, ChannelTypes } from "../../../api/abstraction/projectApi";
import { removeJobNodePlug } from "../../../api/abstraction/jobNodeApi";



export interface JobNodePlugMenuArgs{
    element : PlugElement
}

const JobNodePlugMenu = ({
    element
} : JobNodePlugMenuArgs) => {


    const [channels, setChannels] = useState<{[key:string]:ChannelList}>(
        (element.getParent() as PlugBarElement).getOrientation() ? 
            (element.getParent().getParent() as JobNodeElement).getData().output
            :
            (element.getParent().getParent() as JobNodeElement).getData().input
    );

    return (
        <div>
            <h3>{element.getLabel()}</h3>
            <button className="btn btn-danger" onClick={e => {
                removeJobNodePlug(element.getGof().getProjectData().id, 
                    (element.getParent().getParent() as JobNodeElement).getData().id,
                    (element.getParent() as PlugBarElement).getOrientation(), 
                    element.getLabel()
                ).then(response => element.getGof().getRefresh()());
            }}>Delete</button>


            <h3>Channel list:</h3>
            {Object.entries(channels).map( ([key, channelList]) => (
                <div>
                    <h3>{key}</h3>
                    {channelList.channelList.map(channelData => (
                        <div>
                            <h5>Channel: {channelData.channelDetails.name}</h5>
                            <span>Type : {channelData.channelDetails.type}</span>
                            <br/>
                            <span>Header : {channelData.channelDetails.headers.join(", ")}</span>

                            {channelData.inputJobs && channelData.inputJobs.length > 0 && <><hr/><h6>Input Jobs:</h6></>}
                            {channelData.inputJobs && channelData.inputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}

                            {channelData.outputJobs && channelData.outputJobs.length > 0 && <><hr/><h6>Output Jobs:</h6></>}
                            {channelData.outputJobs && channelData.outputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}
                        </div>
                    ))}
                    
                    <hr/>
                </div>
            ))}
            
        </div>
    );
}

export default JobNodePlugMenu;