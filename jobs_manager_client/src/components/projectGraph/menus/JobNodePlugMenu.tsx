
import { useState } from "react";
import { JobNodeElement } from "../gof/JobNodeElement";
import { PlugBarElement } from "../gof/PlugBarElement";
import { PlugElement } from "../gof/PlugElement";
import { ChannelList, ChannelTypes } from "../../../api/abstraction/projectApi";
import { removeJobNodePlug } from "../../../api/abstraction/jobNodeApi";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege, ProjectPrivilege } from "../../../api/authorization/privilegesApi";



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
            <h3>Job Node {(element.getParent() as PlugBarElement).getOrientation() ? "Output" : "Input" }: {element.getLabel()}</h3>

    
            <h5>Channel list:</h5>
            {Object.entries(channels).map( ([key, channelList]) => (
                <div>
                    {channelList.channelList.map(channelData => (
                        <div style={{
                            margin: "0 15%",
                            padding: "20px 0",
                            borderBottom: "1px solid black",
                            borderTop: "1px solid black"
                        }}>
                            <strong>Name: {channelData.channelDetails.name || "name is not specified"}</strong>
                            <br/>
                            <strong>Type : {channelData.channelDetails.type}</strong>
                            <br/>
                            <strong>Header : {channelData.channelDetails.headers.join(", ")}</strong>

                        </div>
                    ))}
                    
                    <hr/>
                </div>
            ))}
            
            
            <SecuredNode
                roles={null}
                alternative={null}
                moderator
                jobNodePrivilegeConfig={{
                    jobNode: (element.getParent().getParent() as JobNodeElement).getData(),
                    privileges: [JobNodePrivilege.MANAGER]
                }}
                projectPrivilegeConfig={{
                    project : element.getGof().getContext().projectData,
                    privileges : [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT]
                }}
            >
                <button className="btn btn-danger" onClick={e => {
                    removeJobNodePlug(element.getGof().getContext().projectData.id, 
                        (element.getParent().getParent() as JobNodeElement).getData().id,
                        (element.getParent() as PlugBarElement).getOrientation(), 
                        element.getLabel()
                    ).then(response => {
                        element.getGof().getContext().refresh();
                        element.getGof().getContext().setMenuSource(null);
                    })
                    .catch(element.getGof().getContext().catchRequestError);
                }}>Delete</button>

            </SecuredNode>
            
        </div>
    );
}

export default JobNodePlugMenu;