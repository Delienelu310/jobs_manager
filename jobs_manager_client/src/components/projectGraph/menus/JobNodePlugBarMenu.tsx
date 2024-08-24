import { useEffect, useState } from "react";
import { PlugBarElement } from "../gof/PlugBarElement";
import { addJobNodePlug } from "../../../api/abstraction/jobNodeApi";
import { JobNodeElement } from "../gof/JobNodeElement";
import { ChannelList, ChannelTypes } from "../../../api/abstraction/projectApi";
import SecuredNode from "../../../authentication/SecuredNode";
import OpenerComponent from "../../OpenerComponent";
import { JobNodePrivilege, ProjectPrivilege } from "../../../api/authorization/privilegesApi";
import { ErrorMessage, Field, Form, Formik } from "formik";
import * as Yup from "yup"
import { NotificationType } from "../../notifications/Notificator";

export interface JobNodePlugBarMenu{
    element : PlugBarElement
}

const LabelValidation = Yup.object({
    label : Yup.string()
        .required("The label must be specified")
        .min(1, "The length of label must be between 3 and 20")
        .max(20, "The length of label must be between 3 and 20")
});


const JobNodePlugBarMenu = ({element} : JobNodePlugBarMenu) => {
    
    const [plugs, setPlugs] = useState<{[key:string] : ChannelList}>({});

    function refresh(){
        setPlugs(
            element.getOrientation() ? 
                (element.getParent() as JobNodeElement).getData().output
                :
                (element.getParent() as JobNodeElement).getData().input
        )
    }

    useEffect(refresh, [element]);

    return (
        <div>
            <h3>Job Node {element.getOrientation() ? "Outputs" : "Inputs"} menu</h3>

            <SecuredNode
                alternative={<h3>What ht ehell</h3>}
                roles={null}
                moderator
                projectPrivilegeConfig={{
                    project: element.getGof().getContext().projectData,
                    privileges: [ProjectPrivilege.ADMIN, ProjectPrivilege.ARCHITECT, ProjectPrivilege.MODERATOR]
                }}
                jobNodePrivilegeConfig={{
                    jobNode: (element.getParent() as JobNodeElement).getData(),
                    privileges: [JobNodePrivilege.MANAGER]
                }}
            >
                <OpenerComponent
                    closedLabel={<h5>Add {element.getOrientation() ? "Output" : "Input"}</h5>}
                    openedElement={
                        <Formik
                            initialValues={{
                                label : ""
                            }}
                            onSubmit={(values) => {
                                addJobNodePlug(
                                    element.getGof().getContext().projectData.id, 
                                    (element.getParent() as JobNodeElement ).getData().id, 
                                    element.getOrientation(), 
                                    values.label
                                ).then(respone => {
                                    element.getGof().getContext().refresh()
                                    refresh();

                                    element.getGof().getContext().pushNotification({
                                        message: "Label was added",
                                        time: 3,
                                        type: NotificationType.INFO
                                    })
                                })
                                .catch(element.getGof().getContext().catchRequestError)
                            }}
                            validationSchema={LabelValidation}
                        >
                            {() => (
                                <Form>
                                    <div>
                                        <strong><label htmlFor="label">Label: </label></strong>
                                        <Field name="label" id="label" className="form-control m-2"/>
                                        <ErrorMessage name="label" component="div" className="text-danger"/>
                                    </div>
                                    


                                    <button type="submit" className="btn btn-success m-2">Add plug</button>
                                </Form>
                            )}
                            
                        </Formik>
                    }
                />
                <hr/>
            </SecuredNode>
                
          
            <h5>Labels list:</h5>

            <button className="btn btn-primary m-2" onClick={refresh}>Refresh</button>
            
            {Object.entries(plugs).map( ([key, channelList]) => ( <div style={{
                margin: "30px 15%", 
                borderBottom: "3px solid black",
                borderTop: "3px solid black",
            }}>
                <h5>{key}</h5>
              
                {channelList.channelList.map( (channelData) => (
                    
                    <div style={{
                        margin: "20px 15%",
                        borderBottom: "2px solid black"
                    }}>
                        <strong>Channel: {channelData.channelDetails.name}</strong>
                        <br/>
                        <strong>Type : {channelData.channelDetails.type}</strong>
                        <br/>
                        <strong>Header : {channelData.channelDetails.headers.join(", ")}</strong>
                    </div>
                ))}

            </div>))}
        </div>
    );
}


export default JobNodePlugBarMenu;