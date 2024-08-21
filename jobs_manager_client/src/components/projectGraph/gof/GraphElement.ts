import { AxiosResponse } from "axios";
import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import { GOF } from "./GOF";


export interface GraphElement{

    doesContainPoint(x : number, y : number) : boolean;
    draw(ctx : CanvasRenderingContext2D) : void;

    getChildren() : GraphElement[];
    getParent() : GraphElement;

    isNull() : boolean;

    getGofId() : string;

    getGof() : GOF;


    getEventHandler() : GraphElementEventHandler;


    getMenuComponent() : JSX.Element | null;

    deleteElement() : Promise<AxiosResponse<void>> | null;

}