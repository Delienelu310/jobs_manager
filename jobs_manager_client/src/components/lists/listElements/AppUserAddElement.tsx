import React from "react";
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
        <div>

            <h3>{data.username}</h3>
            <strong>FullName:</strong>
            <i>{data.appUserDetails.fullname}</i>
            <br/>

            <button onClick={e => context.action(data.username)} className="btn btn-success">Add</button>

        </div>
    );

}


export default AppUserAddElement;