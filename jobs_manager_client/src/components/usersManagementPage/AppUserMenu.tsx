import { useEffect, useState } from "react";
import { AppUserDetails, AppUserSimple, deleteModerator, deleteUser, retrieveUser, Roles, updateDetails, updateModeratorPassword, updatePassword, updateRoles } from "../../api/authorization/usersApi";
import { UsersManagementPageContext } from "../../pages/UsersManagementPage";
import OpenerComponent from "../OpenerComponent";
import { AxiosResponse } from "axios";
import SecuredNode from "../../authentication/SecuredNode";


export interface AppUserMenuArgs{
    username : string,
    context : UsersManagementPageContext
}

const AppUserMenu = ({
    username, context
} : AppUserMenuArgs) => {


    const [data, setData] = useState<AppUserSimple | null>(null);

    const [newPassword, setNewPassword] = useState<string>("");
    const [newRepeatPassword, setNewRepeatPassword] = useState<string>("");

    const [newRoles, setNewRoles] = useState<string[]>([]);

    const [newUserDetails, setNewUserDetails] = useState<AppUserDetails>({
        fullname : ""
    });


    function getData(){
        retrieveUser(username).then(r => {
            setData(r.data)
        }).catch(e => console.log(e));
    }

    function changePassword(){
        if(!data) return;

        let updatePasswordPromise : Promise<AxiosResponse<void>>;
        if(
            data.authorities.map(auth => auth.authority).includes("ROLE_MODERATOR") || 
            data.authorities.map(auth => auth.authority).includes("ROLE_ADMIN")
        ){
            updatePasswordPromise = updateModeratorPassword(username, newPassword);
        }else{
            updatePasswordPromise =  updatePassword(username, newPassword);
        }
        updatePasswordPromise
            .then(() => {
                
            }).catch(e => console.log(e));
    }

    function changeDetails(){
        updateDetails(username, newUserDetails)
            .then(() => {
                getData();
                context.setUsersListDependency(Math.random())
            }).catch(e => console.log(e));
    }

    function changeRoles(){
        updateRoles(username, newRoles)
            .then(() => {
                context.setUsersListDependency(Math.random());
            }).catch(e => console.log(e));
    }

    function delUser(){
        if(!data) return;

        let requestPromise : Promise<AxiosResponse<void>>;
        if(data.authorities.map(auth => auth.authority).includes("ROLE_MODERATOR")){
            requestPromise = deleteModerator(username);
        }else{
            requestPromise = deleteUser(username);
        }
        requestPromise
            .then(r => {
                context.setMenu(null);
                context.setUsersListDependency(Math.random());
            }).catch(e => console.log(e));
    }

    useEffect(() => {
        getData();
    }, []);


    return (

        <>
            {data == null ?
                <h3>Loading ...</h3>
                :
                 <div> 

                    <h3>App User Menu</h3>

                    <hr/>

                    <strong>Username: </strong> {data.username} <br/>
                    <strong>Full name: </strong> {data.appUserDetails.fullname} <br/>
                    <strong>Roles:</strong>
                    <br/>
                    {data.authorities.map(auth => auth.authority).map(auth => <>
                        <strong>{auth}</strong>
                        <br/>
                    </>)}

                    <hr/>

                    <SecuredNode
                        jobNodePrivilegeConfig={null}
                        projectPrivilegeConfig={null}
                        moderator={data.authorities.map(auth => auth.authority).includes("ROLE_MODERATOR")}
                        roles={[]}
                        alternative={null}
                    >
                        <OpenerComponent
                            closedLabel={  <h5>Update password:</h5>}
                            openedElement={
                                <div>
                                    <h5>Update password:</h5>
                                    <strong>New Password:</strong>
                                    <input className="form-control m-2" type="password" value={newPassword} onChange={e => setNewPassword(e.target.value)}/>
                                    <strong>Repeat new Password</strong>
                                    <input className="form-control m-2" type="password" value={newRepeatPassword} onChange={e => setNewRepeatPassword(e.target.value)}/>
                                    <button className="btn btn-success m-2" onClick={changePassword}>Change Password</button>
                                </div>
                            }
                        />
                        <hr/>   

                        {data && data.authorities.map(auth => auth.authority).filter(auth => auth == "ROLE_ADMIN" || auth == "ROLE_MODERATOR").length == 0 &&
                            <>
                                <OpenerComponent
                                    closedLabel={<h5>Update Roles</h5>}
                                    openedElement={
                                        <div>
                                            <h5>Update Roles</h5>

                                            <select className="form-control m-2" value={newRoles} multiple onChange={e => {
                                                let newNewRoles = Array.from(newRoles);

                                                if(newNewRoles.includes(e.target.value)){
                                                    newNewRoles = newNewRoles.filter(val => val != e.target.value)
                                                }else{
                                                    newNewRoles.push(e.target.value);
                                                }
                                                setNewRoles(newNewRoles);
                                            }}>
                                                {Object.values(Roles).map(role => <option value={role}>{role}</option>)}
                                            </select>
                                            <button className="btn btn-danger m-2">Deselect</button>
                                            <br/>

                                            <button className="btn btn-success m-2" onClick={changeRoles}>Update Roles</button>
                                        </div>
                                    }
                                /> 
                                <hr/>
                            </>
                        }
                  

                        <OpenerComponent
                            closedLabel={<h5>Update details:</h5>}
                            openedElement={
                                <div>
                                    <h5>Update details:</h5>


                                    <strong>Full Name:</strong>
                                    <input className="form-control m-2" value={newUserDetails.fullname} onChange={e => setNewUserDetails({...newUserDetails, fullname : e.target.value})}/>
                                    <button className="btn btn-success m-2" onClick={changeDetails}>Update Details</button>
                                </div>
                            }
                            
                        />

                    <hr/>

                        <button className="btn btn-danger m-2" onClick={delUser}>Delete</button>
                    </SecuredNode>
                    

                </div>
            }
        </>
       
    );
}

export default AppUserMenu;