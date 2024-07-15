import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";

import { JobNodeElement } from "./JobNodeElement";
import { NullGraphElement } from "./NullGraphElement";



export interface StaticPlugBarConfig{
    x : number,
    y : number

    width : number,
    distanceBetween : number,
    plugHeight : number,
    plugWidth : number
}

export class PlugBarElement implements GraphElement{

    private gof : GOF;

    private parent : JobNodeElement | NullGraphElement;
    private config : StaticPlugBarConfig;
    private rightOrientation : boolean;

    constructor(
        gof : GOF,
        parent : JobNodeElement | NullGraphElement,
        config : StaticPlugBarConfig,
        rightOrienation : boolean
    ){
        this.gof = gof;
        this.parent = parent;
        this.config = config;
        this.rightOrientation = rightOrienation;

        if(
            parent.isNull()
        ){
            
        }else{

        }
    }
    getGof(): GOF {
        return this.gof;
    }


    doesContainPoint(x: number, y: number): boolean {
        throw new Error("Method not implemented.");
    }
    draw(): void {
        throw new Error("Method not implemented.");
    }
    getChildren(): GraphElement[] {
        throw new Error("Method not implemented.");
    }
    getParent(): GraphElement {
        return this.parent;
    }
    setParent(parent: JobNodeElement): void {
        this.parent = parent;
    }
    isNull(): boolean {
        return false;
    }
    getGofId(): string {
        if(this.parent.isNull()){
            return `PlugBarElement_project_${this.rightOrientation ? "output" : "input"}`;
        }else{
            return `PlugBarElement_${(this.parent as JobNodeElement).getData().id}_${this.rightOrientation ? "output" : "input"}`;
        }
    }

}