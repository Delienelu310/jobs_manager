import { useEffect, useRef, useState } from "react";
import { ProjectGraph } from "../../api/ui/projectGraphApi";
import { ChannelDetails, ChannelTypes, JobNodeDetails, ProjectFullData } from "../../api/abstraction/projectApi";

import { JobNodeElement, StaticJobNodeElementConfig } from "./gof/JobNodeElement";
import { StaticPlugBarConfig, PlugBarElement } from "./gof/PlugBarElement";
import { DynamicCanvasConfig, GOF, StaticCanvasConfig } from "./gof/GOF";
import { NullGraphElement } from "./gof/NullGraphElement";
import { ChannelElement, StaticChannelConfig } from "./gof/ChannelElement";
import { PanelMods } from "./gof/eventHandlers/PanelMods";
import SecuredNode from "../../authentication/SecuredNode";
import { ProjectPrivilege } from "../../api/authorization/privilegesApi";

import * as Yup from 'yup';
import { NotificationConfig, NotificationType, useNotificator } from "../notifications/Notificator";
import { AxiosError } from "axios";
import { GraphElement } from "./gof/GraphElement";

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


export interface ProjectGraphComponentContext{
    readonly projectData : ProjectFullData;
    readonly projectGraph : ProjectGraph;
    readonly config : StaticCanvasConfig;
    readonly dynamic : DynamicCanvasConfig;


    readonly setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>;
    // readonly setMenu : React.Dispatch<React.SetStateAction<JSX.Element>>;
    readonly setMenuSource : React.Dispatch<React.SetStateAction<GraphElement | null>>;
    readonly mod : PanelMods;
    readonly refresh : () => void


    readonly newChannelDetails : ChannelDetails;
    readonly connectValidationSchema : Yup.AnyObjectSchema;
    readonly newJobNodeDetails : JobNodeDetails;
    readonly jobNodeDetailsValidation : Yup.AnyObjectSchema;

    readonly pushNotification : (config : NotificationConfig) => void;
    readonly catchRequestError: (e : AxiosError<string>) => void;
}

