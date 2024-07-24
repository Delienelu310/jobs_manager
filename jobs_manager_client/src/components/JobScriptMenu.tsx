import { useEffect, useState } from "react";
import { deleteJobScript, JobScriptDetails, JobScriptSimple, retreiveJobScript, updateJobScriptDetails } from "../api/ilum_resources/jobScriptsApi";
import List, { SourceArg, SourceCountArg } from "./lists/List";
import JobsFileRemoveElement, { JobsFileRemoveElementContext } from "./lists/listElements/JobsFileRemoveElement";
import { JobsFileSimple } from "../api/ilum_resources/jobsFilesApi";
import { FieldType } from "./lists/Filter";



export interface JobScriptMenu{
    data : JobScriptSimple,
    setMenu : React.Dispatch<React.SetStateAction<JSX.Element | null>>,
    setJobSciptsListDependency :  React.Dispatch<React.SetStateAction<number>>,
    setJobsFileListDependency : React.Dispatch<React.SetStateAction<number>>

}





const JobScriptMenu = ({data, setMenu, setJobSciptsListDependency, setJobsFileListDependency} : JobScriptMenu) => {
    
    const [actualData, setActualData] = useState<JobScriptSimple | null>(null);
    
    const [newDetails, setNewDetails] = useState<JobScriptDetails>({
        name : ""
    });

    const [jobSearchListOpened, setJobSearchListOpened] = useState<boolean>(false);

    function refresh(){
        retreiveJobScript(data.project.id, data.jobNode.id, data.id)
            .then(response => {
                setActualData(response.data);
            })
            .catch(e => console.log(e));
    }

    function deleteJobScriptElement(){
        deleteJobScript(data.project.id, data.jobNode.id, data.id)
            .then(r => {
                setMenu(null);
                setJobSciptsListDependency(Math.random);
            })
            .catch(e => console.log(e));   
    }

    function updateDetails(){
        updateJobScriptDetails(data.project.id, data.jobNode.id, data.id, newDetails)
            .then(r => {
                refresh();
                setJobSciptsListDependency(Math.random());
            })
            .catch(e => console.log(e));

    }

    function getJobsFilesUsedList({filter, search, pager} : SourceArg) : Promise<JobsFileSimple[]>{

        return new Promise<JobsFileSimple[]>(() => {
            if(!actualData) return [];

            let classNames : string[] | undefined = filter.values.get("classname");
            if(!classNames || !classNames[0]) return [];
            const className : string = classNames[0];

            let publisherVal : string[] | undefined = filter.values.get("publisher");
            if(!publisherVal || publisherVal[0]) return [];
            const publisher : string = publisherVal[0];

            let offset : number = pager.pageSize * pager.pageChosen
            
            return actualData.jobsFiles
                .filter(jobsFile => jobsFile.jobDetails.name.startsWith(search))
                .filter(jobsFile => jobsFile.allClasses.includes(className))
                .filter(jobsFile => jobsFile.publisher.username == publisher)
                .filter((jobsFile, index) => index >= offset && index < offset + pager.pageSize)
        });
    }

    function getJobsFilesUsedCount({filter, search} : SourceCountArg): Promise<number>{

        return new Promise<number>(() => {
            if(!actualData) return 0;

            let classNames : string[] | undefined = filter.values.get("classname");
            if(!classNames || !classNames[0]) return 0;
            const className : string = classNames[0];

            let publisherVal : string[] | undefined = filter.values.get("publisher");
            if(!publisherVal || publisherVal[0]) return 0;
            const publisher : string = publisherVal[0];

            
            return actualData.jobsFiles
                .filter(jobsFile => jobsFile.jobDetails.name.startsWith(search))
                .filter(jobsFile => jobsFile.allClasses.includes(className))
                .filter(jobsFile => jobsFile.publisher.username == publisher)
                .length
        });
    }
    

    
    useEffect(() => {
        refresh();
    }, []);

    return (
        <>
            {actualData ? 
                <div>

                    {/* current job script data: */}
                    <h3>{actualData.jobScriptDetails.name}</h3>
                    <span>ID: {actualData.id}</span>
                    <br/>
                    <span>Author : {actualData.author.username}</span>
                    <br/>
                    <strong>Extension: {actualData.extension}</strong>
                    <br/>
                    <h4>Class full name:</h4>
                    <strong>{actualData.classFullName}</strong>
                    <br/>


                    {/* update job script details */}
                    <label>
                        New name:
                        <input value={newDetails.name} onChange={e => setNewDetails({...newDetails, name : e.target.value})}/>
                    </label>
                    <br/>
                    <button className="btn btn-success" onClick={updateDetails}>Update details</button>


                    {/* delete job script */}

                    <button className="btn btn-danger" onClick={deleteJobScriptElement}>Delete</button>

                    {/* browse the list of jobs files not used by job script and add it */}
                   
                    <button className="btn btn-primary">{jobSearchListOpened ? "Close" : "Add jobs file"}</button>
                    
                    {jobSearchListOpened && <></>}



                    {/* browse the list of jobs files used */}

                    <h4>Jobs Files used:</h4>
                    <List<JobsFileSimple, JobsFileRemoveElementContext>
                        Wrapper={JobsFileRemoveElement}
                        pager={{defaultPageSize : 10}}
                        source={{
                            sourceData: getJobsFilesUsedList,
                            sourceCount: getJobsFilesUsedCount
                            
                        }}
                        context={{
                            setMenu: setMenu,
                            setJobsFileListDependency: setJobsFileListDependency,
                            refreshJobScript: refresh
                        }}
                        dependencies={[]}
                        filter={{parameters: [
                            {label: "publisher", additionalData: [], fieldType: FieldType.SingleInput},
                            {label: "classname", additionalData: [], fieldType: FieldType.SingleInput},
                        ]}}
                    />





                </div>
                :
                <h4>Loading...</h4>
            }
        
        </>
        
    );
}

export default JobScriptMenu;