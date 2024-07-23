import apiClient, { convertSourceArgsToRequestParams, convertSourceCountArgsToRequestParams } from "../../api/ApiClient"
import List, { Field, SourceArg, SourceCountArg, WrapperProps } from "./List"


export interface ServerBoundListProps<Data>{
    pager : {
        defaultPageSize : number
    },
    filter : {
        parameters: Field[]
    }

    Wrapper : React.FC<WrapperProps<Data>>,
    endpoint: {
        resourse : string,
        count : string
    }
} 



const ServerBoundList = <Data,>(
    props : ServerBoundListProps<Data>
) => {

    async function sourceData(arg : SourceArg) : Promise<Data[]>{
        console.log(convertSourceArgsToRequestParams(arg));
        return apiClient.get(props.endpoint.resourse + "?" + convertSourceArgsToRequestParams(arg)).then( response=> response.data);
    }

    async function sourceCount(arg: SourceCountArg) : Promise<number>{
        return apiClient.get(props.endpoint.count + "?" + convertSourceCountArgsToRequestParams(arg)).then(response => response.data)
    }

    return (
        <div>
            <List<Data>
                filter={props.filter}
                pager={props.pager}
                Wrapper={props.Wrapper}
                source={{
                    sourceData: sourceData,
                    sourceCount: sourceCount
                }}
            />
        </div>
    );
}

export default ServerBoundList;