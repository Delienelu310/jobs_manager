import { useEffect, useRef, useState } from "react";
import { JobNodeVerticeDetails, ProjectGraph, updateJobNodeVertice, updateProjectGraph  } from "../../api/ui/projectGraphApi";
import { ChannelDetails, ChannelFullData, ChannelTypes, JobNodeDetails, ProjectFullData } from "../../api/abstraction/projectApi";

import { JobNodeElement, StaticJobNodeElementConfig } from "./gof/JobNodeElement";
import { StaticPlugBarConfig, PlugBarElement } from "./gof/PlugBarElement";
import { DynamicCanvasConfig, GOF, StaticCanvasConfig } from "./gof/GOF";
import { NullGraphElement } from "./gof/NullGraphElement";
import { PlugElement } from "./gof/PlugElement";
import { ChannelElement, StaticChannelConfig } from "./gof/ChannelElement";
import { PanelMods } from "./gof/eventHandlers/PanelMods";
import { createJobNode } from "../../api/abstraction/jobNodeApi";

interface StaticGraphCanvasConfig{
    
    canvas : StaticCanvasConfig;
    jobNodes : StaticJobNodeElementConfig;
    projectPlugs : StaticPlugBarConfig;
    channels : StaticChannelConfig;

}

interface ProjectGraphComponent{
    refresh : () => void,
    projectGraph : ProjectGraph,
    setProjectGraph : (projectGraph : ProjectGraph) => void,
    projectFullData : ProjectFullData,
    staticConfig : StaticGraphCanvasConfig,

}

