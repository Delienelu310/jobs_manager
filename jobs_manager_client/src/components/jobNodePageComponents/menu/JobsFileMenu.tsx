import { useEffect, useState } from "react";
import { checkJobsFileState, deleteJobsFile, JobsFileDetails, JobsFileExtension, JobsFileSimple, JobsFileState, retrieveJobsFile, updateJobsFileDetails, updateJobsFileFile } from "../../../api/ilum_resources/jobsFilesApi";
import { JobNodePageRefresh, JobNodeResourceListsMembers } from "../../../pages/JobNodePage";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import { NotificationType, useNotificator } from "../../notifications/Notificator";
import * as Yup from 'yup';


export interface JobsFileMenuContext{
    jobNodePageRefresh : JobNodePageRefresh
}

export interface JobsFileMenuArgs{
    data : JobsFileSimple,
    context : JobsFileMenuContext
}


const UpdateDetailsValidationSchema = Yup.object({
    name : Yup.string()
        .min(3)
        .max(50)
        .required()
    ,
    description : Yup.string()
        .nullable()
        .min(3)
        .max(500)
        
});

const JobsFileMenu = ({data, context} : JobsFileMenuArgs) => {


    const {pushNotification, catchRequestError} = useNotificator();

    const [fullData, setFullData] = useState<JobsFileSimple | null>(null);

    
    const [newFile, setNewFile] = useState<File | null>(null);
    const [newExtension, setNewExtension] = useState<string>(JobsFileExtension.JAR);
    const [state, setState] = useState<JobsFileState>(JobsFileState.UNKNOWN);

    function refresh(){
        retrieveJobsFile(data.project.id, data.jobNode.id, data.id)
            .then(response => setFullData(response.data))
            .catch(catchRequestError);
    }

    function checkState(){
        checkJobsFileState(data.project.id, data.jobNode.id, data.id)
            .then(response => {
                console.log(response.data);
                setState(response.data)
            })
            .catch(catchRequestError);
    }

    function deleteJob(){
        deleteJobsFile(data.project.id, data.jobNode.id, data.id)
            .then(r => {
                context.jobNodePageRefresh.setMenu(null);
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                pushNotification({
                    message: "Jobs File was deleted",
                    time : 5,
                    type: NotificationType.INFO
                })
                context.jobNodePageRefresh.setMenu(null);
                
            })
            .catch(catchRequestError);
    }

    function updateDetails(newJobsFileDetails : JobsFileDetails){
        updateJobsFileDetails(data.project.id, data.jobNode.id, data.id, newJobsFileDetails)
            .then(response => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }
                pushNotification({
                    message: "Jobs File details was updated",
                    time : 5,
                    type: NotificationType.INFO
                })
            }).catch(catchRequestError);
    }

    function updateFile(){
        if(!newFile) return;

        updateJobsFileFile(data.project.id, data.jobNode.id, data.id, newExtension, newFile)
            .then(r => {
                refresh();
                if(
                    context.jobNodePageRefresh.chosenResourceList &&
                    context.jobNodePageRefresh.chosenResourceList.label == JobNodeResourceListsMembers.JOBS_FILES
                ){
                    context.jobNodePageRefresh.chosenResourceList.setDependency(Math.random());
                }

                pushNotification({
                    message: "Jobs File was updated",
                    time : 5,
                    type: NotificationType.INFO
                })
            }).catch(catchRequestError);
    }


    useEffect(() => {
        refresh();
    }, []);

    return (
        <div>
            {fullData == null ?
                <h3>Loading...</h3>
                :
                <div>
                    <h3>Jobs File</h3>
                    <hr/>

                    <div>
                        <h5>About:</h5>

                        <strong>Name: {fullData.jobDetails.name}</strong>
                        <strong>Jobs File ID: {fullData.id}</strong>
                        <br/>
                        <strong>Author:</strong> {fullData.publisher.username}
                        <br/>
                        <OpenerComponent
                            closedLabel={<strong>Classes Used:</strong>}
                            openedElement={<>{fullData.allClasses.map(className => <><i>{className}</i> <br/> </>)}</>}
                        />
                        
                        <br/>
                       
                    </div>
                    

                    <hr/>

                    <strong>Current State: {state}</strong>
                    <br/>
                    <button className="btn btn-primary m-3" onClick={e => checkState()}>Check state</button>

               
                    <SecuredNode
                        moderator={true}
                        projectPrivilegeConfig={null}
                        alternative={null}
                        jobNodePrivilegeConfig={{
                            jobNode: context.jobNodePageRefresh.jobNodeData,
                            privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.SCRIPTER]
                        }}
                        roles={null}
                    >
                        <hr/>
                        
                        
                        <strong>New File:</strong>
                        <input className="form-control m-2" type="file" onChange={e => {
                            if(e.target.files && e.target.files[0]){
                                setNewFile(e.target.files[0]);
                            }
                        }}/>
                        <strong>Extension:</strong>
                        <select className="form-control m-2" value={newExtension} onChange={e => setNewExtension(e.target.value)}>
                            {Object.values(JobsFileExtension).map(type => <option value={type}>{type}</option>)}
                        </select>

                        <button className="btn btn-success m-3" onClick={e => updateFile()}>Update file</button>


                        <hr/>

                        <Formik
                            initialValues={{
                                name : data.jobDetails.name,
                                description : data.jobDetails.description || null 
                            }}
                            validationSchema={UpdateDetailsValidationSchema}
                            onSubmit={updateDetails}
                        >
                            {() => (
                                <Form>
                                    <div>
                                        <strong><label htmlFor="name">Name:</label></strong>
                                        <Field name="name" id="name" className="form-control m-2"/>
                                        <ErrorMessage component="div" name="name" className="text-danger"/>
                                    </div>
                                    <div>
                                        <strong><label htmlFor="description">Description:</label></strong>
                                        <Field name="description" id="description" className="form-control m-2" as="textarea"/>
                                        <ErrorMessage component="div" name="description" className="text-danger"/>

                                    </div>

                                   

                                    <button type="submit" className="btn btn-success m-3">Update details</button>
                                    
                                </Form>
                            )}
                        </Formik>

                        <hr/>
                        <button className="btn btn-danger m-3" onClick={e => deleteJob()}>Delete</button>

                        
                    </SecuredNode>
                   
                </div>

            }
                    
        </div>
    );

}


export default  JobsFileMenu;