import React from "react";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../pages/JobNodePage";



export interface JobNodeResourceListArgs{
    context : JobNodePageRefresh,
    dependency: number   
}


export interface JobNodeResourceListData{
    label : JobNodeResourceListsMembers,
    dependency : number,
    setDependency : React.Dispatch<React.SetStateAction<number>>,
    Component : React.FC<JobNodeResourceListArgs>
}

export interface JobNodeResourceListPanelArgs{
    context : JobNodePageRefresh,
    choices : JobNodeResourceListData[]
}

const JobNodeResourceListPanel = ({
    choices, context 
} : JobNodeResourceListPanelArgs) => {
    return (
        <div>
            <div style={{
                textAlign: "left",
                border: "1px solid black",
                background: "#ccc"
            }}>
                {choices.map(choice => (
                    <div style={{cursor: "pointer",display: "inline-block", width: "14.2%", borderLeft: "1px solid black ", textAlign: "center", lineHeight: "50px",
                        height: "50px", backgroundColor: "#ccc"
                    }} onClick={e => {context.setChosenResourceList(choice); console.log(context.chosenResourceList)}}><h4>{choice.label}</h4></div>
                ))}
            </div>

            
            {context.chosenResourceList && <div style={{marginTop: "50px"}}>
                <context.chosenResourceList.Component
                    context={context}
                    dependency={context.chosenResourceList.dependency}
                />
            </div>}

        </div>
    );
}

export default JobNodeResourceListPanel;