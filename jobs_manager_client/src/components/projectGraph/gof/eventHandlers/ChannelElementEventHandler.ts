import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { ChannelElement } from "../ChannelElement";



export class ChannelElementEventHandler implements GraphElementEventHandler{

    private element : ChannelElement;
    
    constructor(element : ChannelElement){
        this.element = element;
    }


    handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void = (event) => {
        
    };



}