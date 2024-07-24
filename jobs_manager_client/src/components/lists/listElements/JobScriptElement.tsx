import React from "react";
import { JobScriptSimple } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "../../JobScriptMenu";

export interface JobScriptListContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,
    setJobSciptsListDependency :  React.Dispatch<React.SetStateAction<number>>,
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>
}

export interface JobScriptElementArgs{
    data : JobScriptSimple,
    context : JobScriptListContext
} 

const JobScriptElement = ({data, context} : JobScriptElementArgs) => {
    return (
        <div>
            <h3>{data.jobScriptDetails.name}</h3>
            <span>ID: {data.id}</span>
            <br/>
            <span>Author : {data.author.username}</span>
            <br/>
            <strong>Extension: {data.extension}</strong>
            <br/>
            <h4>Class full name:</h4>
            <strong>{data.classFullName}</strong>

            <button className="btn btn-success" onClick={e => {
                context.setMenu((
                    <JobScriptMenu
                        data={data}
                        setMenu={context.setMenu}
                        setJobSciptsListDependency={context.setJobSciptsListDependency}
                        setJobsFileListDependency={context.setJobsFileListDependency}
                    />
                ));
            }}>More...</button>
        </div>
    );
};

export default JobScriptElement;