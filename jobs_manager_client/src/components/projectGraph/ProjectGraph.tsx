import { useEffect, useRef, useState } from "react";
import { ProjectGraph  } from "../../api/ui/projectGraphApi";
import { ProjectFullData } from "../../api/ui/projectApi";



interface StaticGraphCanvasConfig{
    

    canvas : {
        width : number,
        height : number,
        padding : {
            x : number,
            y : number
        }
    },

    jobNodes : {
        width : number,
        height : number
    },

    projectPlugs : {
        inputs : {
            x : number,
            y : number, 
        },
        outputs : {
            x : number,
            y : number
        }
        width : number,
        distanceBetween : number,
        plugSize : number
        
    }
}

interface DynamicGraphCanvasConfig{
    offset : {          //represent how client moved the whole picture with his left mouse button 
        x : number,
        y : number
    }
}

interface ProjectGraphComponent{
    projectGraph : ProjectGraph,
    projectFullData : ProjectFullData,
    staticConfig : StaticGraphCanvasConfig,
}

const ProjectGraphComponent = ({projectFullData, projectGraph, staticConfig} : ProjectGraphComponent) => {



    const canvasRef = useRef<HTMLCanvasElement>(null);

    const [dynamicConfig, setDynamicConfig] = useState<DynamicGraphCanvasConfig>({
        offset : {
            x : 0,
            y : 0
        }
    });


    function getActualPosition( [x, y] : [number, number]) : [number, number]{
        return [
            x + dynamicConfig.offset.x + staticConfig.canvas.padding.x, 
            y + dynamicConfig.offset.y + staticConfig.canvas.padding.y
        ];
    }

    function drawProjectInputsOutputs(
        ctx : CanvasRenderingContext2D
    ){

        let width = staticConfig.projectPlugs.width;
        let height = Object.keys(projectFullData.inputChannels).length * (staticConfig.projectPlugs.distanceBetween + staticConfig.projectPlugs.plugSize) + 
            staticConfig.projectPlugs.distanceBetween
    
        let [inputX, inputY] = getActualPosition([staticConfig.projectPlugs.inputs.x, staticConfig.projectPlugs.inputs.y]);


        ctx.strokeStyle = 'black'; 
        ctx.lineWidth = 5;
        ctx.strokeRect(inputX, inputY, width, height);


        let counter = 0;
        for(let label of Object.keys(projectFullData.inputChannels)){
            counter++;
            let [inputChannelX, inputChannelY] = getActualPosition([
                staticConfig.projectPlugs.inputs.x + staticConfig.projectPlugs.width / 2,
                staticConfig.projectPlugs.inputs.y + staticConfig.projectPlugs.distanceBetween * (counter) + staticConfig.projectPlugs.plugSize * (counter + 1) / 2
            ])
            ctx.beginPath();
            ctx.arc(inputChannelX, inputChannelY, staticConfig.projectPlugs.plugSize / 2, 0, 2 * Math.PI, false);
            console.log(inputChannelX, inputChannelY);
            ctx.fill();
            ctx.closePath();
        
        }


        let [outputX, outputY] = getActualPosition([staticConfig.projectPlugs.outputs.x, staticConfig.projectPlugs.outputs.y]);


        ctx.strokeStyle = 'black'; 
        ctx.lineWidth = 5;
        ctx.strokeRect(outputX, outputY, width, height);


        counter = 0;
        for(let label of Object.keys(projectFullData.outputChannels)){
            counter++;
            let [inputChannelX, inputChannelY] = getActualPosition([
                staticConfig.projectPlugs.outputs.x + staticConfig.projectPlugs.width / 2,
                staticConfig.projectPlugs.outputs.y + staticConfig.projectPlugs.distanceBetween * (counter) + staticConfig.projectPlugs.plugSize * (counter + 1) / 2
            ])
            ctx.beginPath();
            ctx.arc(inputChannelX, inputChannelY, staticConfig.projectPlugs.plugSize / 2, 0, 2 * Math.PI, false);
            console.log(inputChannelX, inputChannelY);
            ctx.fill();
            ctx.closePath();
        
        }

    }

    function drawCanvas(
        ctx : CanvasRenderingContext2D
    ){
        ctx.strokeStyle = 'black'; 
        ctx.lineWidth = 5; 

        ctx.strokeRect(0, 0, staticConfig.canvas.width, staticConfig.canvas.height);
    }

    function drawGraph(graph : ProjectGraph, ctx : CanvasRenderingContext2D){
        for(let vertice of graph.vertices){
            ctx.fillStyle = '#f0f0f0'; 


            let [x, y] = getActualPosition([vertice.x, vertice.y]);

            ctx.fillRect(x, y, staticConfig.jobNodes.width, staticConfig.jobNodes.height);
            ctx.strokeStyle = 'brown'; 
            ctx.strokeRect(x, y, staticConfig.jobNodes.width, staticConfig.jobNodes.height);
            ctx.fillStyle = "black";
            ctx.fillText(vertice.jobNode.id.length > staticConfig.jobNodes.width / 5 ? 
                vertice.jobNode.id.substring(staticConfig.jobNodes.width / 5 - 3) + "..."
                :
                vertice.jobNode.id
                , x + 5,  y + staticConfig.jobNodes.height / 2);
        }
    }

    useEffect(() => {

        if(projectGraph == null) return;
        
    
        const canvas = canvasRef.current;
        if(canvas == null) return;

        const ctx = canvas.getContext('2d');

        if(!ctx) return;
        
        drawCanvas(ctx);
        drawProjectInputsOutputs(ctx);
        drawGraph(projectGraph, ctx);

    }, [projectGraph]);

    return (
        <div>
            {projectGraph && <canvas ref={canvasRef} width={staticConfig.canvas.width} height={staticConfig.canvas.height}/>}
        </div>
    )
}

export default ProjectGraphComponent;