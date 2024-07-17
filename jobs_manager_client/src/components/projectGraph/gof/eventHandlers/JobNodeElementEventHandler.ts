import React from "react";

import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { JobNodeElement } from "../JobNodeElement";


export class JobNodeElementEventHandler implements GraphElementEventHandler{
    
    private element : JobNodeElement;
    constructor(element : JobNodeElement){
        this.element = element;
    }
    
    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void = (event) => {
        
    };

}