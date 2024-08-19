import { useEffect, useState } from "react";
import { AppUserSimple, deleteModerator, deleteUser, retrieveUser, Roles, updateDetails, updateModeratorPassword, updatePassword, updateRoles } from "../../api/authorization/usersApi";
import { UsersManagementPageContext } from "../../pages/UsersManagementPage";
import OpenerComponent from "../OpenerComponent";
import { AxiosResponse } from "axios";
import SecuredNode from "../../authentication/SecuredNode";
import { ErrorMessage, Field, Formik, Form } from "formik";
import { validatePassword } from "../../validation/customValidations";
import * as Yup from 'yup';
import { NotificationType, useNotificator } from "../notifications/Notificator";


export interface AppUserMenuArgs{
    username : string,
    context : UsersManagementPageContext
}


const updatePasswordValidationSchema = Yup.object({
    newPassword : Yup.string()
        .required('Password is required'),
    repeatPassword : Yup.string()
        .oneOf([Yup.ref('newPassword'), ''], 'Passwords must match')
        .required('Confirm password is required')
}); 

const udpateDetailsValidationSchema = Yup.object({
    fullname : Yup.string()
        .min(3, "Full Name must be of size between 3 and 50")
        .max(50, "Full Name must be of size between 3 and 50")
        .nonNullable("Full Name must not be blank")
        .required("Full Name is required"),
    description: Yup.string()
        .min(3, "Description must be of size between 3 and 500")
        .max(500, "Description must be of size between 3 and 500")
        .nonNullable("Description must not be blank")
    
})

const AppUserMenu = ({
    username, context
} : AppUserMenuArgs) => {


    const {pushNotification, catchRequestError} = useNotificator();

    const [data, setData] = useState<AppUserSimple | null>(null);

    const [newRoles, setNewRoles] = useState<string[]>([]);


    function getData(){
        retrieveUser(username).then(r => {
            setData(r.data)
        }).catch(catchRequestError);
    }

    function changeRoles(){
        updateRoles(username, newRoles)
            .then(() => {
                getData();
                context.setUsersListDependency(Math.random());
                pushNotification({
                    message: "Roles are updated successfully",
                    time: 3,
                    type: NotificationType.INFO
                });
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
                    <strong>Description:</strong>
                    <p>{data.appUserDetails.description || "Description is not specified"}</p>
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
                                <Formik
                                    initialValues={{
                                        newPassword: "",
                                        repeatPassword: ""
                                    }}
                                    validationSchema={updatePasswordValidationSchema}
                                    onSubmit={(values) => {
                                        let updatePasswordPromise : Promise<AxiosResponse<void>>;
                                        if(
                                            data.authorities.map(auth => auth.authority).includes("ROLE_MODERATOR") || 
                                            data.authorities.map(auth => auth.authority).includes("ROLE_ADMIN")
                                        ){
                                            updatePasswordPromise = updateModeratorPassword(username, values.newPassword);
                                        }else{
                                            updatePasswordPromise =  updatePassword(username, values.repeatPassword);
                                        }
                                        updatePasswordPromise
                                            .then(() => {
                                                pushNotification({
                                                    message: "Password Changed Successfully",
                                                    time: 5,
                                                    type: NotificationType.INFO
                                                })
                                            }).catch(catchRequestError);
                                    }}
                                >
                                    {() => (
                                        <Form>
                                            <h5>Update password:</h5>
                                            <div>
                                                <strong><label htmlFor="newPassword">New Password:</label></strong>
                                                <Field name="newPassword" className="form-control m-2" type="password" validate={validatePassword}/>
                                                <ErrorMessage className="text-danger" name="newPassword" component="div"/>
                                            </div>
                                            
                                            <div>
                                                <strong><label htmlFor="newPassword">Repeat new Password:</label></strong>
                                                <Field name="repeatPassword" className="form-control m-2" type="password"/>
                                                <ErrorMessage className="text-danger" name="repeatPassword" component="div"/>
                                            </div>
                                            
                                            <button type="submit" className="btn btn-success m-2">Change Password</button>
                                        </Form>
                                    )}
                                </Formik>
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
                                            <button className="btn btn-danger m-2" onClick={e => setNewRoles([])}>Deselect</button>
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
                                <Formik
                                    initialValues={{
                                        fullname: data.appUserDetails.fullname,
                                        desciption: data.appUserDetails.description
                                    }}
                                    validationSchema={udpateDetailsValidationSchema}
                                    onSubmit={(values) => {
                                        console.log("t is hjere");
                                        updateDetails(username, {fullname: values.fullname, description: values.desciption})
                                            .then(() => {
                                                getData();
                                                context.setUsersListDependency(Math.random());

                                                pushNotification({
                                                    message: "The Details are updated",
                                                    time: 5,
                                                    type: NotificationType.INFO
                                                });
                                            }).catch();
                                    }}
                                >
                                    {() => (
                                        <Form>
                                            <h5>Update details:</h5>

                                            <div>
                                                <strong><label>Full Name:</label></strong>
                                                <Field className="form-control m-2" name="fullname" id="fullname"/>
                                                <ErrorMessage className="text-danger" name="fullname" component="div"/>
                                            </div>

                                            <div>
                                                <strong><label>Description:</label></strong>
                                                <Field as="textarea" className="form-control m-2" name="description" id="description"/>
                                                <ErrorMessage className="text-danger" name="description" component="div"/>
                                            </div>
                                            
                                            <button className="btn btn-success m-2" type="submit">Update Details</button>
                                        </Form>
                                    )}
                                  
                                </Formik>
                            }
                            
                        />

                        <hr/>

                        {data.authorities.map(authority => authority.authority).includes("ROLE_ADMIN") || 
                            <button className="btn btn-danger m-2" onClick={delUser}>Delete</button>
                        }
                    </SecuredNode>
                    

                </div>
            }
        </>
       
    );
}

export default AppUserMenu;