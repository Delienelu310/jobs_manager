
import { useState } from "react";
import { addProjectPlug, ProjectFullData, removeProjectPlug } from "../../../api/abstraction/projectApi";

import { ChannelDetails, ChannelTypes} from "../../../api/abstraction/projectApi";



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
        <div>
            <div>
                <label>Label: </label>
                <input value={newPlugLabel} onChange={e => setNewPlugLabel(e.target.value)}/>
                <br/>
                <label>Channel name:</label>
                <input value={newPlugChannelDetails.name} onChange={e => setNewPlugChannelDetails({...newPlugChannelDetails, name : e.target.value})}/>
                <br/>
                <select value={newPlugChannelDetails.type}>
                    {Object.values(ChannelTypes).filter(key => typeof key != "string").map(key => <option value={key}>{ChannelTypes[key as number]}</option>)}
                </select>

                <label>Headers: </label>
                <input value={newHeader} onChange={e => setNewHeader(e.target.value)}/>
                <button className="btn btn-primary" onClick={e => {
                    let newHeaders = Array.from(newPlugChannelDetails.headers);
                    newHeaders.push(newHeader);

                    setNewPlugChannelDetails({
                        ...newPlugChannelDetails,
                        headers: newHeaders
                    })
                }}>Add header</button>

                {newPlugChannelDetails.headers.map(header => <div key={header}>{header} 
                    <button className="btn btn-danger" onClick={e => setNewPlugChannelDetails({
                        ...newPlugChannelDetails,
                        headers: newPlugChannelDetails.headers.filter(h => h != header)
                    })}>X</button>
                </div>)}

                <button className="btn btn-success" onClick={e => {
                    addProjectPlug(projectFullData.id, orientation, newPlugLabel, newPlugChannelDetails)
                        .then(respone => refresh())
                }}>Add plug</button>
            </div>
            



            {Object.entries(orientation ? projectFullData.outputChannels : projectFullData.inputChannels).map( ([key, channel]) => ( <div>
                <h5>{key}</h5>
                <div>
                    <h5>Channel: {channel.channelDetails.name}</h5>
                     <button className="btn btn-danger" onClick={e => 
                            removeProjectPlug(projectFullData.id, orientation, key)
                                .then(response => refresh())
                                .catch(e => console.log(e))
                    }>Delete</button>
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

               
            </div>) )}
        </div>
    );

}

export default ProjectPlugBarMenu;