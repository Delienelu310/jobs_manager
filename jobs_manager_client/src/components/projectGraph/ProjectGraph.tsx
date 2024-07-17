import { useEffect, useRef, useState } from "react";
import { ProjectGraph  } from "../../api/ui/projectGraphApi";
import { ChannelFullData, ProjectFullData } from "../../api/abstraction/projectApi";

import { JobNodeElement, StaticJobNodeElementConfig } from "./gof/JobNodeElement";
import { StaticPlugBarConfig, PlugBarElement } from "./gof/PlugBarElement";
import { GOF, StaticCanvasConfig } from "./gof/GOF";
import { NullGraphElement } from "./gof/NullGraphElement";
import { PlugElement } from "./gof/PlugElement";
import { ChannelElement, StaticChannelConfig } from "./gof/ChannelElement";

interface StaticGraphCanvasConfig{
    
    canvas : StaticCanvasConfig;
    jobNodes : StaticJobNodeElementConfig;
    projectPlugs : StaticPlugBarConfig;
    channels : StaticChannelConfig;

}

interface ProjectGraphComponent{
    projectGraph : ProjectGraph,
    projectFullData : ProjectFullData,
    staticConfig : StaticGraphCanvasConfig,
}

const ProjectGraphComponent = ({projectFullData, projectGraph, staticConfig} : ProjectGraphComponent) => {



    const canvasRef = useRef<HTMLCanvasElement>(null);
    const [gof, setGof] = useState<GOF>(new GOF(
        staticConfig.canvas,
        projectFullData,
        {  
            offset: {
                x: 0,
                y: 0
            }
        }
    ));

    const [jobNodeName, setJobNodeName] = useState<string>("");


    function prepareGof(){

        let newGof : GOF = new GOF( staticConfig.canvas,
            projectFullData,
            {  
                offset: {
                    x: 0,
                    y: 0
                }
            }
        );

        //1. prepare job nodes
        //2. prepare project plugs
        //3. create channels
        //4. set channels input/output Ids

        //1. 
        for(let jobNode of projectFullData.jobNodes){
            let vertice = projectGraph.vertices.filter(v => v.jobNode.id == jobNode.id)[0];

            let jobNodeElement = new JobNodeElement(
                newGof, 
                jobNode, 
                vertice,
                staticConfig.jobNodes);

                newGof.addElement(jobNodeElement);
        }


        //2.
        let inputPlugBarElement : PlugBarElement =  new PlugBarElement(newGof, new NullGraphElement(), staticConfig.projectPlugs, false);
        console.log("inputPlugBarElement" + inputPlugBarElement.getOrientation());
        let outputPlugBarElement : PlugBarElement = new PlugBarElement(newGof, new NullGraphElement(), staticConfig.projectPlugs, true);
        console.log("outputPlugBarElement" + outputPlugBarElement.getOrientation());
        newGof.addElement(inputPlugBarElement);
        newGof.addElement(outputPlugBarElement);


        //3. 
        let channelElements : Map<string, ChannelElement> = new Map<string, ChannelElement>([]);
        for(let channel of projectFullData.channels){
            let channelElement : ChannelElement = new ChannelElement(newGof, channel, staticConfig.channels);
            channelElements.set(channel.id, channelElement);
        }


        //4. 
        // add plugs from project inputs/outputs
        for(let [key, channel] of Object.entries(projectFullData.inputChannels)){
            let elem = channelElements.get(channel.id);
            if(!elem) continue;

            elem.getInputIds().add(`project_input_${key}`);
        }
        for(let [key, channel] of Object.entries(projectFullData.outputChannels)){
            let elem = channelElements.get(channel.id);
            if(!elem) continue;
            
            elem.getOutputIds().add(`project_output_${key}`);
        }

        //add plugs from jobnodes
        for(let jobNode of projectFullData.jobNodes){
            for(let [label, channelList] of Object.entries(jobNode.input)){
                for(let channel of channelList.channelList){
                    let elem = channelElements.get(channel.id);
                    if(!elem) continue;

                    elem.getOutputIds().add(`jobnode_${jobNode.id}_input_${label}`);
                }
            }
            for(let [label, channelList] of Object.entries(jobNode.output)){
                for(let channel of channelList.channelList){
                    let elem = channelElements.get(channel.id);
                    if(!elem) continue;

                    elem.getInputIds().add(`jobnode_${jobNode.id}_output_${label}`);
                }
            }
        }

        channelElements.forEach(elem => newGof.addElement(elem));

        setGof(newGof);
    }



    function drawCanvas(
        ctx : CanvasRenderingContext2D
    ){
        ctx.strokeStyle = 'black'; 
        ctx.lineWidth = 5; 

        ctx.strokeRect(0, 0, staticConfig.canvas.width, staticConfig.canvas.height);
    }


    useEffect(() => {

        prepareGof();

    }, [projectGraph, projectFullData]);


    useEffect(() => {
        const canvas = canvasRef.current;
        if(canvas == null) return;
        const ctx = canvas.getContext('2d');
        if(!ctx) return;
        
        ctx.clearRect(0, 0, staticConfig.canvas.width, staticConfig.canvas.height);
        drawCanvas(ctx);
        gof.draw(ctx);
    }, [gof]);

    return (
        <div>
            {projectGraph && projectFullData && gof && <>
                <canvas ref={canvasRef} width={staticConfig.canvas.width} height={staticConfig.canvas.height}/>
                <div>
                    <button>Cursor</button>
                    <button>Connect</button>
                    <button>Delete</button>
                    <button>Add JobNode</button>
                    <div>
                        <input value={jobNodeName} onChange={e => setJobNodeName(e.target.value)}/>
                    </div>
                </div>
            </>}
        </div>
    )
}

export default ProjectGraphComponent;