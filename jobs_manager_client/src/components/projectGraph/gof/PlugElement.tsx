import { AxiosResponse } from "axios";
import { removeJobNodePlug } from "../../../api/abstraction/jobNodeApi";
import { removeProjectPlug } from "../../../api/abstraction/projectApi";
import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import { PlugElementEventHandler } from "./eventHandlers/PlugElementEventHandler";
import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";
import { JobNodeElement } from "./JobNodeElement";

import { PlugBarElement } from "./PlugBarElement";
import { TextNode } from "./TetxNode";
import JobNodePlugMenu from "../menus/JobNodePlugMenu";
import ProjectPlug from "../menus/ProjectPlug";


export class PlugElement implements GraphElement{

    private gof : GOF;
    private eventHandler : PlugElementEventHandler;


    private parent : PlugBarElement;
    private label : string;

    constructor(gof : GOF, parent : PlugBarElement, label : string){
        this.gof = gof;
        this.parent = parent;
        this.label = label;     
        
        this.eventHandler = new PlugElementEventHandler(this);
    }

    public deleteElement(): Promise<AxiosResponse<void>> | null  {
        if(this.parent.getParent().isNull()){
            return removeProjectPlug(this.getGof().getProjectData().id, this.parent.getOrientation(), this.label);
        }else{
            return removeJobNodePlug(this.getGof().getProjectData().id, (this.parent.getParent() as JobNodeElement).getData().id, this.parent.getOrientation(), this.label);
        }
        
    }

    public getMenuComponent(): JSX.Element {
        if(this.parent.getParent().isNull()){
            
            let channelData = this.parent.getOrientation() ?
                this.getGof().getProjectData().outputChannels[this.label]
                :
                this.getGof().getProjectData().inputChannels[this.label]
            ;
            
            return <ProjectPlug label={this.label} channel={channelData}/>
        }else{
            return <JobNodePlugMenu/>
        }


        return <div>this is plug component</div>
    }
    public getEventHandler(): GraphElementEventHandler {
        return this.eventHandler;
    }
    
    public getGof(): GOF {
        return this.gof;
    }

    public getGofId(): string {
        return this.getParent().getParent().isNull() ? 
            `project_${this.parent.getOrientation() ? "output" : "input" }_${this.label}`
            :
            `jobnode_${(this.parent.getParent() as JobNodeElement).getData().id}_${this.parent.getOrientation() ? "output" : "input" }_${this.label}`
    }

    public getCoords(): [number, number]{
        let [plugX, plugY] = this.parent.getCoords();
        plugX += (this.parent.getConfig().width - this.parent.getConfig().plugWidth) / 2;
        plugY += this.parent.getConfig().distanceBetween + 
            this.parent.getChildren().indexOf(this) * (this.parent.getConfig().plugHeight + this.parent.getConfig().distanceBetween);
        return [plugX, plugY]
    }

    public doesContainPoint(x: number, y: number): boolean {
        let [plugX, plugY] = this.getCoords();
        
        return x >= plugX && x <= plugX + this.parent.getConfig().plugWidth &&
            y >= plugY && y <= plugY + this.parent.getConfig().plugHeight;
    }

    public draw(ctx : CanvasRenderingContext2D): void {

        let [x, y] = this.getCoords();
        let [dx, dy] = this.gof.getOffsets();
        x += dx; 
        y += dy;

        ctx.fillStyle = 'black'; 
        ctx.fillRect(x, y, this.parent.getConfig().plugWidth, this.parent.getConfig().plugHeight);


        let textNode : TextNode = new TextNode({
            x : x, 
            y : y + this.parent.getConfig().plugHeight / 2, 
            color : "white", 
            font : "16px Arial", 
            maxWidth : this.parent.getConfig().plugWidth
        }, this.label);

        textNode.draw(ctx);
    }

    public getChildren(): GraphElement[] {
        return [];
    }
    public getParent(): GraphElement {
        return this.parent;
    }

    public isNull(): boolean {
        return false;
    }

}