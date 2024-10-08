import React from "react";

import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { JobNodeElement } from "../JobNodeElement";
import { JobNodeVertice, ProjectGraph } from "../../../../api/ui/projectGraphApi";
import { DynamicCanvasConfig } from "../GOF";
import { PanelMods } from "./PanelMods";


export class JobNodeElementEventHandler implements GraphElementEventHandler{
    
    private element : JobNodeElement;
    private setProjectGraph : (projectGraph: ProjectGraph) => void;
    private setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>;

    constructor(element : JobNodeElement, 
        setProjectGraph : (projectGraph: ProjectGraph) => void,
        setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>
    ){
        this.element = element;
        this.setProjectGraph = setProjectGraph;
        this.setDynamic = setDynamic;

    }

    public handleMouseMove: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {
        
        
        let start = this.element.getGof().getContext().dynamic.dragData.start;
     
        let vertCopy = this.element.getGof().getContext().projectGraph.vertices.map(v => {
            
            if(v.jobNode.id == this.element.getData().id){
                  
                if(start == null) return v;
                return {
                    ...v,
                    x : v.x + event.clientX - start.x,
                    y : v.y + event.clientY - start.y
                }
            }else{
                return v;
            }
        });

        this.setProjectGraph({
            ...this.element.getGof().getContext().projectGraph,
            vertices : vertCopy
        });

        this.setDynamic({
            ...this.element.getGof().getContext().dynamic,
            dragData:{
                ...this.element.getGof().getContext().dynamic.dragData ,
                isDragging: true,
                start: {
                    x : event.clientX,
                    y : event.clientY
                }
            }
        });
    };  
    
    public handleClick: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {
        
    };

}