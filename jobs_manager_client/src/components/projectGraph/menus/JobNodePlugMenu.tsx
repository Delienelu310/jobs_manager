import { ChannelList, JobNodeFullData } from "../../../api/abstraction/projectApi";



export interface JobNodePlugMenuArgs{
    label : string,
    jobNodeData : JobNodeFullData,
    refresh : () => void
}

const JobNodePlugMenu = ({
    label,
    jobNodeData,
    refresh
} : JobNodePlugMenuArgs) => {
    return (
        <div>
            <h3>{label}</h3>
            {/* {jobNodeData[]} */}
        </div>
    );
}

export default JobNodePlugMenu;