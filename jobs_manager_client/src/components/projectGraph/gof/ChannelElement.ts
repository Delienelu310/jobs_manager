import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";
import { NullGraphElement } from "./NullGraphElement";

import { ChannelFullData } from "../../../api/abstraction/projectApi";
import { PlugElement } from "./PlugElement";
import { PlugBarElement } from "./PlugBarElement";
import { TextNode } from "./TetxNode";


export interface StaticChannelConfig{
    width : number,
    height : number
}

export class ChannelElement implements GraphElement{

    private gof : GOF;
    private channelData : ChannelFullData;
    private config : StaticChannelConfig;

    private inputId : string;
    private outputId : string;

    constructor(gof : GOF, channelData : ChannelFullData, config : StaticChannelConfig, inputId : string, outputId : string){
        this.gof = gof;
        this.channelData = channelData;
        this.config = config;

        this.inputId = inputId;
        this.outputId = outputId;
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


    private getPoints() : [[number, number], [number, number]]{
        const inputPlug : PlugElement = this.gof.findById(this.inputId) as PlugElement;
        const outputPlug : PlugElement = this.gof.findById(this.outputId) as PlugElement;

        let leftPoint : [number, number] = inputPlug.getCoords();
        let leftConfig = (inputPlug.getParent() as PlugBarElement).getConfig();
        let rightPoint : [number, number] = outputPlug.getCoords();
        let rightConfig = (inputPlug.getParent() as PlugBarElement).getConfig();

        

        leftPoint[0] += leftConfig.plugWidth;
        leftPoint[1] += leftConfig.plugHeight / 2;

        rightPoint[1] += rightConfig.plugHeight / 2;

        return [leftPoint, rightPoint];
    }



    public getCoords() : [number, number]{
       let [leftPoint, rightPoint] = this.getPoints();


        let middlePoint = [(leftPoint[0] + rightPoint[0]) / 2, (leftPoint[1] + rightPoint[1]) / 2];

        return [middlePoint[0] - this.config.width / 2, middlePoint[1] - this.config.height / 2];

    }


    public doesContainPoint(x: number, y: number): boolean {

        const [boxX, boxY] = this.getCoords(); 

        return x >= boxX && x <= boxX + this.config.width &&
            y >= boxY && y <= boxY + this.config.height;

    }


    public draw(ctx : CanvasRenderingContext2D): void {

        let [leftPoint, rightPoint] = this.getPoints();
        let [boxX, boxY] = this.getCoords();
        let [dx, dy] = this.gof.getOffsets();

        boxX += dx;
        boxY += dy;

        ctx.strokeStyle = "black";
        ctx.lineWidth =  10;

        ctx.beginPath();
        ctx.moveTo(leftPoint[0], leftPoint[1]);
        ctx.lineTo(rightPoint[0], rightPoint[1]);
        ctx.stroke();
        ctx.closePath();

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