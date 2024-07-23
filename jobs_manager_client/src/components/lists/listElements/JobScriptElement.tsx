import React from "react";
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";

export interface JobScriptListContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,
    setJobSciptsListDependency :  React.Dispatch<React.SetStateAction<number>>,
}

export interface JobScriptElementArgs{
    data : JobScriptSimple,
    context : JobScriptListContext
} 

const JobScriptElement = ({data, context} : JobScriptElementArgs) => {
    return (
        <div>
            {data.id}
        </div>
    );
};

export default JobScriptElement;