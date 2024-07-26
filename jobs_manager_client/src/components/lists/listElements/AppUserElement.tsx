import { AppUserSimple } from "../../../api/authorization/usersApi";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import AppUserJobNodeMenu from "../../AppUserJobNodeMenu";

export interface AppUserElementContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface AppUserElementArgs{
    data : AppUserSimple,
    context : AppUserElementContext
}

const AppUserElement = ({data, context} : AppUserElementArgs) => {
    return  (
        <div>   

            <h3>{data.username}</h3>
            <strong>Full name: </strong>
            <i>{data.appUserDetails.fullname}</i>

            <br/>
            <button className="m-2 btn btn-success" onClick={e => context.jobNodePageRefresh.setMenu(<AppUserJobNodeMenu
                username={data.username}
                context={{jobNodePageRefresh : context.jobNodePageRefresh}}
            />)}>More...</button>

        </div>
    );
}

export default AppUserElement;