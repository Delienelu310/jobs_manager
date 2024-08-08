import "../../css/components/lists/commonListsElements.css"

import { AppUserSimple } from "../../api/authorization/usersApi";
import  { useEffect } from "react";
import AppUserProjectMenu from "./AppUserProjectMenu";
import { ProjectPrivilegeListContext } from "./PrivilegeList";



export interface AppUserElementArgs{
    data : AppUserSimple,
    context : ProjectPrivilegeListContext
}

const AppUserElement = ({data, context} : AppUserElementArgs) => {

    useEffect(() => {console.log(data.authorities)}, []);

    return (
        <div className="list_table_element list_table_row_4" onClick={e => context.setMenu(
            <AppUserProjectMenu
                context={context}
                key={"user_menu_" + data.username}
                username={data.username}
    
            />
        )}>
            <div className="list_table_cell">
                {data.username}
            </div>

            <div className="list_table_cell">
                {data.appUserDetails.fullname}
            </div>

            <div className="list_table_cell"> 
                {context.projectData.privileges[data.username] ? context.projectData.privileges[data.username].list.join(", ") : "NO ROLES"}
            </div>  


        </div>
    );
};
export default AppUserElement;