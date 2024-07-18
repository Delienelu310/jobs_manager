import { useState } from "react";
import { ChannelFullData, ChannelTypes, ProjectFullData, removeProjectPlug } from "../../../api/abstraction/projectApi";
import { PlugElement } from "../gof/PlugElement";
import { PlugBarElement } from "../gof/PlugBarElement";


export interface ProjectPlugArgs{
    element : PlugElement
}


const ProjectPlug = ({element} : ProjectPlugArgs) => {

    const [channelData, setChannelData] = useState<ChannelFullData>((element.getParent() as PlugBarElement).getOrientation() ?
        element.getGof().getProjectData().outputChannels[element.getLabel()]
        :
        element.getGof().getProjectData().inputChannels[element.getLabel()]
    );

    return (
        <div>
            <h3>{element.getLabel()}</h3>

            <div>
                <h5>Channel: {channelData.channelDetails.name}</h5>
                <span>Type : {channelData.channelDetails.type}</span>
                <br/>
                <span>Header : {channelData.channelDetails.headers.join(", ")}</span>
  
                {channelData.inputJobs.length > 0 && <><hr/><h6>Input Jobs:</h6></>}
                {channelData.inputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}

                {channelData.outputJobs.length > 0 && <><hr/><h6>Output Jobs:</h6></>}
                {channelData.outputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}
            </div>  

            <button className="btn btn-danger" onClick={e => {
                removeProjectPlug(element.getGof().getProjectData().id, 
                    (element.getParent() as PlugBarElement).getOrientation(), 
                    element.getLabel()
                ).then(response => element.getGof().getRefresh()());
            }}>Delete</button>
        </div>
    );
}

export default ProjectPlug; 