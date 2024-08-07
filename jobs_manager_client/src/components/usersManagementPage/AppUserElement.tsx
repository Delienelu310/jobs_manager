import "../../css/components/lists/commonListsElements.css"

import { AppUserSimple } from "../../api/authorization/usersApi";
import { UsersManagementPageContext } from "../../pages/UsersManagementPage";
import { useEffect } from "react";
import AppUserMenu from "./AppUserMenu";


interface AppUserElementArgs{
    data : AppUserSimple,
    context : UsersManagementPageContext
}

const AppUserElement = ({data, context} : AppUserElementArgs) => {

    useEffect(() => {console.log(data.authorities)}, []);

    return (
        <div className="list_table_element list_table_row_4" onClick={e => context.setMenu(
            <AppUserMenu
                key={"user_menu_" + data.username}
                username={data.username}
                context={context}
            />
        )}>
            <div className="list_table_cell">
                {data.username}
            </div>

            <div className="list_table_cell">
                {data.appUserDetails.fullname}
            </div>

            <div className="list_table_cell"> 
                {data.authorities.map(authority => authority.authority).join(", ")}
            </div>  


        </div>
    );
};
export default AppUserElement;