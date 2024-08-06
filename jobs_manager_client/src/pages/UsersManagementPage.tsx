import { useState } from "react";
import { AppUserSimple } from "../api/authorization/usersApi";
import { FieldType } from "../components/lists/Filter";
import ServerBoundList from "../components/lists/ServerBoundList";
import AppUserElement from "../components/usersManagementPage/AppUserElement";
import AddUserPanel from "../components/usersManagementPage/AddUserPanel";
import OpenerComponent from "../components/OpenerComponent";
import SecuredNode from "../authentication/SecuredNode";


export  interface UsersManagementPageContext{
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>
    setUsersListDependency : React.Dispatch<React.SetStateAction<number>>
}

const UsersManagementPage = () => {

    const [menu, setMenu] = useState<JSX.Element | null>(null);

    const [usersListDependency, setUsersListDependency] = useState<number>(0);

    return (
        <div>
            
            
            <div className="m-5">
                
                {menu}
                {menu && <button className="btn btn-danger m-3" onClick={() => setMenu(null)}>Close Menu</button>}
            </div>
            
            <SecuredNode
                jobNodePrivilegeConfig={null}
                projectPrivilegeConfig={null}
                roles={[]}
                moderator={true}
                alternative={null}
            >
                <div className="m-5">

                    <OpenerComponent
                        closedLabel={<h5>Add User</h5>}
                        openedElement={
                            <AddUserPanel context={{
                                setMenu : setMenu,
                                setUsersListDependency : setUsersListDependency
                            }}/>
                        }
                    />
                </div>
            </SecuredNode>
            
            

            <ServerBoundList<AppUserSimple, UsersManagementPageContext>
                context={{
                    setMenu: setMenu,
                    setUsersListDependency : setUsersListDependency
                }}
                dependencies={[usersListDependency]}
                endpoint={{
                    resourse: `/users?`,
                    count: `/users/count?`
                }}
                pager={{defaultPageSize : 10}}
                filter={{
                    parameters: [
                        {label : "fullname", additionalData: [], fieldType: FieldType.SingleInput}
                    ]
                }}
                Wrapper={AppUserElement}

            />

        </div>
    );
}

export default UsersManagementPage;