const ProjectGraphComponent = ({projectFullData, projectGraph, staticConfig, setProjectGraph, refresh} : ProjectGraphComponent) => {


    const {pushNotification, catchRequestError} = useNotificator();

    const canvasRef = useRef<HTMLCanvasElement>(null);
    const [mod, setMod] = useState<PanelMods>(PanelMods.CURSOR);

    // const [menu, setMenu] = useState<JSX.Element>(<div>Choose Element...</div>);
    const [menuSource, setMenuSource] = useState< GraphElement | null>(null);


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

    interface ChannelDetailsErrors{
        name : string | null,
        newHeader : string | null
    }
    const connectValidationSchema = Yup.object({
        name : Yup.string()
            .required()
            .min(3)
            .max(20),
        newHeader : Yup.string()
            .test("test-new-header", "Header is not valid", (value) => {
                if(!value) return false;
                if(!value.trim()) return false;

                value = value.trim();
                if(value.length < 1 || value.length > 20) return false;

                if((value.charAt(0) < 'a' || value.charAt(0) > 'z') && (value.charAt(0) < 'A' || value.charAt(0) > "Z")) return false;

                return true;

            })
    });

    const [newChannelDetailsErorrs, setNewChannelDetailsErrors] = useState<ChannelDetailsErrors>({
        name : null,
        newHeader : null
    });



    const [newJobNodeDetails, setNewJobNodeDetails] = useState<JobNodeDetails>({
        name : "",
        description: null
    });

    interface JobNodeDetailsErrors{
        name : string | null,
        description : string | null
    }

    const [newJobNodeDetailsErrors, setNewJobNodeDetailsErrors] = useState<JobNodeDetailsErrors>({
        name : null,
        description : null
    });

    const jobNodeDetailsSchema = Yup.object({
        name : Yup.string()
            .min(3, "Job Node Name length must be 3 to 50")
            .max(50, "Job Node Name length must be 3 to 50")
            .nonNullable("Job Node Name must not be blank")
            .required("Job Node Name must be specified"),
        description: Yup.string()
            .notRequired()
            .min(3, "Job Node Description length must be 3 to 500")
            .max(500, "Job Node Description length must be 3 to 500")
    });



    
    const [gof, setGof] = useState<GOF>(new GOF({
    
        config: staticConfig.canvas,
        projectData: projectFullData,
        projectGraph,
        dynamic: dynamicConfig,
        setDynamic: setDynamicConfig,
        setMenuSource,
        mod,
        refresh,
        newChannelDetails,
        connectValidationSchema,
        newJobNodeDetails,
        jobNodeDetailsValidation: jobNodeDetailsSchema,
        pushNotification,
        catchRequestError
    }));

    function prepareGof(){

     

        setGof(oldGof => {

            let newGof : GOF = new GOF({
                config: staticConfig.canvas,
                projectData: projectFullData,
                projectGraph,
                dynamic: dynamicConfig,
                setDynamic: setDynamicConfig,
                setMenuSource,
                mod,
                refresh,
                newChannelDetails,
                connectValidationSchema,
                newJobNodeDetails,
                jobNodeDetailsValidation: jobNodeDetailsSchema,
                pushNotification,
                catchRequestError
            });
    
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

            //reset menu

            setMenuSource(oldMenu => {
                if(oldMenu == null) return null;

                return newGof.findById(oldMenu.getGofId());
            })

            return newGof;
        });
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

    }, [projectGraph, projectFullData, dynamicConfig, mod, newChannelDetails, newJobNodeDetails]);


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
                    <h5>Mod : {PanelMods[gof.getContext().mod]}</h5>
                    <h5>Also Mod : {PanelMods[mod]}</h5>
                    <button onClick={e => setMod(PanelMods.CURSOR)}>Cursor</button>
                    <SecuredNode
                        roles={null}
                        jobNodePrivilegeConfig={null}
                        moderator
                        projectPrivilegeConfig={{
                            project: projectFullData,
                            privileges : [ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT, ProjectPrivilege.MODERATOR]
                        }}
                        alternative={<>
                            <button disabled>Connect</button>
                            <button disabled>Delete</button>
                            <button disabled>Job Node</button>
                        </>}
                    >
                        <button onClick={e => setMod(PanelMods.CONNECT)}>Connect</button>
                        <button onClick={e => setMod(PanelMods.DELETE)}>Delete</button>
                        <button onClick={e => setMod(PanelMods.JOB_NODE)}>Job Node</button>
                    </SecuredNode>
                   
                    
                    {mod == PanelMods.JOB_NODE && <div>
                        <SecuredNode
                             roles={null}
                             jobNodePrivilegeConfig={null}
                             alternative={null}
                             moderator
                             projectPrivilegeConfig={{
                                 project: projectFullData,
                                 privileges : [ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT, ProjectPrivilege.MODERATOR]
                             }}
                        >   
                            <div style={{
                                margin: "20px 20%"
                            }}>
                            
                                <h5>Job Node Panel</h5>
                                <div className="m-2">
                                    <strong>Name:</strong>
                                    <input className=" form-control m-2" value={newJobNodeDetails.name} onChange={async e => {
                                        setNewJobNodeDetails({
                                            ...newJobNodeDetails,
                                            name : e.target.value
                                        })
                                        try {
                                            // Validate the specific field using Yup.reach
                                            await jobNodeDetailsSchema.validateAt("name", {name : e.target.value});
                                    
                                            setNewJobNodeDetailsErrors((prevErrors) => ({
                                            ...prevErrors,
                                                name: null,
                                            }));
                                        }catch (error) {

                                            if(error instanceof Yup.ValidationError){
                                                setNewJobNodeDetailsErrors((prevErrors) => ({
                                                    ...prevErrors,
                                                    name: (error as Yup.ValidationError).message,
                                                }));
                                            }else{
                                                pushNotification({
                                                    message : "Unexpected validation Error",
                                                    type: NotificationType.ERROR,
                                                    time: 5
                                                })
                                            }
                                            
                                        }
                                    
                                        
                                    }}/>
                                    {newJobNodeDetailsErrors.name && <div className="text-danger m-2">{newJobNodeDetailsErrors.name}</div>}
                                </div>

                                <div className="m-2">
                                    <strong>Description:</strong>
                                    <textarea className=" form-control m-2" value={newJobNodeDetails.description || ''} onChange={async e => {
                                        setNewJobNodeDetails(
                                        {
                                            ...newJobNodeDetails,
                                            description : e.target.value || null
                                        })
                                        
                                        try {
                                            // Validate the specific field using Yup.reach
                                            await jobNodeDetailsSchema.validateAt("description", {description : e.target.value || undefined});
                                    
                                            setNewJobNodeDetailsErrors((prevErrors) => ({
                                                ...prevErrors,
                                                description: null,
                                            }));
                                        }catch (error) {

                                            if(error instanceof Yup.ValidationError){
                                                setNewJobNodeDetailsErrors((prevErrors) => ({
                                                    ...prevErrors,
                                                    description: (error as Yup.ValidationError).message,
                                                }));
                                            }else{
                                                pushNotification({
                                                    message : "Unexpected validation Error",
                                                    type: NotificationType.ERROR,
                                                    time: 5
                                                })
                                            }
                                            
                                        }
                                    
                                        
                                        
                                    }}/>
                                    {newJobNodeDetailsErrors.description && <div className="text-danger m-2">{newJobNodeDetailsErrors.description}</div>}
                                </div>
                                
                            
                            </div>
                            
                        </SecuredNode>
                       
                    </div>}

                    <hr/>
                    {mod == PanelMods.CONNECT && <div>
                        <SecuredNode
                            roles={null}
                            jobNodePrivilegeConfig={null}
                            alternative={null}
                            moderator
                            projectPrivilegeConfig={{
                                project: projectFullData,
                                privileges : [ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT, ProjectPrivilege.MODERATOR]
                            }}
                        >
                            <div style={{
                                margin: "20px 20%"
                            }}>
                                <h3>Channel creation panel:</h3>

                                <div className="m-2">
                                    <strong>Channel name: </strong>
                                    <input className="form-control m-2" value={newChannelDetails.name} 
                                        onChange={e => {
                                            setNewChannelDetails({...newChannelDetails, name : e.target.value})
                                            connectValidationSchema.validateAt("name", {name : e.target.value})
                                                .then(r => setNewChannelDetailsErrors({...newChannelDetailsErorrs, name : null}))
                                                .catch(e => {
                                                    if(e instanceof Yup.ValidationError){
                                                        setNewChannelDetailsErrors({...newChannelDetailsErorrs, name : (e as Yup.ValidationError).message});
                                                    }else{
                                                        pushNotification({
                                                            message: "Unexpected error when validating the channel name",
                                                            type: NotificationType.ERROR,
                                                            time: 5
                                                        });
                                                    }
                                                })
                                            
                                        }}
                                    />
                                    {newChannelDetailsErorrs.name && <div className="text-danger">{newChannelDetailsErorrs.name}</div>}
                                </div>

                                
                               
                                <strong>Channel type:</strong>
                                <select className="form-control m-2" value={newChannelDetails.type}>
                                    {Object.values(ChannelTypes).map(key => <option value={key}>{key}</option>)}
                                </select>

                                <h5>Headers: </h5>
                                {newChannelDetails.headers.map((header, index) => 
                                    <div key={index + "_" + header} style={{borderBottom: "1px solid black", margin: "10px 10%", height: "40px"}}>
                                        <span style={{fontSize: "20px"}}>{header} </span>

                                        <button style={{float: "right"}} className="btn btn-primary" onClick={e => {
                                            
                                            if(index == 0) return;
                                            const newHeaders = [...newChannelDetails.headers];
                                            const previous = newHeaders[index - 1];
                                            newHeaders[index - 1] = newHeaders[index];
                                            newHeaders[index] = previous

                                            setNewChannelDetails({...newChannelDetails, headers : newHeaders})
                                        }}>Up</button>

                                        <button style={{float: "right"}} className="btn btn-primary" onClick={e => {
                                            if(index == newChannelDetails.headers.length - 1) return;
                                            const newHeaders = [...newChannelDetails.headers];
                                            const next = newHeaders[index + 1];
                                            newHeaders[index + 1] = newHeaders[index];
                                            newHeaders[index] = next

                                            setNewChannelDetails({...newChannelDetails, headers : newHeaders})
                                        }}>Down</button>



                                        <button style={{float: "right"}}className="btn btn-danger" onClick={e => {

                                            const newHeaders = Array.from(newChannelDetails.headers);

                                            newHeaders.splice(index, 1);

                                            setNewChannelDetails({
                                                ...newChannelDetails,
                                                headers: newHeaders
                                            })
                                        }}>X</button>
                                    </div>
                                )}

                                
                                <strong>Add new header:</strong>
                                <input className="form-control m-2" value={newHeader} onChange={e => {
                                    setNewHeader(e.target.value)
                                    connectValidationSchema.validateAt("newHeader", {newHeader : e.target.value})
                                        .then(r => setNewChannelDetailsErrors({...newChannelDetailsErorrs, newHeader : null}))
                                        .catch(e => {
                                            if(e instanceof Yup.ValidationError){
                                                setNewChannelDetailsErrors({...newChannelDetailsErorrs, newHeader : (e as Yup.ValidationError).message});
                                            }else{
                                                pushNotification({
                                                    message: "Unexpected error when validating the channel new header",
                                                    type: NotificationType.ERROR,
                                                    time: 5
                                                });
                                            }
                                        })

                                }}/>
                                {newChannelDetailsErorrs.newHeader && <div className="text-danger">{newChannelDetailsErorrs.newHeader}</div>}
                                
                                <button className="btn btn-primary" onClick={e => {

                                    connectValidationSchema.validateAt("newHeader", {newHeader : newHeader})
                                        .then(r => {
                                            let newHeaders = Array.from(newChannelDetails.headers);
                                            newHeaders.push(newHeader);

                                            setNewChannelDetails({
                                                ...newChannelDetails,
                                                headers: newHeaders
                                            })
                                        }).catch(e => {
                                            if(e instanceof Yup.ValidationError){
                                            
                                                pushNotification({
                                                    message:  (e as Yup.ValidationError).message,
                                                    type: NotificationType.ERROR,
                                                    time: 5
                                                });
                                            }else{
                                                pushNotification({
                                                    message: "Unexpected error when validating the channel new header",
                                                    type: NotificationType.ERROR,
                                                    time: 5
                                                });
                                            }
                                        })

                                   
                                }}>Add header</button>

                               

                            </div>
                   
                        </SecuredNode>
                      
                    </div>}
                </div>

                <hr/>
                <button className="btn btn-danger m-3" onClick={() => setMenuSource(null)}>Close menu</button>
                {menuSource == null ? <div>Choose Element...</div> :  menuSource.getMenuComponent()}
            </>}
        </div>
    )
}

export default ProjectGraphComponent;