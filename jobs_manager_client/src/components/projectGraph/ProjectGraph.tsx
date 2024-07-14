import { useEffect, useRef } from "react";
import { ProjectGraph  } from "../../api/ui/projectGraphApi";


interface ProjectGraphComponent{
    projectGraph : ProjectGraph
}

const ProjectGraphComponent = ({projectGraph} : ProjectGraphComponent) => {


    const canvasRef = useRef<HTMLCanvasElement>(null);

    function drawGraph(graph : ProjectGraph, ctx : CanvasRenderingContext2D){
        for(let vertice of graph.vertices){
            ctx.fillStyle = '#f0f0f0'; 
            ctx.fillRect(vertice.x, vertice.y, 50, 50);
            ctx.strokeStyle = 'brown'; 
            ctx.strokeRect(vertice.x, vertice.y, 50, 50);

            ctx.fillStyle = "black";
            ctx.fillText(vertice.jobNode.id, vertice.x + 50 / 2, vertice.y + 25);
        }
    }

    useEffect(() => {

        if(projectGraph == null) return;
        
    
        const canvas = canvasRef.current;
        if(canvas == null) return;

        const ctx = canvas.getContext('2d');

        if(ctx) drawGraph(projectGraph, ctx);

    }, [projectGraph]);

    return (
        <div>
            {projectGraph && <canvas ref={canvasRef}/>}
        </div>
    )
}

export default ProjectGraphComponent;