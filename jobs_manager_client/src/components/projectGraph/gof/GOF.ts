import { connect } from "../../../api/abstraction/channelApi";
import { createJobNode, deleteJobNode } from "../../../api/abstraction/jobNodeApi";
import { ChannelDetails } from "../../../api/abstraction/projectApi";
import {  updateJobNodeVertice, updateProjectGraph } from "../../../api/ui/projectGraphApi";
import { NotificationType } from "../../notifications/Notificator";
import { ProjectGraphComponentContext } from "../ProjectGraph";
import { PanelMods } from "./eventHandlers/PanelMods";
import { GraphElement } from "./GraphElement";
import { JobNodeElement } from "./JobNodeElement";
import { NullGraphElement } from "./NullGraphElement";
import { PlugBarElement } from "./PlugBarElement";
import { PlugElement } from "./PlugElement";
import * as Yup from 'yup';

export interface StaticCanvasConfig{
    width : number,
    height : number,
    padding : {
        x : number,
        y : number
    }
}


export interface DynamicCanvasConfig{
    offset : {
        x : number,
        y : number
    },
    elemOffset : {
        [key: string] : {x : number, y : number}    //where key should be gofId
    }
    dragData : {
        start : {
            x : number,
            y : number
        } | null,
        elem : GraphElement,
        isDragging : boolean
    },
    connectMod : {
        input : PlugElement | null,
        output : PlugElement | null
    }
}

export type DraggableGraphElement = JobNodeElement;

export class GOF{


    private children : GraphElement[] = [];

    private context : ProjectGraphComponentContext;

    public constructor(
        projectGraphComponentContext : ProjectGraphComponentContext

    ){

        this.context = projectGraphComponentContext;

    }

    public getContext() : ProjectGraphComponentContext{
        return this.context;
    }

    

    public getOffsets() : [number, number]{
        return [
            this.context.dynamic.offset.x,
            this.context.dynamic.offset.y 
        ]
    }

    public findById(id : string) : GraphElement {
        return this.findByIdRecursively(id, this.children);
    }

    private findByIdRecursively(id : string, elements : GraphElement[]) : GraphElement{
        
        for(let elem of elements){
            if(elem.isNull()) continue;
            
            if(elem.getGofId() == id) return elem;


            let child = this.findByIdRecursively(id, elem.getChildren());
            if(child.getGofId() == id) return child;
        }   
        
        return new NullGraphElement();
    }

    
    public handleMouseDown = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        if(this.context.mod != PanelMods.CURSOR) return;

        const [dx, dy] = this.getOffsets();
        const [x, y] = [event.clientX - dx, event.clientY - dy];

        let target = this.findClickTarget(x, y);
        
