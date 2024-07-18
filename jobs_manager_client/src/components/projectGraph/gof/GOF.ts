import { connect } from "../../../api/abstraction/channelApi";
import { ChannelDetails, ChannelTypes, ProjectFullData } from "../../../api/abstraction/projectApi";
import { ProjectGraph } from "../../../api/ui/projectGraphApi";
import { PanelMods } from "./eventHandlers/PanelMods";
import { GraphElement } from "./GraphElement";
import { JobNodeElement } from "./JobNodeElement";
import { NullGraphElement } from "./NullGraphElement";
import { PlugBarElement, StaticPlugBarConfig } from "./PlugBarElement";
import { PlugElement } from "./PlugElement";


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
    private projectData : ProjectFullData;
    private projectGraph : ProjectGraph;
    private config : StaticCanvasConfig;

    private dynamic : DynamicCanvasConfig;
    private setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>;
    private setMenu : React.Dispatch<React.SetStateAction<JSX.Element>>;
    private mod : PanelMods;
    private refresh : () => void;
    private newChannelDetails : ChannelDetails;
    

    public constructor(
        config : StaticCanvasConfig, 
        projectData : ProjectFullData, 
        projectGraph : ProjectGraph,
        dynamic : DynamicCanvasConfig,
        setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>,
        setMenu : React.Dispatch<React.SetStateAction<JSX.Element>>,
        mod : PanelMods,
        refresh : () => void,
        newChannelDetails : ChannelDetails
    ){
        this.config = config;
        this.projectData = projectData;
        this.projectGraph = projectGraph;
        this.dynamic = dynamic;
        this.setDynamic = setDynamic;
        this.setMenu = setMenu;
        this.mod = mod;
        this.refresh = refresh;
        this.newChannelDetails = newChannelDetails;

    }

    public getNewChannelDetails() : ChannelDetails{
        return this.newChannelDetails;
    }

    public getProjectGraph() : ProjectGraph{
        return this.projectGraph;
    }

    public getMod() : PanelMods{
        return this.mod;
    }

    public getRefresh() : () => void {
        return this.refresh;
    }

    public setMod(mod : PanelMods) : void{
        this.mod = mod;
    }

    public getProjectData() : ProjectFullData{
        return this.projectData;
    }
    public getCanvasConfig() : StaticCanvasConfig{
        return this.config;
    }

    public getOffsets() : [number, number]{
        return [
            this.dynamic.offset.x,
            this.dynamic.offset.y 
        ]
    }

    public getDynamic() : DynamicCanvasConfig{
        return this.dynamic;
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

        if(this.mod != PanelMods.CURSOR) return;

        let target = this.findClickTarget(event.clientX, event.clientY);
        
        this.setDynamic({
            ...this.dynamic,
            dragData: {
                isDragging : true,
                start : {
                    x : event.clientX,
                    y : event.clientY
                },
                elem : target
            }
        })

        // console.log(event.clientX + " " + event.clientY);
    }

    public handleMouseMove  = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        if(this.mod != PanelMods.CURSOR) return;

        if(!this.dynamic.dragData.isDragging) return;

        if(!this.dynamic.dragData.start) return;



        if(this.dynamic.dragData.elem.isNull()){
            this.setDynamic({
                ...this.dynamic,
                dragData : {
                    ...this.dynamic.dragData,
                    start :{
                        x : event.clientX,
                        y : event.clientY
                    }

                },
                offset: {
                    x : this.dynamic.offset.x + event.clientX - this.dynamic.dragData.start.x,
                    y:  this.dynamic.offset.y + event.clientY - this.dynamic.dragData.start.y
                }   
            });
        }else{
            // console.log("dragging jobnode");
            this.dynamic.dragData.elem.getEventHandler().handleMouseMove(event, mod);
        }
        

    }

    public handleMouseUp = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

        if(this.mod != PanelMods.CURSOR) return;
        this.setDynamic({
            ...this.dynamic,
            dragData: {
                ...this.dynamic.dragData,
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
                
                this.setMenu(elem.getMenuComponent());

            }],
            [PanelMods.DELETE, (event) => {
                const [dx, dy] = this.getOffsets();

                let elem = this.findClickTarget(event.clientX - dx, event.clientY - dy);
                elem.deleteElement()
                    ?.then(r => this.refresh())
                    .catch(e => console.log(e))
                ;
            }],
            [PanelMods.CONNECT, (event) => {
                const [dx, dy] = this.getOffsets();

                let elem = this.findClickTarget(event.clientX - dx, event.clientY - dy);
                if(!(elem instanceof PlugElement)) return;

                let plug = elem as PlugElement;

                let rightOrientation = (plug.getParent() as PlugBarElement).getOrientation();
                let isOfProject = (plug.getParent().getParent().isNull());

                let newConnectMod = {...this.dynamic.connectMod};

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

                    connect(this.getProjectData().id, parameters, this.newChannelDetails)
                        .then(r => this.refresh())
                        .then(r => {
                            this.setDynamic({
                                ...this.dynamic,
                                connectMod: {
                                    ...this.dynamic.connectMod,
                                    input : null,
                                    output : null
                                }
                            });
                        }).catch(e => console.log(e));

                }else{
                    this.setDynamic({...this.dynamic, connectMod : {...this.dynamic.connectMod, input : newConnectMod.input, output : newConnectMod.output}});
                }


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