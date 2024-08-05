import "../../../css/components/lists/commonListsElements.css"



import { AppUserSimple } from "../../../api/authorization/usersApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import AppUserJobNodeMenu from "./AppUserJobNodeMenu";

export interface AppUserElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserElementArgs{
    data : AppUserSimple,
    context : AppUserElementContext
}

const AppUserElement = ({data, context} : AppUserElementArgs) => {
    return  (
        <div className="list_table_element list_table_row_4" onClick={e => context.jobNodePageRefresh.setMenu(<AppUserJobNodeMenu
            username={data.username}
            context={{jobNodePageRefresh : context.jobNodePageRefresh}}
        />)}>   

            <div className="list_table_cell">
                <h5>{data.username}</h5>
            </div>

            <div className="list_table_cell">
                <i>{data.appUserDetails.fullname}</i>
            </div>

        
        </div>
    );
}

export default AppUserElement;