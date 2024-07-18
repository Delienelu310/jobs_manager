import { AxiosResponse } from "axios";
import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import { PlugBarElementEventHandler } from "./eventHandlers/PlugBarElementEventHandler";
import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";

import { JobNodeElement } from "./JobNodeElement";
import { NullGraphElement } from "./NullGraphElement";
import { PlugElement } from "./PlugElement";
import ProjectPlugBarMenu from "../menus/ProjectPlugBarMenu";
import JobNodePlugBarMenu from "../menus/JobNodePlugBarMenu";



export interface StaticPlugBarConfig{
    x : number,
    y : number
    width : number,
    minHeight : number,
    distanceBetween : number,
    plugHeight : number,
    plugWidth : number
}

export class PlugBarElement implements GraphElement{

    private gof : GOF;
    private eventHandler : PlugBarElementEventHandler;

    private parent : JobNodeElement | NullGraphElement;
    private children : PlugElement[];
    private config : StaticPlugBarConfig;
    private rightOrientation : boolean;

    constructor(
        gof : GOF,
        parent : JobNodeElement | NullGraphElement,
        config : StaticPlugBarConfig,
        rightOrienation : boolean
    ){
        this.gof = gof;
        this.parent = parent;
        this.config = config;
        this.rightOrientation = rightOrienation;

        this.children = [];

        const labels : string[] = Object.keys(
            this.getParent().isNull() ? 
                (this.rightOrientation ? 
                    this.getGof().getProjectData().outputChannels
                    :
                    this.getGof().getProjectData().inputChannels
                )
                :
                (this.rightOrientation ? 
                    (this.getParent() as JobNodeElement).getData().output
                    :
                    (this.getParent() as JobNodeElement).getData().input
                )
        );

        for(let key of labels){
            const plugElement = new PlugElement(gof, this, key);
        
            this.children.push(plugElement);
            
        }

        this.eventHandler = new PlugBarElementEventHandler(this);
    }
    public deleteElement(): Promise<AxiosResponse<void>> | null   {
        return null;
    }
    public getMenuComponent(): JSX.Element {

        if(this.parent.isNull()){
            return <ProjectPlugBarMenu
                projectFullData={this.getGof().getProjectData()}
                orientation={this.getOrientation()}
                refresh={this.getGof().getRefresh()}
            />
        }else{
            return <JobNodePlugBarMenu element={this}/>
        }
    }
    public getEventHandler(): GraphElementEventHandler {
        return this.eventHandler;
    }


    public getGof(): GOF {
        return this.gof;
    }

    public getOrientation() : boolean{
        return this.rightOrientation;
    }

    public getConfig() : StaticPlugBarConfig{
        return this.config;
    }

    public getCoords(){
        let [barX, barY] = [0,0];
        if(this.getParent().isNull()){
            [barX, barY] = [this.rightOrientation ? this.getGof().getCanvasConfig().width - this.config.x - this.config.width : this.config.x , this.config.y];
            
        }else{
            [barX, barY] = [(this.getParent() as JobNodeElement).getVertice().x + 
                (this.rightOrientation ? 
                    (this.getParent() as JobNodeElement).getConfig().width - this.config.x - this.config.width
                    : 
                    this.config.x),
                    (this.getParent() as JobNodeElement).getVertice().y + this.config.y
                    
            ];
            
        }

        return [barX, barY];
    }

    public getHeight() : number{
        return Math.max(
            this.config.minHeight,
            this.getChildren().length * (this.config.distanceBetween + this.config.plugHeight) + this.config.distanceBetween
        );
    }


    public doesContainPoint(x: number, y: number): boolean {
        const height = this.getHeight();

        let [barX, barY] = this.getCoords();

        return x >= barX && x <= barX + this.config.width &&
            y >= barY && y <= barY + height;
    }

    public draw(ctx : CanvasRenderingContext2D): void {

        let [x, y] = this.getCoords();
        let [dx, dy] = this.gof.getOffsets();
        x += dx;
        y += dy;

        const height = this.getHeight()

        ctx.strokeStyle = 'brown'; 
        ctx.lineWidth = 5;
        ctx.strokeRect(x, y, this.config.width, height);

        for(let elem of this.children){
            elem.draw(ctx);
        }
    }

    public getChildren(): GraphElement[] {
        return this.children;
    }

    public getParent(): GraphElement {
        return this.parent;
    }
  
    public isNull(): boolean {
        return false;
    }

    public getGofId(): string {
        if(this.parent.isNull()){
            return `PlugBarElement_project_${this.rightOrientation ? "output" : "input"}`;
        }else{
            return `PlugBarElement_${(this.parent as JobNodeElement).getData().id}_${this.rightOrientation ? "output" : "input"}`;
        }
    }

}