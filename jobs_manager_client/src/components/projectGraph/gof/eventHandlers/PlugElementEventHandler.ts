import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { PlugElement } from "../PlugElement";
import { PanelMods } from "./PanelMods";


export class PlugElementEventHandler implements GraphElementEventHandler{

    private element : PlugElement;
    public constructor(element : PlugElement){
        this.element = element;
    }
    public handleMouseMove: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event,mod) => {
        this.element.getParent().getEventHandler().handleMouseMove(event, mod);
    };


    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {

    };

}