import { useState } from "react";
import { AppUserDetails, createModerator, createUser, Roles } from "../../api/authorization/usersApi";
import { AxiosResponse } from "axios";
import { UsersManagementPageContext } from "../../pages/UsersManagementPage";
import SecuredNode from "../../authentication/SecuredNode";


export interface AddUserPanelArgs{
    context : UsersManagementPageContext
}

const AddUserPanel = ({context} : AddUserPanelArgs) => {

    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [repeatPassword, setRepeatPassword] = useState<string>("");

    const [appUserDetails, setAppUserDetails] = useState<AppUserDetails>({
        fullname: ""
    });
    const [authorities ,setAuthorities] = useState<string[]>([]);


    function addUser(){

        if(password != repeatPassword) throw Error("Passwords are different");

        let chosenPromise : Promise<AxiosResponse<void>>;
        if(authorities.includes("MODERATOR")){
            chosenPromise = createModerator({username : username, passwordEncoded: btoa(password),  appUserDetails: appUserDetails, roles: []});
    
        }else{
            chosenPromise = createUser({username : username, passwordEncoded : btoa(password), appUserDetails : appUserDetails, roles: authorities})
        }
        chosenPromise.then(r => {
            context.setUsersListDependency(Math.random());

        }).catch(e => console.log(e));
    }

    return (
        <div>
            <h3>Add User</h3>

            <strong>Username</strong>
            <input className="form-control m-2" value={username} onChange={e => setUsername(e.target.value)}/>

            <strong>Password</strong>
            <input type="password" className="form-control m-2" value={password} onChange={e => setPassword(e.target.value)}/>

            <strong>Repeat password</strong>
            <input type="password" className="form-control m-2" value={repeatPassword} onChange={e => setRepeatPassword(e.target.value)}/>


            <strong>Full name</strong>
            <input className="form-control m-2" value={appUserDetails.fullname} onChange={e => setAppUserDetails({...appUserDetails, fullname: e.target.value})}/>
        
            <strong>Authorities</strong>
            <select className="form-control m-2" multiple value={authorities}  onChange={e => {
                let newAuthorities = Array.from(authorities);
                if(newAuthorities.includes(e.target.value)){
                    newAuthorities = newAuthorities.filter(val => val != e.target.value);
                }else{
                    newAuthorities.push(e.target.value);
                }
                setAuthorities(newAuthorities);

            }}>
                <SecuredNode
                    jobNodePrivilegeConfig={null}
                    projectPrivilegeConfig={null}
                    moderator={false}
                    roles={[]}
                    alternative={null}
                >
                    <option value={"MODERATOR"}>MODERATOR</option>   
                </SecuredNode>
                
                {Object.values(Roles).map(role => <option value={role}>{role}</option>)}
            </select>

            <button onClick={e => setAuthorities([])} className="btn btn-danger m-2">Deselect</button>

            <br/>

            <button className="btn btn-success m-2" onClick={addUser}>Create User</button>
        
        </div>
    );
}

export default AddUserPanel;