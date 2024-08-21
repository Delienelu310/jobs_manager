import "../../../css/components/jobNodePageComponent/jobScriptList/jobScriptCreator.css"


import { useState } from "react";
import { createJobScript, JobScriptDTO, retreiveJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import { JobsFileExtension } from "../../../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from 'yup'
import { NotificationType, useNotificator } from "../../notifications/Notificator";
import JobScriptMenu from "../menu/JobScriptMenu";

export interface JobScriptCreatorContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobScriptCreatorArgs{
    context : JobScriptCreatorContext
}


const JobScriptCreatorValidationSchema = Yup.object({
    name : Yup.string()
        .min(3)
        .max(50)
        .required(),
    description: Yup.string()
        .min(3)
        .max(500),
    classFullName : Yup.string()
        .nonNullable()
        .min(1)
        .max(200)
        .required()
});

const JobScriptCreator = ({context} : JobScriptCreatorArgs) => {
    

    const {catchRequestError, pushNotification} = useNotificator();

    function create(jobScriptDTO : JobScriptDTO){
        createJobScript(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, jobScriptDTO)
            .then(response => {
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }

                pushNotification({
                    message: "Job Script was created successfully",
                    time : 5,
                    type : NotificationType.INFO
                })

                

                context.jobNodePageRefresh.setMenu(
                    <JobScriptMenu
                        context={context}
                        data={response.data}
                        
                    />
                )
               

            }).catch(catchRequestError);
    }
    
    return (
        <SecuredNode
            alternative={<h5>You dont have access to the job script creator</h5>}
            projectPrivilegeConfig={null}
            roles={null}
            moderator
            jobNodePrivilegeConfig={{
                jobNode: context.jobNodePageRefresh.jobNodeData,
                privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
            }}
        >
            <div className="job_script_creator">
                <Formik
                    initialValues={{
                        name : "",
                        descipription : null,
                        classFullName : "",
                        extension : JobsFileExtension.JAR
                    }}
                    onSubmit={(values) => create({classFullName: values.classFullName, extension: values.extension, jobScriptDetails: {
                        name : values.name,
                        description: values.descipription || null
                    }})}
                    validationSchema={JobScriptCreatorValidationSchema}
                >
                    {() => (
                        <Form>
                            <h3>Create Job Script:</h3>
                        
                            <div>
                                <strong><label htmlFor="name">Name: </label></strong>
                                <Field name="name" id="name" className="form-control m-2" />
                                <ErrorMessage name="name" component="div" className="text-danger"/>

                            </div>

                            

                            <div>
                                <strong><label htmlFor="description">Desciprtion: </label></strong>
                                <Field name="description" id="description" className="form-control m-2" as="textarea"/>
                                <ErrorMessage name="description" component="div" className="text-danger"/>

                            </div>

                            <div>
                                <strong><label htmlFor="classFullName">Class full name: </label></strong>
                                <Field name="classFullName" id="classFullName" className="form-control m-2"/>
                                <ErrorMessage name="classFullName" component="div" className="text-danger"/>

                            </div>

                            <div>
                                <strong><label htmlFor="extension">Extension: </label></strong>
                                <Field name="extension" id="extension" className="form-control m-2" as="select">
                                    {Object.values(JobsFileExtension).map(val => <option value={val}>{val}</option>)}
                                </Field>
                                <ErrorMessage name="extension" component="div" className="text-danger"/>
                            </div>


                            <button className="btn btn-success" type="submit">Create</button>
                        </Form>
                    )}
                </Formik>

               
            </div>
        </SecuredNode>
     
    );
}

export default JobScriptCreator;