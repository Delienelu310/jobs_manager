import React from "react";
import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { PlugElement } from "../PlugElement";


export class PlugElementEventHandler implements GraphElementEventHandler{

    private element : PlugElement;
    public constructor(element : PlugElement){
        this.element = element;
    }


    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void = () => {

    };

}