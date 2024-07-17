
import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { PlugBarElement } from "../PlugBarElement";


export class PlugBarElementEventHandler implements GraphElementEventHandler{
    
    private element : PlugBarElement;
    public constructor(element : PlugBarElement){
        this.element = element;
    }
    
    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void = (event) => {

    };



}