const ProjectGraphComponent = ({projectFullData, projectGraph, staticConfig, setProjectGraph, refresh} : ProjectGraphComponent) => {

    const canvasRef = useRef<HTMLCanvasElement>(null);
    const [mod, setMod] = useState<PanelMods>(PanelMods.CURSOR);
    const [menu, setMenu] = useState<JSX.Element>(<div>Choose Element...</div>);
    const [dynamicConfig, setDynamicConfig] = useState<DynamicCanvasConfig>({
        offset : {
            x : 0,
            y : 0
        },
        dragData: {
            start : null,
            elem : new NullGraphElement(),
            isDragging: false
        },
        connectMod:{
            input : null,
            output : null
        },
        elemOffset : {}
    });
    
    const [newChannelDetails, setNewChannelDetails] = useState<ChannelDetails>({
        name : "",
        type : ChannelTypes.MINIO,
        headers: []
    });
    const [newHeader, setNewHeader] = useState<string>("");


    const [gof, setGof] = useState<GOF>(new GOF(
        staticConfig.canvas,
        projectFullData,
        projectGraph,
        dynamicConfig,
        setDynamicConfig,
        setMenu,
        mod,
        refresh,
        newChannelDetails
    ));



    const [newJobNodeDetails, setNewJobNodeDetails] = useState<JobNodeDetails>({
        name : ""
    });
    const [newJobVerticeDetails, setNewJobVerticeDetails] = useState<JobNodeVerticeDetails>({x: 0, y : 0});

    

    function prepareGof(){

        let newGof : GOF = new GOF( 
            staticConfig.canvas,
            projectFullData,
            projectGraph,
            dynamicConfig,
            setDynamicConfig,
            setMenu,
            mod,
            refresh,
            newChannelDetails
        );

        //1. prepare job nodes
        //2. prepare project plugs
        //3. create channels
        //4. set channels input/output Ids

        //1. 
        for(let jobNode of projectFullData.jobNodes){
            let verticesFiltered = projectGraph.vertices.filter(v => v.jobNode.id == jobNode.id);
            let vertice = verticesFiltered[0];
            let jobNodeElement = new JobNodeElement(
                newGof, 
                jobNode, 
                vertice,
                staticConfig.jobNodes,
                setProjectGraph,
                setDynamicConfig
        );

                newGof.addElement(jobNodeElement);
        }


        //2.
        let inputPlugBarElement : PlugBarElement =  new PlugBarElement(newGof, new NullGraphElement(), staticConfig.projectPlugs, false);
        let outputPlugBarElement : PlugBarElement = new PlugBarElement(newGof, new NullGraphElement(), staticConfig.projectPlugs, true);
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

    }, [projectGraph, projectFullData, dynamicConfig, mod]);


    useEffect(() => {
        const canvas = canvasRef.current;
        if(canvas == null) return;
        const ctx = canvas.getContext('2d');
        if(!ctx) return;
        
        ctx.clearRect(0, 0, staticConfig.canvas.width, staticConfig.canvas.height);
        drawCanvas(ctx);
        gof.draw(ctx);
    }, [gof]);

    function convertEvent(e : 
        React.MouseEvent<HTMLCanvasElement, MouseEvent> |
        React.DragEvent<HTMLCanvasElement>
    ) : void{
        const canvas = e.currentTarget;
        const rect = canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        e.clientX = x;
        e.clientY = y;
    }

    return (
        <div>
            {projectGraph && projectFullData && gof && <>
                <canvas ref={canvasRef}
                    width={staticConfig.canvas.width} 
                    height={staticConfig.canvas.height}
                    onClick={(e) => {
                        convertEvent(e);
                        gof.handleClick(e, mod);
                    }}    
                onMouseDown={e => {
                    convertEvent(e);
                    gof.handleMouseDown(e, mod);
                }}
                onMouseMove={(e) => {
                    convertEvent(e)
                    gof.handleMouseMove(e, mod);
                }}
                onMouseUp={e => {
                    convertEvent(e)
                    gof.handleMouseUp(e, mod);
                }}

                />
                <div>
                    <h5>Mod : {PanelMods[gof.getMod()]}</h5>
                    <h5>Also Mod : {PanelMods[mod]}</h5>
                    <button onClick={e => setMod(PanelMods.CURSOR)}>Cursor</button>
                    <button onClick={e => setMod(PanelMods.CONNECT)}>Connect</button>
                    <button onClick={e => setMod(PanelMods.DELETE)}>Delete</button>
                    
                    <div>
                        <label>Name:</label>
                        <input className="m-2" value={newJobNodeDetails.name} onChange={e => setNewJobNodeDetails(
                            {
                                ...newJobNodeDetails,
                                name : e.target.value
                            }
                        )}/>
                        <br/>
                        <label>x: < input type="number" className="m-2" value={newJobVerticeDetails.x} onChange={e => {
                            try{
                                setNewJobVerticeDetails({...newJobVerticeDetails, x: parseInt(e.target.value) })
                            }catch(e){}
                        }}/></label>
                        <br/>
                        <label>y: < input type="number" className="m-2" value={newJobVerticeDetails.y} onChange={e => {
                            try{
                                setNewJobVerticeDetails({...newJobVerticeDetails, y: parseInt(e.target.value) })
                            }catch(e){};
                            
                        }}/></label>
                        <br/>

                        <button className="btn btn-success" onClick={e => 
                            createJobNode(projectFullData.id, newJobNodeDetails)
                                .then(response => {
                                    return updateProjectGraph(projectFullData.id).then(response2 => {
                                        return updateJobNodeVertice(projectFullData.id, response.data, newJobVerticeDetails);
                                    });
                                }).then(response => {
                                    refresh();
                                })
                                .catch(e => console.log(e))
                        }>Add JobNode</button>
                    </div>

                    <hr/>
                    {mod == PanelMods.CONNECT && <div>
                        <h3>Channel creation panel:</h3>
                        <label>Channel name : <input value={newChannelDetails.name} onChange={e => 
                            setNewChannelDetails({...newChannelDetails, name : e.target.value})}/>
                        </label>
                        <br/>
                        <select value={newChannelDetails.type}>
                            {Object.values(ChannelTypes).map(key => <option value={key}>{key}</option>)}
                        </select>

                        <label>Headers: </label>
                        <input value={newHeader} onChange={e => setNewHeader(e.target.value)}/>
                        <button className="btn btn-primary" onClick={e => {
                            let newHeaders = Array.from(newChannelDetails.headers);
                            newHeaders.push(newHeader);

                            setNewChannelDetails({
                                ...newChannelDetails,
                                headers: newHeaders
                            })
                        }}>Add header</button>

                        {newChannelDetails.headers.map(header => <div key={header}>{header} 
                            <button className="btn btn-danger" onClick={e => setNewChannelDetails({
                                ...newChannelDetails,
                                headers: newChannelDetails.headers.filter(h => h != header)
                            })}>X</button>
                        </div>)}

                    </div>}
                </div>

                <hr/>
                {menu}
            </>}
        </div>
    )
}

export default ProjectGraphComponent;