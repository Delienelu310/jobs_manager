import { GOF } from "./GOF";


export interface GraphElement{

    doesContainPoint(x : number, y : number) : boolean;
    draw() : void;

    getChildren() : GraphElement[];
    getParent() : GraphElement;

    isNull() : boolean;

    getGofId() : string;

    getGof() : GOF;

}