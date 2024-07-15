import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";

import { PlugBarElement } from "./PlugBarElement";


export class PlugElement implements GraphElement{

    private gof : GOF;
    private parent : PlugBarElement;

    constructor(gof : GOF, parent : PlugBarElement){
        this.gof = gof;
        this.parent = parent;        
    }
    
    getGof(): GOF {
        throw new Error("Method not implemented.");
    }
    getGofId(): string {
        throw new Error("Method not implemented.");
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
    setParent(parent: GraphElement): void {
        throw new Error("Method not implemented.");
    }
    isNull(): boolean {
        return false;
    }

}