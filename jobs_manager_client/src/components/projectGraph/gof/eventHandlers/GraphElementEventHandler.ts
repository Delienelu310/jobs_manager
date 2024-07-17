import React from "react";



export interface GraphElementEventHandler{
    handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>) => void;
}