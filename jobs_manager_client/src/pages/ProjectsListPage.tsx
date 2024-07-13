import { ProjectSimple } from "../api/abstraction/ProjectClient";

import { FieldType } from "../components/lists/Filter";
import ProjectElement from "../components/lists/listElements/ProjectElement";
import ServerBoundList from "../components/lists/ServerBoundList";


const ProjectListPage = () => {
    return (
        <div>
            <ServerBoundList<ProjectSimple> 
                pager={{
                    defaultPageSize: 10
                }} 
                filter={{parameters: [{label: "admin", additionalData: [], fieldType: FieldType.SingleInput}]}} 
                Wrapper={ProjectElement}
                endpoint={{
                    resourse: "/projects",
                    count: "/projects/count"
                }}
            />
        </div>
    );
};


export default ProjectListPage; 