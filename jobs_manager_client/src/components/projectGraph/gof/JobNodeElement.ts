import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";


import { JobNodeFullData } from "../../../api/abstraction/projectApi";
import { JobNodeVertice } from "../../../api/ui/projectGraphApi";
import { PlugBarElement, StaticPlugBarConfig } from "./PlugBarElement";

import { GOF } from "./GOF";
import { TextNode } from "./TetxNode";


export interface StaticJobNodeElementConfig{
    width : number,
    height : number,
    plugBarConfig : StaticPlugBarConfig
    
}

export class JobNodeElement implements GraphElement{

    private gof : GOF;
    private children : GraphElement[];
    private parent: NullGraphElement = new NullGraphElement;


    private data : JobNodeFullData;
    private vertice : JobNodeVertice;
    private config : StaticJobNodeElementConfig;

    public constructor(gof : GOF, data : JobNodeFullData, vertice : JobNodeVertice, config : StaticJobNodeElementConfig){

        this.gof = gof;

        this.data = data;
        this.vertice = vertice;
        this.config = config;


        const inputBar = new PlugBarElement(gof, this, config.plugBarConfig, false);
        const outputBar = new PlugBarElement(gof, this,  config.plugBarConfig, true);

        this.children = [inputBar, outputBar];

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
        const leftCorner = {x : this.vertice.x, y : this.vertice.y};

        const width = this.config.width;
        const height = this.config.height;

        return x >= leftCorner.x && x <= leftCorner.x + width 
            &&
            y >= leftCorner.y && y <= leftCorner.y + height;
         
    }

    public draw(ctx : CanvasRenderingContext2D) : void{

        let [x, y] = [this.vertice.x, this.vertice.y];
        let [dx, dy] = this.gof.getOffsets();
        x += dx;
        y += dy;


        ctx.strokeStyle = "black";
        ctx.lineWidth = 5;
        ctx.strokeRect(x, y, this.config.width, this.config.height);
        
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
