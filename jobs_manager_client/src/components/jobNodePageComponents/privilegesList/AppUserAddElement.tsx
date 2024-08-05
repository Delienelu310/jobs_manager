import "../../../css/components/lists/commonListsElements.css"

import { AppUserSimple } from "../../../api/authorization/usersApi";


export interface AppUserAddElementContext{
    action : (username : string) => void
}

export interface AppUserAddElementArgs{
    data : AppUserSimple
    context : AppUserAddElementContext
}

const AppUserAddElement = ({data, context} : AppUserAddElementArgs) => {

    return (
        <div className="list_table_element list_table_row_4" onClick={e => context.action(data.username)}>

            <div className="list_table_cell">
                <h5>{data.username}</h5>
            </div>
            <div className="list_table_cell">
                <i>{data.appUserDetails.fullname}</i>
            </div>

        </div>
    );

}


export default AppUserAddElement;