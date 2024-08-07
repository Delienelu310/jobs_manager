import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";

import { ChannelFullData } from "../../../api/abstraction/projectApi";
import { PlugElement } from "./PlugElement";
import { PlugBarElement } from "./PlugBarElement";
import { TextNode } from "./TetxNode";
import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import { ChannelElementEventHandler } from "./eventHandlers/ChannelElementEventHandler";
import { deleteChannel } from "../../../api/abstraction/channelApi";
import { AxiosResponse } from "axios";
import ChannelMenu from "../menus/ChannelMenu";


export interface StaticChannelConfig{
    width : number,
    height : number
}

export class ChannelElement implements GraphElement{
    
    
    private gof : GOF;
    private eventHandler : ChannelElementEventHandler;

    private channelData : ChannelFullData;
    private config : StaticChannelConfig;

    private inputIds : Set<string> = new Set([]);
    private outputIds : Set<string> = new Set([]);

    constructor(gof : GOF, channelData : ChannelFullData, config : StaticChannelConfig){
        this.gof = gof;
        this.channelData = channelData;
        this.config = config;

        this.eventHandler = new ChannelElementEventHandler(this);

    }
    public deleteElement(): Promise<AxiosResponse<void>> | null {
        return deleteChannel(this.getGof().getProjectData().id, this.channelData.id);
    }
    public getMenuComponent() : JSX.Element{
        return <ChannelMenu element={this}/>
    }

    public getEventHandler(): GraphElementEventHandler {
        return this.eventHandler;
    }

    public getInputIds() : Set<string>{
        return this.inputIds;
    }

    public getOutputIds() : Set<string>{
        return this.outputIds;        
    }


    public getData() : ChannelFullData{
        return this.channelData;
    }

    public getGof(): GOF {
        return this.gof;
    }

    public getGofId(): string {
        return `ChannelElement_${this.channelData.id}`;
    }


    private getPoints() : [[number, number][], [number, number][]]{

        let inputs : Set<PlugElement> = new Set<PlugElement>();
        let outputs : Set<PlugElement> = new Set<PlugElement>();

        
        this.inputIds.forEach(id => inputs.add(this.gof.findById(id) as PlugElement));
        this.outputIds.forEach(id => outputs.add(this.gof.findById(id) as PlugElement));

        let leftPoints : [number, number][] = [];
        let rightPoints : [number, number][] = [];

        inputs.forEach(inputPlug => {
            let leftPoint : [number, number] = inputPlug.getCoords();
            let leftConfig = (inputPlug.getParent() as PlugBarElement).getConfig();
            
            leftPoint[0] += leftConfig.plugWidth;
            leftPoint[1] += leftConfig.plugHeight / 2;

            leftPoints.push(leftPoint);
        });

        outputs.forEach(outputPlug => {
            let rightPoint : [number, number] = outputPlug.getCoords();
            let rightConfig = (outputPlug.getParent() as PlugBarElement).getConfig();


            rightPoint[1] += rightConfig.plugHeight / 2;

            rightPoints.push(rightPoint);
        });

        return [leftPoints, rightPoints];
    }



    public getCoords() : [number, number]{
       let [leftPoints, rightPoints] = this.getPoints();

       if(leftPoints.length == 0 || rightPoints.length == 0) return [0,0];

    
       let middleLeftPoint : [number, number] = [0, 0];
       for(let leftPoint of leftPoints){
            middleLeftPoint[0] += leftPoint[0];
            middleLeftPoint[1] += leftPoint[1];
       }
       middleLeftPoint[0] /= leftPoints.length;
       middleLeftPoint[1] /= leftPoints.length;

       let middleRightPoint : [number, number] = [0, 0];
       for(let rightPoint of rightPoints){
            middleRightPoint[0] += rightPoint[0];
            middleRightPoint[1] += rightPoint[1];
       }
       middleRightPoint[0] /= rightPoints.length;
       middleRightPoint[1] /= rightPoints.length;

       

        let middlePoint = [(middleLeftPoint[0] + middleRightPoint[0]) / 2, (middleLeftPoint[1] + middleRightPoint[1]) / 2];

        return [middlePoint[0] - this.config.width / 2, middlePoint[1] - this.config.height / 2];

    }


    public doesContainPoint(x: number, y: number): boolean {

        const [boxX, boxY] = this.getCoords(); 

        return x >= boxX && x <= boxX + this.config.width &&
            y >= boxY && y <= boxY + this.config.height;

    }


    public draw(ctx : CanvasRenderingContext2D): void {

        if(Array.from(this.inputIds).length == 0 || Array.from(this.outputIds).length == 0) return;
        let [leftPoints, rightPoints] = this.getPoints();
        let [boxX, boxY] = this.getCoords();
        let [dx, dy] = this.gof.getOffsets();

        boxX += dx;
        boxY += dy;

        // draw the channel box

        ctx.fillStyle = "white";
        ctx.fillRect(boxX, boxY, this.config.width, this.config.height);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 5;
        ctx.strokeRect(boxX, boxY, this.config.width, this.config.height);

        let textNode : TextNode = new TextNode({
            x : boxX, 
            y : boxY + this.config.height / 2, 
            color : "black", 
            font : "16px Arial", 
            maxWidth : this.config.width
        }, this.channelData.channelDetails.name);

        textNode.draw(ctx);

        //draw the lines to the box
        ctx.strokeStyle = "black";
        ctx.lineWidth =  10;

        for(let leftPoint of leftPoints){
            ctx.beginPath();
            ctx.moveTo(leftPoint[0] + dx, leftPoint[1] + dy);
            ctx.lineTo(boxX , boxY + this.config.height / 2 );
            ctx.stroke();
            ctx.closePath();
        }

        for(let rightPoint of rightPoints){
            ctx.beginPath();
            ctx.moveTo(boxX + this.config.width , boxY + this.config.height / 2 );
            ctx.lineTo(rightPoint[0] + dx, rightPoint[1] + dy);
            ctx.stroke();
            ctx.closePath();
        }

    }


    public getChildren(): GraphElement[] {
        return [];
    }

    public getParent(): GraphElement {
        return new NullGraphElement();
    }

    public isNull(): boolean {
        return false;
    }

}