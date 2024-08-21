import { useEffect, useState } from "react";
import { addJobEntityToQueue, QueueTypes } from "../../../api/ilum_resources/queueOperationsApi";
import { JobEntityDetails } from "../../../api/ilum_resources/jobEntityApi";
import { JobScriptSimple, retreiveJobScript } from "../../../api/ilum_resources/jobScriptsApi";
import JobScriptMenu from "./JobScriptMenu";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import JobEntityMenu from "./JobEntityMenu";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import { NotificationType, useNotificator } from "../../notifications/Notificator";

import * as Yup from 'yup'
import { validateJsonString } from "../../../validation/customValidations";

export interface JobEntityCreatorContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobEntityCreatorArgs{
    projectId : string,
    jobNodeId : string,
    jobScriptId : string,
    context : JobEntityCreatorContext
}

const JobEntityCreator = ({
    projectId,
    jobNodeId,
    jobScriptId,
    context
} : JobEntityCreatorArgs) => {
    

    const {catchRequestError, pushNotification} = useNotificator();

    const [jobScriptData, setJobScriptData] = useState<JobScriptSimple | null>(null);

    function getJobScript(){
        retreiveJobScript(projectId, jobNodeId, jobScriptId)
            .then(response => {
                setJobScriptData(response.data);
            })
            .catch(catchRequestError)
        ;
    }

    function addJob(jobEntityDetails : JobEntityDetails, configuration : string, chosenQueueType : string){
        const queueType : string = chosenQueueType;

        addJobEntityToQueue(projectId, jobNodeId, queueType, jobScriptId, {configuration : configuration, details : jobEntityDetails})
            .then(response => {
                
                if(
                    context.jobNodePageRefresh.chosenResourceList && 
                    (
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_QUEUE &&
                        queueType == QueueTypes.JOBS_QUEUE
                        ||
                        context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.TESTING_QUEUE &&
                        queueType == QueueTypes.TESTING_JOBS
                    )
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                
                context.jobNodePageRefresh.setMenu(<JobEntityMenu
                    jobEntityId={response.data}
                    context={{
                        jobNodePageRefresh: context.jobNodePageRefresh,
                        queueType: queueType
                    }}
                />)

                pushNotification({
                    message: "The job entity was created successfully",
                    time: 5,
                    type : NotificationType.INFO
                })
            }).then()
            .catch(catchRequestError);
        ;
    }

    useEffect(() => {
        getJobScript();
    }, []);
    
    return (
        <div>
            <h3>Job Entity Menu</h3>
            {jobScriptData ? 
                <>
                    <h5>Chosen script data:</h5>
                    <h5> {jobScriptData.jobScriptDetails.name}</h5>
                    <strong>Class name:</strong>
                    <i>{jobScriptData.classFullName}</i>
                    <br/>
                    <strong>Author: </strong> {jobScriptData.author.username}
                    <br/>
                    <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobScriptMenu
                        context={context}
                        data={jobScriptData.id}
                    />)}>More... </button>

                </>
                :
                <span>Job Script data is loading...</span>

            }
            <SecuredNode
                projectPrivilegeConfig={null}
                roles={null}
                jobNodePrivilegeConfig={{
                    jobNode : context.jobNodePageRefresh.jobNodeData,
                    privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
                }}
                alternative={
                    <h5>You dont have privilege to create scripts</h5>
                }
                moderator={true}
            >
               
                <hr/>
                <Formik
                    initialValues={{
                        name : "",
                        description : null as (null | string),
                        configuration : "",
                        chosenQueueType : "" 
                    }}
                    onSubmit={values => {
                        addJob({name : values.name, description : values.description}, values.configuration, values.chosenQueueType);
                    }}
                    validationSchema={Yup.object({
                        name : Yup.string()
                            .required()
                            .min(3)
                            .max(50)
                        ,
                        description : Yup.string()
                            .notRequired()
                            .nullable()
                            .min(3)
                            .max(500)
                        ,
                        configuration : Yup.string()
                            .notRequired()
                            .nullable()
                        ,
                        chosenQueueType : Yup.string()
                            .required()
                    })}
                >
                    {() => (
                        <Form>
                            <h5>Job Entity Data:</h5>


                            <div>
                                <strong><label htmlFor="chosenQueueType">Queue type: </label></strong>
                                <Field className="form-control m-2" as="select" name="chosenQueueType" id="chosenQueueType">
                                    <option value=""></option>

                                    {Object.values(QueueTypes).map(type => <SecuredNode
                                        projectPrivilegeConfig={null}
                                        roles={null}
                                        alternative={null}
                                        moderator={true}
                                        jobNodePrivilegeConfig={{
                                            jobNode: context.jobNodePageRefresh.jobNodeData,
                                            privileges: [JobNodePrivilege.MANAGER, type == QueueTypes.JOBS_QUEUE ?  
                                                JobNodePrivilege.SCRIPTER
                                                :
                                                JobNodePrivilege.TESTER
                                            ]
                                        }}
                                    >
                                        <option value={type}>{type}</option>
                                    </SecuredNode>)}
                                </Field>
                                <ErrorMessage className="text-danger" name="chosenQueueType" component="div"/>
                            </div>


                            <div>
                                <strong><label htmlFor="name">Name:</label></strong>
                                <Field className="form-control m-2" name="name" id="name"/>
                                <ErrorMessage className="text-danger" name="name" component="div"/>
                            </div>
                            

                            <div>
                                <strong><label htmlFor="description">Description:</label></strong>
                                <Field className="form-control m-2" name="description" id="description" as="textarea"/>
                                <ErrorMessage className="text-danger" name="description" component="div"/>
                            </div>
                                
                            <div>
                                <strong><label htmlFor="configuration">Configuration:</label></strong>
                                <Field validate={validateJsonString} className="form-control m-2" name="configuration" id="configuration" as="textarea"/>
                                <ErrorMessage className="text-danger" name="configuration" component="div"/>
                            </div>
                          
                            <button className="btn btn-success m-2" type="submit">Add</button>

                        </Form>
                    )}
                </Formik>
            </SecuredNode>
    
        </div>
    );
}

export default JobEntityCreator;