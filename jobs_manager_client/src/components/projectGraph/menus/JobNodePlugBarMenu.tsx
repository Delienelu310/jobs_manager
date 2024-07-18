import { useState } from "react";
import { PlugBarElement } from "../gof/PlugBarElement";
import { addJobNodePlug } from "../../../api/abstraction/jobNodeApi";
import { JobNodeElement } from "../gof/JobNodeElement";
import { ChannelList, ChannelTypes } from "../../../api/abstraction/projectApi";


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
            <div>
                <label>Label: </label>
                <input value={newPlugLabel} onChange={e => setNewPlugLabel(e.target.value)}/>


                <button className="btn btn-success" onClick={e => {
                    addJobNodePlug(element.getGof().getProjectData().id, 
                        (element.getParent() as JobNodeElement ).getData().id, 
                        element.getOrientation(), 
                        newPlugLabel
                    ).then(respone => element.getGof().getRefresh()()).catch(e => console.log(e))
                }}>Add plug</button>
            </div>
            
            
            {Object.entries(plugs).map( ([key, channelList]) => ( <div>
                <h3>{key}</h3>
                <h3>Channel list:</h3>
                {channelList.channelList.map( (channelData) => (
                    <div>
                        <div>
                            <h5>Channel: {channelData.channelDetails.name}</h5>
                            <span>Type : {ChannelTypes[channelData.channelDetails.type]}</span>
                            <br/>
                            <span>Header : {channelData.channelDetails.headers.join(", ")}</span>

                            {channelData.inputJobs.length > 0 && <><hr/><h6>Input Jobs:</h6></>}
                            {channelData.inputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}

                            {channelData.outputJobs.length > 0 && <><hr/><h6>Output Jobs:</h6></>}
                            {channelData.outputJobs.map(job => <>{job.jobNodeDetails.name}, id: {job.id} </>)}
                        </div>
                        <hr/>
                    </div>
                ))}

            </div>))}
        </div>
    );
}


export default JobNodePlugBarMenu;