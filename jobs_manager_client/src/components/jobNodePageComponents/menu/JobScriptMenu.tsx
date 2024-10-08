import {  useEffect, useState } from "react";
import { deleteJobScript, JobScriptDetails, JobScriptSimple, retreiveJobScript, updateJobScriptDetails } from "../../../api/ilum_resources/jobScriptsApi";
import List, { SourceArg, SourceCountArg } from "../../lists/List";
import JobsFileRemoveElement, { JobsFileRemoveElementContext } from "./JobsFileRemoveElement";
import { JobsFileSimple } from "../../../api/ilum_resources/jobsFilesApi";
import { FieldType } from "../../lists/Filter";
import ServerBoundList from "../../lists/ServerBoundList";
import JobsFileAddElement, { JobsFileAddElementContext } from "./JobsFileAddElement";
import JobEntityCreator from "./JobEntityCreator";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from 'yup';
import { NotificationType, useNotificator } from "../../notifications/Notificator";

export interface JobScriptMenuContext{
    jobNodePageRefresh : JobNodePageRefresh,
}

export interface JobScriptMenu{
    data : string,
    context : JobScriptMenuContext,
}


const JobScriptMenu = ({
    data, 
    context
} : JobScriptMenu) => {


    const {pushNotification, catchRequestError} = useNotificator();
    
    const [actualData, setActualData] = useState<JobScriptSimple | null>(null);
    
  
    function refresh(){
        retreiveJobScript(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data)
            .then(response => {
                setActualData(response.data);
            })
            .catch(catchRequestError);
    }

    function deleteJobScriptElement(){
        deleteJobScript(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }
                pushNotification({
                    message: "Job Script was deleted successfully",
                    time : 5,
                    type : NotificationType.INFO
                })
                
            }).catch(catchRequestError);   
    }

    function updateDetails(newDetails : JobScriptDetails){
        updateJobScriptDetails(context.jobNodePageRefresh.projectId, context.jobNodePageRefresh.jobNodeId, data, newDetails)
            .then(r => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_SCRIPTS
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random);
                }

                pushNotification({
                    message: "Job Script was updated successfully",
                    time : 5,
                    type : NotificationType.INFO
                })
            })
            .catch(catchRequestError);

    }

    function getJobsFilesUsedList({filter, search, pager} : SourceArg) : Promise<JobsFileSimple[]>{

        return new Promise<JobsFileSimple[]>((resolve, reject) => {

            if(!actualData) {
                resolve([]);
                return;
            };
            let result : JobsFileSimple[] = actualData.jobsFiles;
            result = result.filter(jobsFile => jobsFile.jobDetails.name.startsWith(search));
      
            const className : string = (filter.values.get("classname") ?? [""])[0];
            if(className != "") result = result.filter(jobsFile => jobsFile.allClasses.includes(className))

        
            const publisher : string = (filter.values.get("publisher") ?? [""])[0];
            if(publisher != "") result = result.filter(jobsFile => jobsFile.publisher.username == publisher);

            let offset : number = pager.pageSize * pager.pageChosen
    
            resolve(result
                .filter((jobsFile, index) => index >= offset && index < offset + pager.pageSize)
            );
        });
    }

    function getJobsFilesUsedCount({filter, search} : SourceCountArg): Promise<number>{

        return new Promise<number>((resolve, reject) => {
            
            if(!actualData){
                resolve(0);
                return;
            }

            let result : JobsFileSimple[] = actualData?.jobsFiles;
            result = result.filter(jobsFile => jobsFile.jobDetails.name.startsWith(search));

            const className : string = (filter.values.get("classname") ?? [""])[0];
            if(className != "") result = result.filter(jobsFile => jobsFile.allClasses.includes(className))

        
            const publisher : string = (filter.values.get("publisher") ?? [""])[0];
            if(publisher != "") result = result.filter(jobsFile => jobsFile.publisher.username == publisher);
            
            resolve(result.length);
        });
    }
    

    
    useEffect(() => {
        refresh();
    }, []);

    return (
        <>
            {actualData ? 
                <div>

                    <h3>Job Script Menu</h3>

                    <hr/>

                    <div className="m-3">
                        <h5 className="m-3">About:</h5>

                        <strong>Name:</strong> {actualData.jobScriptDetails.name}
                        <br/>
                        <strong>Description:</strong>
                        <p>{actualData.jobScriptDetails.description || "Description is not specified"}</p>
                        <strong>ID: {actualData.id}</strong>
                        <br/>
                        <strong>Author : </strong>{actualData.author.username}
                        <br/>
                        <strong>Extension:</strong> {actualData.extension}
                        <br/>
                        <strong>Class full name:</strong>
                         <i>{actualData.classFullName}</i>
                    </div>

                    <hr/>
                   

                    <div>
                        <OpenerComponent
                            closedLabel={ <h5>Show Dependencies</h5>}
                            openedElement={
                                <>            
                                    <h5>Jobs Files used:</h5>
                                    <List<JobsFileSimple, JobsFileRemoveElementContext>
                                        Wrapper={JobsFileRemoveElement}
                                        pager={{defaultPageSize : 10}}
                                        source={{
                                            sourceData: getJobsFilesUsedList,
                                            sourceCount: getJobsFilesUsedCount,
                                            catchCount : e => console.log(e),
                                            catchData: e => console.log(e)
                                            
                                        }}
                                        context={{
                                            jobNodePageRefresh : context.jobNodePageRefresh,
                                            refreshJobScript: refresh,
                                            jobScript : actualData
                                        }}
                                        dependencies={[actualData]}
                                        filter={{parameters: [
                                            {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                                            {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                                        ]}}
                                    />
                                </>
                            }
                        />
                    </div>
                   

                    <hr/>
                    
                    <SecuredNode
                        projectPrivilegeConfig={null}
                        roles={null}
                        moderator={true}
                        alternative={null}
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
                        }}
                    >
                        <Formik
                            initialValues={{
                                name: actualData.jobScriptDetails.name,
                                description : actualData.jobScriptDetails.description || null
                            }}
                            onSubmit={values => updateDetails({name : values.name, description : values.description})}
                            validationSchema={Yup.object({
                                name : Yup.string()
                                    .min(3)
                                    .max(50)
                                    .required()
                                    .nonNullable(),
                                description: Yup.string()
                                    .min(3)
                                    .max(500)
                                    .nullable()
                            })}

                        >
                            {() => (
                                <Form>
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

                                    <button className="btn btn-success m-2" type="submit">Update details</button>
                                </Form>
                            )}
                           
                    
                        </Formik>

                        <hr/>

                        <div>
                            <OpenerComponent
                                closedLabel={<h5>Add Dependencies</h5>}
                                openedElement={
                                    <div>
                                        <h5 className="m-2">Add Dependencies: </h5>
                                        <ServerBoundList<JobsFileSimple, JobsFileAddElementContext>
                                            endpoint={{
                                                resourse: `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}/jobs_files?`,
                                                count :  `/projects/${context.jobNodePageRefresh.projectId}/job_nodes/${context.jobNodePageRefresh.jobNodeId}/jobs_files/count?`
                                            }}
                                            Wrapper={JobsFileAddElement}
                                            pager={{defaultPageSize: 10}}
                                            context={{
                                                jobNodePageRefresh : context.jobNodePageRefresh,
                                                refreshJobScript: refresh,
                                                jobScript : actualData
                                            }}
                                            dependencies={[]}
                                            filter={{parameters: [
                                                {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                                                {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                                                {label: "extension", additionalData: ["py", "jar"], fieldType: FieldType.SingleSelection}
                                            ]}}
                                        />
                                    </div>
                                }
                            />

                        </div>
                        
                        <hr/>

                        <h5>Actions: </h5>

                        <button className="btn btn-success m-2" onClick={e => context.jobNodePageRefresh.setMenu(<JobEntityCreator
                            context={{
                                jobNodePageRefresh : context.jobNodePageRefresh
                            }}
                            projectId={context.jobNodePageRefresh.projectId}
                            jobNodeId={context.jobNodePageRefresh.jobNodeId}
                            jobScriptId={data}
                        />)}>Add to Queue</button>

                        <br/>

                        <button className="btn btn-danger m-2" onClick={deleteJobScriptElement}>Delete</button>
                        <br/>
            
                    </SecuredNode>

                    
          

                </div>
                :
                <h4>Loading...</h4>
            }
        
        </>
        
    );
}

export default JobScriptMenu;