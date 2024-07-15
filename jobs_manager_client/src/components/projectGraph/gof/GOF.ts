import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";

export class GOF{


    private children : GraphElement[] = [];


    public getSizes() : [number, number]{
        return [0,0];
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