        this.context.setDynamic({
            ...this.context.dynamic,
            dragData: {
                isDragging : true,
                start : {
                    x : event.clientX,
                    y : event.clientY
                },
                elem : target
            }
        })

    }

    public handleMouseMove  = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        if(this.context.mod != PanelMods.CURSOR) return;

        if(!this.context.dynamic.dragData.isDragging) return;

        if(!this.context.dynamic.dragData.start) return;

        if(this.context.dynamic.dragData.elem.isNull()){
            this.context.setDynamic({
                ...this.context.dynamic,
                dragData : {
                    ...this.context.dynamic.dragData,
                    start :{
                        x : event.clientX,
                        y : event.clientY
                    }

                },
                offset: {
                    x : this.context.dynamic.offset.x + event.clientX - this.context.dynamic.dragData.start.x,
                    y:  this.context.dynamic.offset.y + event.clientY - this.context.dynamic.dragData.start.y
                }   
            });
        }else{
            let id = this.context.dynamic.dragData.elem.getGofId();
            let elemOffset = this.context.dynamic.elemOffset[id] ?? {x : 0, y : 0}
            this.context.setDynamic({
                ...this.context.dynamic,
                dragData : {
                    ...this.context.dynamic.dragData,
                    start :{
                        x : event.clientX,
                        y : event.clientY
                    }

                },
                elemOffset: {
                    ...this.context.dynamic.elemOffset,
                    [this.context.dynamic.dragData.elem.getGofId()] : {
                        x : elemOffset.x + event.clientX - this.context.dynamic.dragData.start.x,
                        y : elemOffset.y + event.clientY - this.context.dynamic.dragData.start.y,
                    }
                }
            })
        }
        

    }

    public handleMouseUp = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        if(this.context.mod != PanelMods.CURSOR) return;
        this.context.setDynamic({
            ...this.context.dynamic,
            dragData: {
                ...this.context.dynamic.dragData,
                isDragging : false
            }
        })
    }
    
    
    private clickHandlers : Map<PanelMods, (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void> = 
        new Map<PanelMods, (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void> ([
            [PanelMods.CURSOR, (event) => {
                const [dx, dy] = this.getOffsets();

                let elem = this.findClickTarget(event.clientX - dx, event.clientY - dy);
                if(elem.isNull()) return;
                
                this.context.setMenuSource(elem);

            }],
            [PanelMods.DELETE, (event) => {
                const [dx, dy] = this.getOffsets();

                let elem = this.findClickTarget(event.clientX - dx, event.clientY - dy);
                elem.deleteElement()
                    ?.then(r => this.context.refresh())
                    .catch(this.context.catchRequestError)
                ;
            }],
            [PanelMods.CONNECT, (event) => {
                const [dx, dy] = this.getOffsets();

                let elem = this.findClickTarget(event.clientX - dx, event.clientY - dy);
                if(!(elem instanceof PlugElement)) return;

                let plug = elem as PlugElement;

                let rightOrientation = (plug.getParent() as PlugBarElement).getOrientation();
                let isOfProject = (plug.getParent().getParent().isNull());

                let newConnectMod = {...this.context.dynamic.connectMod};

                if(rightOrientation && isOfProject || (!rightOrientation && !isOfProject)){
                    if(newConnectMod.output != plug){
                        newConnectMod.output = plug;
                    }else{
                        newConnectMod.output = null;
                    }
                }else{
                    if(newConnectMod.input != plug){
                        newConnectMod.input = plug;
                    }else{
                        newConnectMod.input = null;
                    }
                }

                

                if(newConnectMod.input && newConnectMod.output){
                    let isLeftOfProject = newConnectMod.input.getParent().getParent().isNull();
                    let isRightOfProject = newConnectMod.output.getParent().getParent().isNull();

                    if(isLeftOfProject && isRightOfProject) return;

                    let parameters : string[] = [];
                    //input parameters
                    if(isLeftOfProject){
                        parameters.push(`project_input_label=${newConnectMod.input.getLabel()}`);
                    }else{
                        parameters.push(`output_label=${newConnectMod.input.getLabel()}`);
                        let jobId = (newConnectMod.input.getParent().getParent() as JobNodeElement).getData().id;
                        parameters.push(`output_job_node_id=${jobId}`);
                    }

                    if(isRightOfProject){
                        parameters.push(`project_output_label=${newConnectMod.output.getLabel()}`);
                    }else{
                        parameters.push(`input_label=${newConnectMod.output.getLabel()}`);
                        let jobId = (newConnectMod.output.getParent().getParent() as JobNodeElement).getData().id;
                        parameters.push(`input_job_node_id=${jobId}`);
                    }

                    let requestBody : ChannelDetails = this.context.newChannelDetails;

                    if(!isLeftOfProject && !isRightOfProject){
                        if(
                            !this.context.newChannelDetails.name ||
                            this.context.newChannelDetails.name.length < 3 ||
                            this.context.newChannelDetails.name.length > 20
                        ){
                            this.context.pushNotification({
                                message: "Exception: Channel name is invalid",
                                type : NotificationType.ERROR,
                                time: 5
                            })

                            return;
                        }

                        if(this.context.newChannelDetails.headers.length < 1 
                            || this.context.newChannelDetails.headers.length > 20
                        ){
                            this.context.pushNotification({
                                message: "Exception: Invalid headers size",
                                type : NotificationType.ERROR,
                                time : 5
                            })
                            return;
                        }
                    }else{
                        if(isLeftOfProject){
                            requestBody = this.context.projectData.inputChannels[newConnectMod.input.getLabel()].channelDetails
                        }else{
                            requestBody = this.context.projectData.outputChannels[newConnectMod.output.getLabel()].channelDetails
                        }
                    }
                  
                    connect(this.context.projectData.id, parameters, requestBody)
                        .then(r => this.context.refresh())
                        .then(r => {
                            this.context.setDynamic({
                                ...this.context.dynamic,
                                connectMod: {
                                    ...this.context.dynamic.connectMod,
                                    input : null,
                                    output : null
                                }
                            });
                        }).catch(this.context.catchRequestError);

                }else{
                    this.context.setDynamic({
                        ...this.context.dynamic, 
                        connectMod : {
                            ...this.context.dynamic.connectMod, 
                            input : newConnectMod.input, 
                            output : newConnectMod.output
                        }
                    });
                }


            }],
            [PanelMods.JOB_NODE, (event) => {

                this.context.jobNodeDetailsValidation.validate({...this.context.newJobNodeDetails})
                    .then(() => {
                        const [dx, dy] = this.getOffsets();

                        createJobNode(this.context.projectData.id, this.context.newJobNodeDetails)
                            .then(response => {
                                updateProjectGraph(this.context.projectData.id).then(response2 => {
                                    updateJobNodeVertice(this.context.projectData.id, response.data, {
                                        x : event.clientX - dx, 
                                        y : event.clientY - dy
                                    }).then(response => {
                                        this.context.refresh();
                                        this.context.pushNotification({
                                            message: "Job Node was created successfully",
                                            time: 5,
                                            type: NotificationType.INFO
                                        })
                                    }).catch(e => {

                                        deleteJobNode(this.context.projectData.id, response.data)
                                            .then(r => {
                                                return updateProjectGraph(this.context.projectData.id)
                                            }).then(r => {
                                                this.context.pushNotification({
                                                    message: "Error while updating job node vertice: the jobnode was deleted",
                                                    time: 5,
                                                    type: NotificationType.ERROR
                                                });
                                            }).catch(e =>{
                                                this.context.pushNotification({
                                                    message: "Error while updating job node vertice: impossible to delete jobnode",
                                                    time: 5,
                                                    type: NotificationType.ERROR
                                                })
                                            });

                                        this.context.catchRequestError(e);
                                    });
                                }).catch(e => {
                               
                                    deleteJobNode(this.context.projectData.id, response.data)
                                        .then(r => {
                                            this.context.pushNotification({
                                                message: "Error while updating job node vertice: the jobnode was deleted",
                                                time: 5,
                                                type: NotificationType.ERROR
                                            });
                                        }).catch(e =>{
                                            this.context.pushNotification({
                                                message: "Error while updating job node vertice: impossible to delete jobnode",
                                                time: 5,
                                                type: NotificationType.ERROR
                                            })
                                        });

                                    this.context.catchRequestError(e);
                                });
                            }).catch( this.context.catchRequestError);
                    }).catch(e => {
                        if(e instanceof Yup.ValidationError){
                            this.context.pushNotification({
                                message: (e as Yup.ValidationError).message,
                                time: 5,
                                type: NotificationType.ERROR
                            })
                        }else{
                            this.context.pushNotification({
                                message: "Unexpected validation Error",
                                time: 5,
                                type: NotificationType.ERROR
                            })
                        }
                    });

               
            }]
        ]);
    

    public handleClick = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        let handler = this.clickHandlers.get(mod);
        if(!handler) return;

        handler(event);
    }


    private findClickTarget(x : number, y : number) : GraphElement{
        return this.findClickTargetRecursive(x, y, this.children)
    }

    private findClickTargetRecursive(x : number, y : number, children : GraphElement[]) : GraphElement{
    
        let result : GraphElement = new NullGraphElement();
        for(let elem of children){
            if(elem.doesContainPoint(x,y)){
                result = elem;
                break;
            }
        }
        if(result.isNull()) return result;

        let deeperResult = this.findClickTargetRecursive(x, y, result.getChildren());
        if(deeperResult.isNull()) return result;
        else return deeperResult;
    }



    public addElement(element : GraphElement) : void{

        this.children.push(element);
    }


    public draw(ctx : CanvasRenderingContext2D) : void {
        for(let elem of this.children){
            elem.draw(ctx);
        }
    }


}