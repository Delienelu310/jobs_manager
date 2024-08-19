
import { createModerator, createUser, isUsernameBusy, Roles } from "../../api/authorization/usersApi";
import {  AxiosResponse } from "axios";
import { UsersManagementPageContext } from "../../pages/UsersManagementPage";
import SecuredNode from "../../authentication/SecuredNode";
import { NotificationType, useNotificator } from "../notifications/Notificator";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from 'yup'
import { validatePassword } from "../../validation/customValidations";

export interface AddUserPanelArgs{
    context : UsersManagementPageContext
}


// The validation schema
const validationSchema = Yup.object({
    username: Yup.string()
        .matches(/^[a-zA-Z0-9]{3,20}$/, "Username should have only letters and numbers, be of 3 to 20 size")
        .required('Username is required'),
    password: Yup.string()
        .required('Password is required'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), ''], 'Passwords must match')
        .required('Confirm password is required'),
    fullname: Yup.string()
        .min(3, "Full Name must be of size 3 to 50")
        .max(50, "Full Name must be of size 3 to 50")
        .nonNullable("Full Name must not be blank")
        .required("Full Name is requried"),
    description : Yup.string()
        .min(3, "Description must be of size 3 to 500")
        .max(500, "Description must be of size 3 to 500")
    
  });

const AddUserPanel = ({context} : AddUserPanelArgs) => {

    const {catchRequestError, pushNotification} = useNotificator();

    async function checkIfUsernameIsFree(username : string) : Promise<string | undefined>{
    try{
        if(!username) return "Username is required"
        return (await isUsernameBusy(username)).data ? "Username is already used" : undefined;
    }catch(e){
        
        pushNotification({
            message: "Internal Server Error: Impossible to check the Username",
            time: 5,
            type: NotificationType.INFO
        })

        return "Internal Server Error: Impossible to check the Username";
    }
    
}


    return (
        <div>
            <h3>Add User</h3>



            <Formik
                initialValues={{
                    username: '',
                    password: '',
                    confirmPassword: '',
                    fullname : '',
                    description: '',
                    authorities: [] as string[]
                }}
                validationSchema={validationSchema}
                onSubmit={(values) => {
    

                    let chosenPromise : Promise<AxiosResponse<void>>;
                    if(values.authorities.includes("MODERATOR")){
                        chosenPromise = createModerator({username : values.username, passwordEncoded: btoa(values.password),  appUserDetails: {
                            fullname: values.fullname, description: values.description
                        }, roles: []});
                
                    }else{
                        chosenPromise = createUser({username : values.username, passwordEncoded : btoa(values.password), appUserDetails : {
                            fullname : values.fullname, description : values.description
                        }, roles: values.authorities})
                    }
                    chosenPromise.then(r => {
                        context.setUsersListDependency(Math.random());

                        pushNotification({message: `The User ${values.username} Created Successfully`, time: 5, type: NotificationType.INFO});

                    }).catch(catchRequestError);
                }}
            >
                {({setFieldValue, values}) => (
                    <Form>


                        <div>
                            <strong><label htmlFor="username">Username</label></strong>
                            <Field className="form-control m-2" type="text" id="username" name="username" validate={checkIfUsernameIsFree}/>
                            <ErrorMessage className="text-danger" name="username" component="div" />
                        </div>

                        <div>
                            <strong><label htmlFor="password">Password</label></strong>
                            <Field className="form-control m-2" type="password" id="password" name="password" validate={validatePassword}/>
                            <ErrorMessage className="text-danger" name="password" component="div" />
                        </div>

                        <div>
                            <strong><label htmlFor="confirmPassword">Confirm Password</label></strong>
                            <Field className="form-control m-2" type="password" id="confirmPassword" name="confirmPassword" />
                            <ErrorMessage className="text-danger" name="confirmPassword" component="div" />
                        </div>

                        <div>
                            <strong><label htmlFor="fullname">Full Name</label></strong>
                            <Field className="form-control m-2" type="text" id="fullname" name="fullname" />
                            <ErrorMessage className="text-danger" name="fullname" component="div" />
                        </div>
                        <div>
                            <strong><label htmlFor="description">Description</label></strong>
                            <Field as="textarea" className="form-control m-2" type="text" id="description" name="description" />
                            <ErrorMessage className="text-danger" name="description" component="div" />
                        </div>
                        <div>
                            <strong><label htmlFor="authorities">Authorities</label></strong>
                            <select  
                                id="authorities"
                                name="authorities"
                                className="form-control m-2" 
                                multiple 
                                value={values["authorities"]}  
                                onChange={e => {
                                    let newAuthorities = Array.from(values['authorities']);
                                    console.log(newAuthorities);
                                    if(newAuthorities.includes(e.target.value)){
                                        newAuthorities = newAuthorities.filter(val => val != e.target.value);
                                    }else{
                                        newAuthorities.push(e.target.value);
                                    }
                                    console.log(newAuthorities)
                                    setFieldValue("authorities",newAuthorities);
                                }}
                            >
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

                            <button type="button" onClick={e => setFieldValue("authorities", [])} className="btn btn-danger m-2">Deselect</button>

                        </div>
                        

                        <button className="btn btn-success m-2">Create User</button>

                    </Form>
                )}
            </Formik>
        
        </div>
    );
}

export default AddUserPanel;