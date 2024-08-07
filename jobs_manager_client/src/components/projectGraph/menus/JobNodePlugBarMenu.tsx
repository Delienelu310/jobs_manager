import { useState } from "react";
import { PlugBarElement } from "../gof/PlugBarElement";
import { addJobNodePlug } from "../../../api/abstraction/jobNodeApi";
import { JobNodeElement } from "../gof/JobNodeElement";
import { ChannelList, ChannelTypes } from "../../../api/abstraction/projectApi";
import SecuredNode from "../../../authentication/SecuredNode";
import OpenerComponent from "../../OpenerComponent";
import { JobNodePrivilege, ProjectPrivilege } from "../../../api/authorization/privilegesApi";


export interface JobNodePlugBarMenu{
    element : PlugBarElement
}

const JobNodePlugBarMenu = ({element} : JobNodePlugBarMenu) => {
    
    const [newPlugLabel, setNewPlugLabel] = useState<string>("");

    const [plugs, setPlugs] = useState<{[key:string] : ChannelList}>(
        element.getOrientation() ? 
            (element.getParent() as JobNodeElement).getData().output
            :
            (element.getParent() as JobNodeElement).getData().input
    );
    
    return (
        <div>
            <h3>Job Node {element.getOrientation() ? "Outputs" : "Inputs"} menu</h3>

            <SecuredNode
                alternative={null}
                roles={null}
                moderator
                projectPrivilegeConfig={{
                    project: element.getGof().getProjectData(),
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT, ProjectPrivilege.MODERATOR]
                }}
                jobNodePrivilegeConfig={{
                    jobNode: (element.getParent() as JobNodeElement).getData(),
                    privileges: [JobNodePrivilege.MANAGER]
                }}
            >
                <OpenerComponent
                    closedLabel={<h5>Add {element.getOrientation() ? "Output" : "Input"}</h5>}
                    openedElement={
                        <div>
                            <strong>Label: </strong>
                            <input className="form-control m-2" value={newPlugLabel} onChange={e => setNewPlugLabel(e.target.value)}/>


                            <button className="btn btn-success m-2" onClick={e => {
                                addJobNodePlug(element.getGof().getProjectData().id, 
                                    (element.getParent() as JobNodeElement ).getData().id, 
                                    element.getOrientation(), 
                                    newPlugLabel
                                ).then(respone => element.getGof().getRefresh()()).catch(e => console.log(e))
                            }}>Add plug</button>
                        </div>
                    }
                />
                <hr/>
            </SecuredNode>
                
          
            <h5>Labels list:</h5>
            
            {Object.entries(plugs).map( ([key, channelList]) => ( <div style={{
                margin: "30px 15%", 
                borderBottom: "3px solid black",
                borderTop: "3px solid black",
            }}>
                <h5>{key}</h5>
              
                {channelList.channelList.map( (channelData) => (
                    
                    <div style={{
                        margin: "20px 15%",
                        borderBottom: "2px solid black"
                    }}>
                        <strong>Channel: {channelData.channelDetails.name}</strong>
                        <br/>
                        <strong>Type : {channelData.channelDetails.type}</strong>
                        <br/>
                        <strong>Header : {channelData.channelDetails.headers.join(", ")}</strong>
                    </div>
                ))}

            </div>))}
        </div>
    );
}


export default JobNodePlugBarMenu;