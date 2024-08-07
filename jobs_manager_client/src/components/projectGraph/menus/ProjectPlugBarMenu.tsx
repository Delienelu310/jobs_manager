
import { useState } from "react";
import { addProjectPlug, ProjectFullData, removeProjectPlug } from "../../../api/abstraction/projectApi";

import { ChannelDetails, ChannelTypes} from "../../../api/abstraction/projectApi";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";
import { ProjectPrivilege } from "../../../api/authorization/privilegesApi";



export interface ProjectPlugBarArgs{
    
    orientation : boolean,
    projectFullData : ProjectFullData,
    refresh : () => void
}




const ProjectPlugBarMenu = ({projectFullData, orientation, refresh} : ProjectPlugBarArgs) => {

    const [newPlugLabel, setNewPlugLabel] = useState<string>("");
    const [newHeader, setNewHeader] = useState<string>("");
    const [newPlugChannelDetails, setNewPlugChannelDetails] = useState<ChannelDetails>({
        name : "",
        type : ChannelTypes.MINIO,
        headers : [],
    });




    return (
        <div style={{
            margin: "30px 15%"
        }}>
            
            <h3>Project {orientation ? "outputs" : "inputs"} bar</h3>

            <SecuredNode
                alternative={null}
                jobNodePrivilegeConfig={null}
                roles={null}
                moderator
                projectPrivilegeConfig={{
                    project: projectFullData,
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT]
                }}
            >
                <OpenerComponent
                    closedLabel={<h5>Add new label</h5>}
                    openedElement={
                        <div>
                            <strong>Label: </strong>
                            <input className="form-control m-2" value={newPlugLabel} onChange={e => setNewPlugLabel(e.target.value)}/>
                    
                            <strong>Channel name:</strong>
                            <input className="form-control m-2" value={newPlugChannelDetails.name} onChange={e => setNewPlugChannelDetails({...newPlugChannelDetails, name : e.target.value})}/>
                
                            <strong>Channel type:</strong>
                            <select className="form-control m-2" value={newPlugChannelDetails.type}>
                                {Object.values(ChannelTypes).map(key => <option value={key}>{key}</option>)}
                            </select>
            
                            <h5>Headers: </h5>
                            
                            {newPlugChannelDetails.headers.map((header, index) => 
                                <div key={index + "_" + header} style={{margin: "20px 15%", height: "40px", borderBottom: "1px solid black"}}>
                                    <span style={{fontSize: "20ps"}}>{header} </span>
                                    <button style={{float:"right"}} className="btn btn-danger" onClick={e => {
                                        
                                        let newHeaders = Array.from(newPlugChannelDetails.headers);
                                        newHeaders.splice(index, 1);

                                        setNewPlugChannelDetails({
                                            ...newPlugChannelDetails,
                                            headers: newHeaders
                                        })
                                    }}>X</button>
                                </div>
                            )}
            
                            <strong>Add new Header:</strong>
                            <input className="form-control m-2" value={newHeader} onChange={e => setNewHeader(e.target.value)}/>
                            <button className="btn btn-primary m-2" onClick={e => {
                                let newHeaders = Array.from(newPlugChannelDetails.headers);
                                newHeaders.push(newHeader);
            
                                setNewPlugChannelDetails({
                                    ...newPlugChannelDetails,
                                    headers: newHeaders
                                })
                            }}>Add header</button>
            
                            <h5>Actions:</h5>
            
                            <button className="btn btn-success m-3" onClick={e => {
                                addProjectPlug(projectFullData.id, orientation, newPlugLabel, newPlugChannelDetails)
                                    .then(respone => refresh())
                            }}>Add plug</button>
                        </div>
                    }
                />
                
                <hr/>
            </SecuredNode>

            <h5>Labels</h5>
            <hr/>

            {Object.entries(orientation ? projectFullData.outputChannels : projectFullData.inputChannels).map( ([key, channel]) => ( 
                <div>
                    <h5>{key}</h5>
                    <div>
                        <h5 className="m-3">Channel: </h5>
                        <strong>Name:</strong>{channel.channelDetails.name}
                        <br/>
                        <strong>Type : {channel.channelDetails.type}</strong>
                        <br/>
                        <strong>Header : {channel.channelDetails.headers.join(", ")}</strong>
                        <br/>

                        {channel.inputJobs && channel.inputJobs.length > 0 && <>
                           
                            <h5 className="m-3">Input Jobs:</h5>
                            {channel.inputJobs.map(job => 
                                <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                                    {job.jobNodeDetails.name}
                                    <br/>
                                    {job.id} 
                                </div>
                            )}
                        </>}
                       
                        {channel.outputJobs && channel.outputJobs.length > 0 && <>
                     
                            <h5 className="m-3">Output Jobs:</h5>
                            {channel.outputJobs.map(job => 
                                <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                                    {job.jobNodeDetails.name}
                                    <br/>
                                    {job.id} 
                                </div>
                            )}
                        </>}
                        
                       
                    </div>  
                    <button className="btn btn-danger m-3" onClick={e => 
                        removeProjectPlug(projectFullData.id, orientation, key)
                            .then(response => refresh())
                            .catch(e => console.log(e))
                    }>Delete Label</button>
                    <hr/>
                </div>
            ) )} 
            
        </div>
    );

}

export default ProjectPlugBarMenu;