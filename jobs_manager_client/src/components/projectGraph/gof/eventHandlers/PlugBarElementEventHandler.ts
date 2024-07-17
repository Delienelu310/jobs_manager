
import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { PlugBarElement } from "../PlugBarElement";
import { PanelMods } from "./PanelMods";


export class PlugBarElementEventHandler implements GraphElementEventHandler{
    
    private element : PlugBarElement;
    public constructor(element : PlugBarElement){
        this.element = element;
    }
    public handleMouseMove: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {
        if(this.element.getParent().isNull()) return;

        this.element.getParent().getEventHandler().handleMouseMove(event, mod);
    };
    
    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void = (event) => {

    };



}