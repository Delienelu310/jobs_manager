import { useState } from "react";
import { ProjectSimple } from "../api/abstraction/ProjectClient";

import { FieldType } from "../components/lists/Filter";
import ProjectElement from "../components/projectListPageComponents/ProjectElement";
import ServerBoundList from "../components/lists/ServerBoundList";
import { createProject, deleteProject, ProjectDetails } from "../api/abstraction/projectApi";
import { useNavigate } from "react-router-dom";
import { updateProjectGraph } from "../api/ui/projectGraphApi";
import OpenerComponent from "../components/OpenerComponent";
import { useAuth } from "../authentication/AuthContext";
import { Roles } from "../api/authorization/usersApi";
import SecuredNode from "../authentication/SecuredNode";
import { NotificationType, useNotificator } from "../components/notifications/Notificator";

import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from 'yup';

export interface ProjectListPageContext{

}

const createProjectValidationSchema = Yup.object({
    name : Yup.string()
        .min(3, "Exception: Name must be of size 3 to 50")
        .max(50, "Exception: Name must be of size 3 to 50")
        .nonNullable("Exception: Name must not be blank")
        .required("Exception: Name must be specified"),
    description: Yup.string()
        .min(3, "Exception: Description must be of size 3 to 500")
        .max(500, "Exception: Description must be of size 3 to 500")
        .nonNullable("Exception: Description must not be blank")
});

const ProjectListPage = () => {


    const {catchRequestError, pushNotification} = useNotificator();

    const {authentication} = useAuth();
    
    const navigate = useNavigate();

    const [projectListDependency, setProjectListDependency] = useState<number>(0);

    function create(projectDetails : ProjectDetails){
        createProject(projectDetails)
            .then(response => {
                updateProjectGraph(response.data)
                    .then(r => navigate(`/projects/${response.data}`))
                    .then(r => {
                        setProjectListDependency(Math.random());
                    }).catch(e => {
                        deleteProject(response.data).then(r => {
                            pushNotification({
                                message: "Invalid Project was Automatically Deleted",
                                time: 5,
                                type: NotificationType.INFO
                            });
                        }).catch(e => {
                            pushNotification({
                                message: "Error: Created Project is Invalid, Automatic Deletion Failed " + e.response && e.response.data,
                                time: 5,
                                type: NotificationType.ERROR
                            });
                        }).finally(() => catchRequestError(e))
                       
                    });
                
            }).catch(catchRequestError);
        ;
    }
    
    return (
        <div>

            <SecuredNode
                jobNodePrivilegeConfig={null}
                projectPrivilegeConfig={null}
                moderator={true}
                roles={[Roles.MANAGER]}
                alternative={null}
            >
                <div className="m-5">
                    <OpenerComponent 
                        closedLabel={<h4>Create Project</h4>}
                        openedElement={
                            <div style={{margin: "30px 10%"}}>
                                <Formik 
                                    initialValues={{
                                        name: '',
                                        description: ''
                                    }}
                                    onSubmit={values => create({
                                        name: values.name,
                                        description : values.description || null
                                    })}
                                    validationSchema={createProjectValidationSchema}
                                >
                                    {() => (
                                        <Form>
                                            <h4 className="m-2">Create project:</h4>

                                            <div>
                                                <strong><label htmlFor="name">Name :</label></strong> 
                                                <Field name="name" id="name" className="form-control m-2"/>
                                                <ErrorMessage name="name" className="text-danger" component="div"/>
                                            </div>
                                        <div> 
                                                <strong><label htmlFor="description">Description:</label></strong>
                                                <Field as="textarea" name="description" id="description" className="form-control m-2"/>
                                                <ErrorMessage name="description" className="text-danger" component="div"/>
                                        </div>
                                            <button type="submit" className="btn btn-success m-2">Create</button>
                                        </Form>
                                    )}
                                </Formik>
                            </div>
                            
                        }
                    />
                </div>
                
            </SecuredNode>
           
            
           
           
          
            <h4 className="m">Project List:</h4>

            <ServerBoundList<ProjectSimple, ProjectListPageContext> 
                pager={{
                    defaultPageSize: 10
                }} 
                filter={{parameters: [{label: "admin", additionalData: [], fieldType: FieldType.SingleInput}]}} 
                Wrapper={ProjectElement}
                endpoint={{
                    resourse: "/projects?",
                    count: "/projects/count?"
                }}
                context={{}}
                dependencies={[projectListDependency]}
            />
        </div>
    );
};


export default ProjectListPage; 