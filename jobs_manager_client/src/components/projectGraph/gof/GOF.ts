import { ProjectFullData } from "../../../api/abstraction/projectApi";
import { ProjectGraph } from "../../../api/ui/projectGraphApi";
import { PanelMods } from "./eventHandlers/PanelMods";
import { GraphElement } from "./GraphElement";
import { JobNodeElement } from "./JobNodeElement";
import { NullGraphElement } from "./NullGraphElement";
import { StaticPlugBarConfig } from "./PlugBarElement";


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
    private setProjectGraph : React.Dispatch<React.SetStateAction<ProjectGraph | undefined>>;
    private setMenu : React.Dispatch<React.SetStateAction<JSX.Element>>;
    private mod : PanelMods = PanelMods.CURSOR;

    

    public constructor(
        config : StaticCanvasConfig, 
        projectData : ProjectFullData, 
        projectGraph : ProjectGraph,
        dynamic : DynamicCanvasConfig,
        setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>,
        setProjectGraph : React.Dispatch<React.SetStateAction<ProjectGraph | undefined>>,
        setMenu : React.Dispatch<React.SetStateAction<JSX.Element>>
    ){
        this.config = config;
        this.projectData = projectData;
        this.projectGraph = projectGraph;
        this.dynamic = dynamic;
        this.setDynamic = setDynamic;
        this.setProjectGraph = setProjectGraph;
        this.setMenu = setMenu;
    }

    public getProjectGraph() : ProjectGraph{
        return this.projectGraph;
    }

    public getMod() : PanelMods{
        return this.mod;
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

        console.log(event.clientX + " " + event.clientY);
    }

    public handleMouseMove  = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {

    

        if(!this.dynamic.dragData.isDragging) return;
        console.log("a");
        if(!this.dynamic.dragData.start) return;
        console.log("b");
        console.log(this.dynamic.dragData.elem);

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
            console.log("dragging jobnode");
            this.dynamic.dragData.elem.getEventHandler().handleMouseMove(event, mod);
        }
        

    }

    public handleMouseUp = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => {
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

                let elem = this.findClickTarget(event.clientX, event.clientY);
                if(elem.isNull()) return;
                
                this.setMenu(elem.getMenuComponent());

            }],
            [PanelMods.DELETE, (event) => {}],
            [PanelMods.CONNECT, (event) => {

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