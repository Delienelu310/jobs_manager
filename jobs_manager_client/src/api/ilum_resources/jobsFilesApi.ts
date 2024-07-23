import { AppUserSimple } from "../authorization/users"

export interface JobsFileSimple{
    id : string,
    extension : string,
    jobDetails : {
        name : string,
        description : string
    },
    allClasses : string[],
    project : any,
    jobNode : any,
    publisher : AppUserSimple

}

