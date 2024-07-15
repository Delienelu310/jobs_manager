import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";


export class ChannelElement implements GraphElement{

    private gof : GOF;

    constructor(gof : GOF){
        this.gof = gof;
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
        throw Error("Channel element cannot have children");
    }
    getParent(): GraphElement {
        throw new Error("");
    }
    setParent(parent: GraphElement): void {
        throw new Error("Method not implemented.");
    }
    isNull(): boolean {
        return false;
    }

}