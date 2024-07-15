import { useEffect, useRef, useState } from "react";
import { ProjectGraph  } from "../../api/ui/projectGraphApi";
import { ProjectFullData } from "../../api/abstraction/projectApi";

import { StaticJobNodeElementConfig } from "./gof/JobNodeElement";
import { StaticPlugBarConfig } from "./gof/PlugBarElement";
import { StaticCanvasConfig } from "./gof/GOF";

interface StaticGraphCanvasConfig{
    
    canvas : StaticCanvasConfig;
    jobNodes : StaticJobNodeElementConfig;
    projectPlugs : StaticPlugBarConfig

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

    function drawCanvas(
        ctx : CanvasRenderingContext2D
    ){
        ctx.strokeStyle = 'black'; 
        ctx.lineWidth = 5; 

        ctx.strokeRect(0, 0, staticConfig.canvas.width, staticConfig.canvas.height);
    }

    function drawJobNodes(ctx : CanvasRenderingContext2D){
        for(let vertice of projectGraph.vertices){
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

            //draw inputs and outputs
        }
    }

    function drawJobChannels(ctx : CanvasRenderingContext2D){

    }

    useEffect(() => {

        if(projectGraph == null) return;
        
    
        const canvas = canvasRef.current;
        if(canvas == null) return;

        const ctx = canvas.getContext('2d');

        if(!ctx) return;
        
        drawCanvas(ctx);
        drawJobNodes(ctx);
        drawJobChannels(ctx);

    }, [projectGraph, projectFullData]);

    return (
        <div>
            {projectGraph && <canvas ref={canvasRef} width={staticConfig.canvas.width} height={staticConfig.canvas.height}/>}
        </div>
    )
}

export default ProjectGraphComponent;