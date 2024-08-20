import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";


import { JobNodeFullData } from "../../../api/abstraction/projectApi";
import { JobNodeVertice, ProjectGraph, updateProjectGraph } from "../../../api/ui/projectGraphApi";
import { PlugBarElement, StaticPlugBarConfig } from "./PlugBarElement";

import { DynamicCanvasConfig, GOF } from "./GOF";
import { TextNode } from "./TetxNode";
import { JobNodeElementEventHandler } from "./eventHandlers/JobNodeElementEventHandler";
import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import React from "react";
import { deleteJobNode } from "../../../api/abstraction/jobNodeApi";
import { AxiosResponse } from "axios";
import JobNodeMenu from "../menus/JobNodeMenu";


export interface StaticJobNodeElementConfig{
    width : number,
    height : number,
    plugBarConfig : StaticPlugBarConfig
    
}

export class JobNodeElement implements GraphElement{

    private gof : GOF;
    private eventHandler : JobNodeElementEventHandler;


    private children : PlugBarElement[];
    private parent: NullGraphElement = new NullGraphElement;


    private data : JobNodeFullData;
    private vertice : JobNodeVertice;
    private config : StaticJobNodeElementConfig;

    public constructor(gof : GOF, data : JobNodeFullData, vertice : JobNodeVertice, config : StaticJobNodeElementConfig,
        setProjectGraph : (projectGraph: ProjectGraph) => void ,
        setDynamic : React.Dispatch<React.SetStateAction<DynamicCanvasConfig>>

    ){

        this.gof = gof;

        this.data = data;
        this.vertice = vertice;
        this.config = config;


        const inputBar = new PlugBarElement(gof, this, config.plugBarConfig, false);
        const outputBar = new PlugBarElement(gof, this,  config.plugBarConfig, true);

        this.children = [inputBar, outputBar];

        this.eventHandler = new JobNodeElementEventHandler(this, setProjectGraph, setDynamic);

    }
    public deleteElement(): Promise<AxiosResponse<void>> | null {
        return deleteJobNode(this.getGof().getContext().projectData.id, this.data.id).then(response => {
            return updateProjectGraph(this.getGof().getContext().projectData.id);
        });
    }


    public getMenuComponent(): JSX.Element {
        return <JobNodeMenu element={this}/>
    }



    public getEventHandler(): GraphElementEventHandler {
        return this.eventHandler;
    }

    public getConfig() : StaticJobNodeElementConfig{
        return this.config;
    }

    public getGof(): GOF {
        return this.gof;
    }

    public getGofId(): string {
        return `JobNodeElement_${this.data.id}`;
    }
    public isNull(): boolean {
        return false;
    }
    
    public doesContainPoint(x: number, y: number){

        const coords = this.getCoords();
        const leftCorner = {x : coords[0], y : coords[1]};

        const width = this.config.width;
        const height = this.config.height;

        return x >= leftCorner.x && x <= leftCorner.x + width 
            &&
            y >= leftCorner.y && y <= leftCorner.y + height;
         
    }

    public getCoords() : [number, number]{
        let [x, y] = [this.vertice.x, this.vertice.y];
        
        let elemOffset = this.gof.getContext().dynamic.elemOffset[this.getGofId()];
        if(!elemOffset) elemOffset = {x : 0, y : 0}
        x += elemOffset.x;
        y += elemOffset.y;

        return [x, y]
    }

    public draw(ctx : CanvasRenderingContext2D) : void{

        let [x, y] =  this.getCoords();
        let [dx, dy] = this.gof.getOffsets();
        x += dx;
        y += dy;

        ctx.strokeStyle = "black";
        ctx.lineWidth = 5;
        ctx.strokeRect(x, y, 
            this.config.width,
            Math.max(this.config.height, this.children[0].getHeight(), this.children[1].getHeight())
        );
        
        let nameTextNode : TextNode = new TextNode({
            x : x + this.config.width * 0.2, 
            y : y + this.config.height * 0.4, 
            color : "black", 
            font : "16px Arial", 
            maxWidth : this.config.width * 0.6
        }, this.data.jobNodeDetails.name);
        nameTextNode.draw(ctx);
  

        for(let child of this.children){
            child.draw(ctx);
        }

    }
    public getChildren() : GraphElement[]{
        return this.children;
    }

    public getParent() : GraphElement{
        return this.parent;
    }




    public getData() : JobNodeFullData{
        return this.data;
    }

    public getVertice() : JobNodeVertice{
        return this.vertice;
    }
}
