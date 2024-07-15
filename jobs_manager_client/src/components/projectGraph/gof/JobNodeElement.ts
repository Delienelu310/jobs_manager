import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";


import { JobNodeFullData } from "../../../api/abstraction/projectApi";
import { JobNodeVertice } from "../../../api/ui/projectGraphApi";
import { PlugBarElement, StaticPlugBarConfig } from "./PlugBarElement";

import { GOF } from "./GOF";


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

    public draw() : void{
        return;
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
