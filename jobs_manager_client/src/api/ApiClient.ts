import axios from "axios";
import { SourceArg, SourceCountArg } from "../components/lists/List";

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_JOBS_MANAGER_API || "http://localhost:8080"
});


export default apiClient;

//used in lists

export function convertSourceArgsToRequestParams(arg : SourceArg){
    let result : string = 
        `query=${arg.search}&` + 
        `pageSize=${arg.pager.pageSize}&` + 
        `pageNumber=${arg.pager.pageChosen}&` +
        Array.from(arg.filter.parameters.entries()).map(([label, fieldValue]) => `${label}=${(arg.filter.values.get(label) ?? [] ).join(",")}`).join("&");

    return result;
}

export function convertSourceCountArgsToRequestParams(arg : SourceCountArg){
    let result : string = 
        `query=${arg.search}&` + 
        Array.from(arg.filter.parameters.entries()).map(([label, fieldValue]) => `${label}=${(arg.filter.values.get(label) ?? [] ).join(",")}`).join("&");

    return result;
}
