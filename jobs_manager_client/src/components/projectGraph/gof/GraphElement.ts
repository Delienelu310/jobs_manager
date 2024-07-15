import { GOF } from "./GOF";


export interface GraphElement{

    doesContainPoint(x : number, y : number) : boolean;
    draw(ctx : CanvasRenderingContext2D) : void;

    getChildren() : GraphElement[];
    getParent() : GraphElement;

    isNull() : boolean;

    getGofId() : string;

    getGof() : GOF;

}