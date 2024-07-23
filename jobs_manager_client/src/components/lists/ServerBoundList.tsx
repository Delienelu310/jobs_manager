import apiClient, { convertSourceArgsToRequestParams, convertSourceCountArgsToRequestParams } from "../../api/ApiClient"
import List, { Field, SourceArg, SourceCountArg, WrapperProps } from "./List"


export interface ServerBoundListProps<Data, Context>{
    pager : {
        defaultPageSize : number
    },
    filter : {
        parameters: Field[]
    }

    Wrapper : React.FC<WrapperProps<Data, Context>>,
    context : Context,
    endpoint: {
        resourse : string,
        count : string
    }
    dependencies : any[]
} 



const ServerBoundList = <Data,Context>(
    props : ServerBoundListProps<Data, Context>
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
            <List<Data, Context>
                filter={props.filter}
                pager={props.pager}
                Wrapper={props.Wrapper}
                source={{
                    sourceData: sourceData,
                    sourceCount: sourceCount
                }}
                context={props.context}
                dependencies={props.dependencies}
            />
        </div>
    );
}

export default ServerBoundList;