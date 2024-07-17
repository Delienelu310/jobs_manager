import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { ChannelElement } from "../ChannelElement";
import { PanelMods } from "./PanelMods";



export class ChannelElementEventHandler implements GraphElementEventHandler{

    private element : ChannelElement;
    
    constructor(element : ChannelElement){
        this.element = element;
    }
    public handleMouseMove: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod)=> {
        
    }


    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {
        
    };



}