import React from "react";
import { PanelMods } from "./PanelMods";



export interface GraphElementEventHandler{
    handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void;
    handleMouseMove : (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void;
}