import React from "react";

import { GraphElementEventHandler } from "./GraphElementEventHandler";
import { JobNodeElement } from "../JobNodeElement";
import { JobNodeVertice, ProjectGraph } from "../../../../api/ui/projectGraphApi";
import { DynamicCanvasConfig } from "../GOF";
import { PanelMods } from "./PanelMods";


export class JobNodeElementEventHandler implements GraphElementEventHandler{
    
    private element : JobNodeElement;
    private setProjectGraph : React.Dispatch<React.SetStateAction<ProjectGraph | undefined>>;
    private setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>;

    constructor(element : JobNodeElement, 
        setProjectGraph : React.Dispatch<React.SetStateAction<ProjectGraph | undefined>>,
        setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>
    ){
        this.element = element;
        this.setProjectGraph = setProjectGraph;
        this.setDynamic = setDynamic;

    }
    public handleMouseMove: (event: React.MouseEvent<HTMLCanvasElement, MouseEvent>, mod : PanelMods) => void = (event, mod) => {
        
        
        let start = this.element.getGof().getDynamic().dragData.start;
        console.log(start);

        let vertCopy = this.element.getGof().getProjectGraph().vertices.map(v => {
            
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
            ...this.element.getGof().getProjectGraph(),
            vertices : vertCopy
        });

        this.setDynamic({
            ...this.element.getGof().getDynamic(),
            dragData:{
                ...this.element.getGof().getDynamic().dragData ,
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