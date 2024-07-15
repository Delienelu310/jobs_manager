


export interface TextNodeConfig{
    font : string,
    color : string,
    maxWidth : number,
    x : number,
    y : number
}

export class TextNode{

    private config : TextNodeConfig;
    private text : string;

    constructor(config : TextNodeConfig, text : string){
        this.config = config;
        this.text = text;
    }

    public draw(ctx : CanvasRenderingContext2D){

        ctx.font = this.config.font;
        ctx.fillStyle = this.config.color;

        let textWidth = ctx.measureText(this.text).width;

        let actualText = this.text;

        if(textWidth > this.config.maxWidth){
            let actualTextLength = 1;
            while( ctx.measureText(this.text.substring(0, actualTextLength)).width < this.config.maxWidth){
                actualTextLength++ 
            }
            actualTextLength -= 4;
            actualText += "...";
        }

        ctx.fillText(actualText, this.config.x, this.config.y);
        
    }


}