import { ProjectFullData } from "../../../api/abstraction/projectApi";
import { PanelMods } from "./eventHandlers/PanelMods";
import { GraphElement } from "./GraphElement";
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
    }
}

export class GOF{


    private children : GraphElement[] = [];
    private projectData : ProjectFullData;
    private config : StaticCanvasConfig;

    private dynamic : DynamicCanvasConfig;
    private dynamicConfigSetter : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>;

    private mod : PanelMods = PanelMods.CURSOR;

    

    public constructor(
        config : StaticCanvasConfig, 
        projectData : ProjectFullData, 
        dynamic : DynamicCanvasConfig,
        dynamicConfigSetter : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>
    ){
        this.config = config;
        this.projectData = projectData;
        this.dynamic = dynamic;
        this.dynamicConfigSetter = dynamicConfigSetter;
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

    private dragTarget : GraphElement = new NullGraphElement();

    public handleDragStart(){

    }

    public handleDragEnd(){

    }

    public handleDrag(){
        if(this.dragTarget.isNull()){
            
        }
    }

    private clickHandlers : Map<PanelMods, (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void> = 
        new Map<PanelMods, (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void> ([
            [PanelMods.CURSOR, (event) => {
                console.log("something");
            }],
            [PanelMods.DELETE, (event) => {}],
            [PanelMods.CONNECT, (event) => {

            }]
        ]);

    public handleClick : React.MouseEventHandler<HTMLCanvasElement> = (event : React.MouseEvent<HTMLCanvasElement, MouseEvent>) => {

        let elem = this.findClickTarget(event.clientX, event.clientY);


        if(elem.isNull()){
            let method = this.clickHandlers.get(this.mod);
            if(!method) return;
            method(event);
        }
        elem.getEventHandler().handleClick(event);

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