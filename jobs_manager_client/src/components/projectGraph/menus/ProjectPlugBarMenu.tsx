
import { useState } from "react";
import { addProjectPlug, removeProjectPlug } from "../../../api/abstraction/projectApi";

import {  ChannelTypes} from "../../../api/abstraction/projectApi";
import OpenerComponent from "../../OpenerComponent";
import SecuredNode from "../../../authentication/SecuredNode";
import { ProjectPrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, FieldArray, Form, Formik } from "formik";
import { PlugBarElement } from "../gof/PlugBarElement";

import * as Yup from 'yup';
import { NotificationType } from "../../notifications/Notificator";

export interface ProjectPlugBarArgs{
    
    plugBarElement : PlugBarElement
}


const HeaderAdditionSchema = Yup.object({
    newHeader: Yup.string()
        .test('is-valid-header', "Invalid Header" , value => {


            if(!value || !value.trim()) return false;
            value = value.trim()

            if(value.length < 1 || value.length > 20) return false;
            if(Number.isInteger(value.charAt(0))) return false;

            return true;
        })
})

const PlugAdditionSchema = Yup.object({
    label : Yup.string()
        .required("Label must be specified")
        .nonNullable("Label cannot be blank")
        .min(3, "Label size must be 3 to 20")
        .max(20, "Label size must be 3 to 20")
    ,
    channelName : Yup.string()
        .required("Channel Name must be specified")
        .nonNullable("Channel Name cannot be blank")
        .min(3, "Channel Name size must be 3 to 50")
        .max(50, "Channel Name size must be 3 to 50"),
    headers: Yup.array()
        .min(1, "Headers count must be from 1 to 20")
        .max(20, "Headers count must be from 1 to 20"),
})

