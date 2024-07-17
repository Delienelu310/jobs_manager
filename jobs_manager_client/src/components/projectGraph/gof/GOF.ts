import { ProjectFullData } from "../../../api/abstraction/projectApi";
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

export enum GOFClickMod{
    CURSOR, CONNECT, DELETE
}

export class GOF{


    private children : GraphElement[] = [];
    private projectData : ProjectFullData;
    private config : StaticCanvasConfig;
    private dynamic : DynamicCanvasConfig;

    private mod : GOFClickMod = GOFClickMod.CURSOR;

    

    public constructor(config : StaticCanvasConfig, projectData : ProjectFullData, dynamic : DynamicCanvasConfig){
        this.config = config;
        this.projectData = projectData;
        this.dynamic = dynamic;
    }

    public getMod() : GOFClickMod{
        return this.mod;
    }

    public setMod(mod : GOFClickMod) : void{
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

            console.log(elem.getGofId());

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

    public handleClick(){

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