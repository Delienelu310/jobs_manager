import { ChannelElement } from "../gof/ChannelElement";


export interface ChannelMenuArgs{
    element : ChannelElement
}

const ChannelMenu = ({element} : ChannelMenuArgs) => {
    return (
        <div>
            <h3>Channel Menu</h3>

            <strong>Channel Name:</strong>{element.getData().channelDetails.name}<br/>
                <strong>Type : {element.getData().channelDetails.type}</strong> <br/>
                <strong>Header : {element.getData().channelDetails.headers.join(", ")}</strong>
  
                {element.getData().inputJobs && element.getData().inputJobs.length > 0 && <>
                           
                    <h5 className="m-3">Input Jobs:</h5>
                    {element.getData().inputJobs.map(job => 
                        <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                            {job.jobNodeDetails.name}
                            <br/>
                            {job.id} 
                        </div>
                    )}
                </>}

                {element.getData().outputJobs && element.getData().outputJobs.length > 0 && <>
                           
                    <h5 className="m-3">Input Jobs:</h5>
                    {element.getData().outputJobs.map(job => 
                        <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                            {job.jobNodeDetails.name}
                            <br/>
                            {job.id} 
                        </div>
                    )}
                </>}
        </div>
    );
}

export default ChannelMenu;