const ProjectPlugBarMenu = ({plugBarElement} : ProjectPlugBarArgs) => {

    const [newHeader, setNewHeader] = useState<string>("");

    return (
        <div style={{
            margin: "30px 15%"
        }}>
            
            <h3>Project {plugBarElement.getOrientation() ? "outputs" : "inputs"} bar</h3>

            <SecuredNode
                alternative={null}
                jobNodePrivilegeConfig={null}
                roles={null}
                moderator
                projectPrivilegeConfig={{
                    project: plugBarElement.getGof().getContext().projectData,
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT]
                }}
            >
                <OpenerComponent
                    closedLabel={<h5>Add new label</h5>}
                    openedElement={
                        <Formik
                            initialValues={{
                                label: "",
                                channelName : "",
                                type: ChannelTypes.MINIO,
                                headers: [] as string[]
                            }}
                            validationSchema={PlugAdditionSchema}
                            onSubmit={(values) => {
                                addProjectPlug(
                                    plugBarElement.getGof().getContext().projectData.id, 
                                    plugBarElement.getOrientation(),
                                    values.label, 
                                    {
                                        name: values.channelName,
                                        type: values.type,
                                        headers: values.headers,
                                    }
                                ).then(respone => plugBarElement.getGof().getContext().refresh())
                                .catch(plugBarElement.getGof().getContext().catchRequestError)
                            }}
                        >
                            {({values, setFieldValue}) => (
                                <Form>

                                    <div>
                                        <strong><label htmlFor="label">Label:</label> </strong>
                                        <Field className="form-control m-2" name="label" id="label"/>
                                        <ErrorMessage component="div" name="label" className="text-danger"/>
                                    </div>

                                    <div>
                                        <strong><label htmlFor="channelName">Channel Name:</label> </strong>
                                        <Field className="form-control m-2" name="channelName" id="channelName"/>
                                        <ErrorMessage component="div" name="channelName" className="text-danger"/>
                                    </div>
                                    
                                    <div>
                                        <strong><label htmlFor="type">Channel type:</label></strong>
                                        <select className="form-control m-2" name="type" id="type">
                                            {Object.values(ChannelTypes).map(key => <option value={key}>{key}</option>)}
                                        </select>
                                    </div>

                                    <div>
                                        <h5><label>Headers:</label> </h5>

                                        <FieldArray name="headers">
                                            {({remove, push}) => (
                                                <div>
                                                    {values.headers.map((header, index) => 
                                                        <div key={index + "_" + header} style={{margin: "20px 15%", height: "40px", borderBottom: "1px solid black"}}>
                                                            <span style={{fontSize: "20ps"}}>{header} </span>


                                                            <button type="button" style={{float:"right"}} className="btn btn-primary" onClick={e => {
                                                                if(index == 0) return
                                                                
                                                                const newHeaders = [...values.headers];
                                                                
                                                                const previousHeader = newHeaders[index - 1];
                                                                newHeaders[index - 1] = newHeaders[index]
                                                                newHeaders[index] = previousHeader
                                                                
                                                                
                                                                setFieldValue("headers", newHeaders);


                                                            }}>Up</button>
                                                            <button type="button" style={{float:"right"}} className="btn btn-primary" onClick={() => {
                                                                  if(index == values.headers.length - 1) return
                                                                
                                                                  const newHeaders = [...values.headers];
                                                                  
                                                                  const nextHeader = newHeaders[index + 1];
                                                                  newHeaders[index + 1] = newHeaders[index]
                                                                  newHeaders[index] = nextHeader
                                                                  
                                                                  setFieldValue("headers", newHeaders);
                                                            }}>Down</button>
                                                            <button type="button" style={{float:"right"}} className="btn btn-danger" onClick={e => remove(index)}>X</button>
                                                        </div>
                                                    )} 

                                                    
                                                    <div>
                                                        <strong>Add new Header:</strong>
                                                        <input value={newHeader} onChange={e => setNewHeader(e.target.value)} className="form-control m-2"/>
                                                    
                                                        <button type="button" className="btn btn-primary m-2" onClick={e => {
                                                          
                                                            HeaderAdditionSchema.validateAt("newHeader", {newHeader : newHeader})
                                                                .then(r => push(newHeader))
                                                                .catch(e => {
                                                                    if(e instanceof Yup.ValidationError){
                                                                        plugBarElement.getGof().getContext().pushNotification({
                                                                            message: "Exception: " + (e as Yup.ValidationError).message,
                                                                            type: NotificationType.ERROR,
                                                                            time: 5
                                                                        })
                                                                    }else{
                                                                        plugBarElement.getGof().getContext().pushNotification({
                                                                            message: "Unexpeted exception during header validation",
                                                                            type: NotificationType.ERROR,
                                                                            time: 5
                                                                        })
                                                                    }
                                                                })
                                                        }}>Add header</button>
                                                    </div>
                                                        
                                                   
                                                   
                                                </div>
                                            )}
                                        </FieldArray>
                                        
                                       
                                    </div>
                        
                                    <h5>Actions:</h5>
                    
                                    <button type="submit" className="btn btn-success m-3" onClick={e => {
                                        
                                    }}>Add plug</button>
                                </Form>
                            )}
                          
                        </Formik>
                    }
                />
                
                <hr/>
            </SecuredNode>

            <h5>Labels</h5>
            <hr/>

            {Object.entries(plugBarElement.getOrientation() ? 
                plugBarElement.getGof().getContext().projectData.outputChannels : 
                plugBarElement.getGof().getContext().projectData.inputChannels
            ).map( ([key, channel]) => ( 
                <div>
                    <h5>{key}</h5>
                    <div>
                        <h5 className="m-3">Channel: </h5>
                        <strong>Name:</strong>{channel.channelDetails.name}
                        <br/>
                        <strong>Type : {channel.channelDetails.type}</strong>
                        <br/>
                        <strong>Header : {channel.channelDetails.headers.join(", ")}</strong>
                        <br/>

                        {channel.inputJobs && channel.inputJobs.length > 0 && <>
                           
                            <h5 className="m-3">Input Jobs:</h5>
                            {channel.inputJobs.map(job => 
                                <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                                    {job.jobNodeDetails.name}
                                    <br/>
                                    {job.id} 
                                </div>
                            )}
                        </>}
                       
                        {channel.outputJobs && channel.outputJobs.length > 0 && <>
                     
                            <h5 className="m-3">Output Jobs:</h5>
                            {channel.outputJobs.map(job => 
                                <div style={{margin: "20px 20%", borderBottom: "1px solid black"}}>
                                    {job.jobNodeDetails.name}
                                    <br/>
                                    {job.id} 
                                </div>
                            )}
                        </>}
                        
                       
                    </div>  
                    <button className="btn btn-danger m-3" onClick={e => 
                        removeProjectPlug(plugBarElement.getGof().getContext().projectData.id, plugBarElement.getOrientation(), key)
                            .then(response => plugBarElement.getGof().getContext().refresh())
                            .catch(e => console.log(e))
                    }>Delete Label</button>
                    <hr/>
                </div>
            ) )} 
            
        </div>
    );

}

export default ProjectPlugBarMenu;