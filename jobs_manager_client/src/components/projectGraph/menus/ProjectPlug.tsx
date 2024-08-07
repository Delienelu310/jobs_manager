import { useState } from "react";
import { ChannelFullData, ChannelTypes, ProjectFullData, removeProjectPlug } from "../../../api/abstraction/projectApi";
import { PlugElement } from "../gof/PlugElement";
import { PlugBarElement } from "../gof/PlugBarElement";
import SecuredNode from "../../../authentication/SecuredNode";
import { ProjectPrivilege } from "../../../api/authorization/privilegesApi";


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
            <h3>Project {(element.getParent() as PlugBarElement).getOrientation() ? "Output" : "Input"}</h3>
          

            <div>
                <strong>Name: </strong>{element.getLabel()}<br/>

                <h5 className="m-3">Channel: </h5>
                <strong>Channel Name:</strong>{channelData.channelDetails.name}<br/>
                <strong>Type : {channelData.channelDetails.type}</strong> <br/>
                <strong>Header : {channelData.channelDetails.headers.join(", ")}</strong>
  
                {channelData.inputJobs && channelData.inputJobs.length > 0 && <>
                           
                    <h5 className="m-3">Input Jobs:</h5>
                    {channelData.inputJobs.map(job => 
                        <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                            {job.jobNodeDetails.name}
                            <br/>
                            {job.id} 
                        </div>
                    )}
                </>}

                {channelData.outputJobs && channelData.outputJobs.length > 0 && <>
                           
                    <h5 className="m-3">Input Jobs:</h5>
                    {channelData.outputJobs.map(job => 
                        <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                            {job.jobNodeDetails.name}
                            <br/>
                            {job.id} 
                        </div>
                    )}
                </>}

            </div>  

            <SecuredNode
                jobNodePrivilegeConfig={null}
                alternative={null}
                roles={null}
                moderator
                projectPrivilegeConfig={{
                    project: element.getGof().getProjectData(),
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT]
                }}
            
            >
                <button className="btn btn-danger" onClick={e => {
                    removeProjectPlug(element.getGof().getProjectData().id, 
                        (element.getParent() as PlugBarElement).getOrientation(), 
                        element.getLabel()
                    ).then(response => element.getGof().getRefresh()());
                }}>Delete</button>

            </SecuredNode>

            
        </div>
    );
}

export default ProjectPlug; 