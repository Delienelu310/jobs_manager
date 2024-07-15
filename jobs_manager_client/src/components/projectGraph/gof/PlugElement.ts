import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";
import { JobNodeElement } from "./JobNodeElement";

import { PlugBarElement } from "./PlugBarElement";


export class PlugElement implements GraphElement{

    private gof : GOF;
    private parent : PlugBarElement;
    private label : string;

    constructor(gof : GOF, parent : PlugBarElement, label : string){
        this.gof = gof;
        this.parent = parent;
        this.label = label;        
    }
    
    public getGof(): GOF {
        return this.gof;
    }

    public getGofId(): string {
        return this.getParent().getParent().isNull() ? 
            `project_${this.parent.getOrientation() ? "output" : "input" }_${this.label}`
            :
            `jobnode_${(this.parent.getParent() as JobNodeElement).getData().id}_${this.parent.getOrientation() ? "output" : "input" }_${this.label}`
    }

    public getCoords(): [number, number]{
        let [plugX, plugY] = this.parent.getCoords();
        plugX += (this.parent.getConfig().width - this.parent.getConfig().plugWidth) / 2;
        plugY += this.parent.getConfig().distanceBetween + 
            this.parent.getChildren().indexOf(this) * (this.parent.getConfig().plugHeight + this.parent.getConfig().distanceBetween);
        return [plugX, plugY]
    }

    public doesContainPoint(x: number, y: number): boolean {
        let [plugX, plugY] = this.getCoords();
        
        return x >= plugX && x <= plugX + this.parent.getConfig().plugWidth &&
            y >= plugY && y <= plugY + this.parent.getConfig().plugHeight;
    }

    public draw(): void {
        throw new Error("Method not implemented.");
    }

    public getChildren(): GraphElement[] {
        return [];
    }
    public getParent(): GraphElement {
        return this.parent;
    }

    public isNull(): boolean {
        return false;
    }

}