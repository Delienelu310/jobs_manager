import Filter, { FieldType } from "./Filter";
import Pager from "./Pager";
import SearchBar from "./SearchBar";

import { FieldValue } from "./Filter";
import React, { useEffect, useState } from "react";



export interface Field{
    label : string,
    fieldType : FieldType,
    additionalData : string[]
}


export interface SourceCountArg{
    filter : {
        parameters : Map<string, FieldValue>
    },
    search : string
}

export interface SourceArg{
    filter : {
        parameters : Map<string, FieldValue>
    },
    search : string,
    pager : {
        pageSize : number,
        pageChosen : number
    }
}

export interface WrapperProps<Data>{
    data : Data
} 

export interface ListProperties<Data>{
    pager : {
        defaultPageSize : number
    },
    filter : {
        parameters: Field[]
    }

    source : {
        sourceCount : (sourceCountArg : SourceCountArg) => Promise<number>,
        sourceData : (sourceArg : SourceArg) => Promise<Data[]>
    }
    Wrapper : React.FC<WrapperProps<Data>>
}




const List = <Data,>({
    pager : {defaultPageSize},
    filter: {parameters},
    source: {sourceCount, sourceData},
    Wrapper 


} : ListProperties<Data>) => {

    //search bar
    const [queue, setQueue] = useState<string>("");

    //pager
    
    const [pageSize, setPageSize] = useState<number>(defaultPageSize);
    const [pageChosen, setPageChosen] = useState<number>(1);

    const [elementsCount, setElementsCount] = useState<number>(0);

    

    //fitler:
    const fields : Map<string, FieldValue> = new Map<string, FieldValue>([]);


    for(let field  of parameters){
        let [val, setter] = useState<string[]>([]);

        let fieldExtended : FieldValue = {
            additionalData: field.additionalData,
            fieldType: field.fieldType,
            setter: setter,
            value: val
        };
        fields.set(field.label, fieldExtended);
    }
    
    
    
    //data
    const [data, setData] = useState<Data[]>([]);


    function count(){
        sourceCount({
            search : queue,
            filter : {
                parameters : fields
            }
        }).then(elementsCount => {
            setElementsCount(elementsCount);
            setPageChosen(0);
        }).catch(e => {
            console.log(e);
        });
    }

    function search(){
        sourceData({
            search : queue,
            filter : {
                parameters : fields
            },
            pager : {
                pageChosen : pageChosen,
                pageSize : pageSize
            }
        }).then(data => {
            setData(data);
        }).catch(e => {
            console.log(e);
        });
    }

    useEffect(() => {
        count();
        search();
    }, []);

    useEffect(() => {
        search();
    }, [pageChosen]);


    return (
        <div>
            <SearchBar queue={queue} setQueue={setQueue}/>
            <button className="btn btn-success" onClick={() => { count(); search(); }}>Apply</button>
            <Filter parameters={new Map<string, FieldValue>([])}/>

            <div>
                {data.map(d => (
                    <Wrapper data={d}/>
                ))}
            </div>

            <Pager
                elementsCount={elementsCount}
                pageSize={pageSize}
                setPageSize={setPageSize}
                pageChosen={pageChosen}
                setPageChosen={setPageChosen}
            />

        </div>
    );
};

export default List;