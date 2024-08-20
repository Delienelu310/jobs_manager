import "../../../css/components/jobNodePageComponent/jobsFileList/jobsFileUploader.css"

import { JobsFileExtension, retrieveJobsFile, uploadJobsFile } from "../../../api/ilum_resources/jobsFilesApi";
import SecuredNode from "../../../authentication/SecuredNode";
import { JobNodePageRefresh } from "../../../pages/JobNodePage";
import { JobNodePrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from 'yup';
import { NotificationType, useNotificator } from "../../notifications/Notificator";
import JobsFileMenu from "../menu/JobsFileMenu";

export interface JobsFileUplaoderArgs{
    projectId : string,
    jobNodeId : string,
    jobNodePageRefresh : JobNodePageRefresh
}

const FileUploaderValidationScheme = Yup.object({
    name : Yup.string()
        .required()
        .min(3)
        .max(50),
    description : Yup.string()
        .nullable()
        .min(3)
        .max(500),
    file : Yup.mixed()
        .required()
        .nonNullable()
});

const JobsFileUploader = ({projectId, jobNodeId, jobNodePageRefresh} : JobsFileUplaoderArgs) => {

    const {catchRequestError, pushNotification} = useNotificator();

    return (
        <SecuredNode
            roles={null}
            projectPrivilegeConfig={null}
            alternative={<h5>You are not privileged to upload files</h5>}
            moderator
            jobNodePrivilegeConfig={{
                jobNode: jobNodePageRefresh.jobNodeData,
                privileges: [JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER]
            }}
        >
            <div className="jobs_file_uploader">
                <Formik
                    initialValues={{
                        name : "",
                        extension: JobsFileExtension.JAR,
                        description : null as (string | null),
                        file : null as (File | null)
                    }}
                    validationSchema={FileUploaderValidationScheme}
                    onSubmit={(values) => {
                        if(values.file == null) return;
                        uploadJobsFile(projectId, jobNodeId, values.extension, {name : values.name, description : values.description || null}, values.file)
                            .then(r => {
                                pushNotification({
                                    message: "Jobs File was created successfully. Id: " + r.data,
                                    time : 5,
                                    type : NotificationType.INFO
                                });
                                retrieveJobsFile(projectId, jobNodeId, r.data)
                                    .then(r => jobNodePageRefresh.setMenu(
                                        <JobsFileMenu
                                            data={r.data}
                                            context={{jobNodePageRefresh}}
                                        />
                                    )).catch(catchRequestError);

                            }).catch(catchRequestError)
                        ;
                    }}
                >
                    {({values, setFieldValue}) => (
                        <Form>
                            <div>
                                <strong><label htmlFor="name">Name: </label></strong> 
                                <Field className="form-control m-2" name="name" id="name"/>
                                <ErrorMessage component="div" name="name" className="text-danger"/>
                            </div>

                            <div>
                                <strong><label htmlFor="extension">Extension:</label> </strong>
                                <Field className="form-control m-2" name="extension" id="extensions" as="select">
                                    {Object.values(JobsFileExtension).map(extension => (
                                        <option value={extension}>
                                            {extension}
                                        </option>
                                    ))}
                                </Field>
                            </div>
                        
                            
                            <div>
                                <strong><label htmlFor="description">Description:</label></strong> 
                                <Field name="description" id="description" className="form-control m-2" as="textarea"/>
                                <ErrorMessage name="description" component="div" className="text-danger"/>
                            </div>
                            
                            <div>
                                <strong><label htmlFor="file">JobsFile: </label></strong>
                                <input name="file" className="form-control m-2" type="file"  onChange={e => {
                                    if(!e.target.files) return;
                                    if(!e.target.files[0]) return;

                                    setFieldValue("file", e.target.files[0]);
                                }}/>
                                <ErrorMessage component="div" name="file" className="text-danger"/>
                            </div>
                        
                            
                            
                            <button type="submit" className="btn btn-success" >Upload</button>
                        </Form>
                    )}
                   
                </Formik>
               
            </div>
        </SecuredNode>
     
    );
}


export default JobsFileUploader;