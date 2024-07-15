import { ProjectFullData } from "../../../api/abstraction/projectApi";
import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";


export interface StaticCanvasConfig{
    width : number,
    height : number,
    padding : {
        x : number,
        y : number
    }
}

export class GOF{


    private children : GraphElement[] = [];
    private projectData : ProjectFullData;
    private config : StaticCanvasConfig;

    

    public constructor(config : StaticCanvasConfig, projectData : ProjectFullData){
        this.config = config;
        this.projectData = projectData;
    }

    public getProjectData() : ProjectFullData{
        return this.projectData;
    }
    public getCanvasConfig() : StaticCanvasConfig{
        return this.config;
    }

    public getOffsets() : [number, number]{
        return [0,0];
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


    public addElement(element : GraphElement) : void{

        this.children.push(element);
    }


    public draw() : void {

    }


}