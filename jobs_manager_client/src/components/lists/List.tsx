import Filter, { FieldType } from "./Filter";
import Pager from "./Pager";
import SearchBar from "./SearchBar";

import { FieldValue } from "./Filter";
import React, { useEffect, useState } from "react";
import OpenerComponent from "../OpenerComponent";


export interface Field{
    label : string,
    fieldType : FieldType,
    additionalData : string[]
}


export interface SourceCountArg{
    filter : {
        parameters : Map<string, FieldValue>,
        values : Map<string , string[]>
    },
    search : string
}

export interface SourceArg{
    filter : {
        parameters : Map<string, FieldValue>,
        values : Map<string, string[]>
    },
    search : string,
    pager : {
        pageSize : number,
        pageChosen : number
    }
}

export interface WrapperProps<Data, Context>{
    data : Data,
    context : Context
} 

export interface ListProperties<Data, Context>{
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
    Wrapper : React.FC<WrapperProps<Data, Context>>,
    context : Context,
    dependencies : any[]
}




const List = <Data, Context>({
    pager : {defaultPageSize},
    filter: {parameters},
    source: {sourceCount, sourceData},
    Wrapper,
    context,
    dependencies


} : ListProperties<Data, Context>) => {

    //search bar
    const [queue, setQueue] = useState<string>("");

    //pager
    
    const [pageSize, setPageSize] = useState<number>(defaultPageSize);
    const [pageChosen, setPageChosen] = useState<number>(1);

    const [elementsCount, setElementsCount] = useState<number>(0);

    

    //fitler:
    const [fields, setFields] = useState<Map<string, FieldValue>>(new Map<string, FieldValue>([]));
    
    const [values, setValues] = useState<Map<string, string[]>>(new Map<string, string[]>([]));

    useEffect(() => {
        
        const fields = new Map<string, FieldValue>([]);
        for(let field  of parameters){
            

            let fieldExtended : FieldValue = {
                additionalData: field.additionalData,
                fieldType: field.fieldType,
                setter: (val) => {
                    
                    setValues(values => {
                        const newMap = new Map(values);
                        newMap.set(field.label, val);
                        return newMap;
                    });
                }
            };
            fields.set(field.label, fieldExtended);

            
        }
        setFields(fields);
    }, []);
    
    
    
    
    //data
    const [data, setData] = useState<Data[]>([]);


    function count(){
        sourceCount({
            search : queue,
            filter : {
                parameters : fields,
                values : values
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
                parameters : fields,
                values: values
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
    }, [...dependencies]);

    useEffect(() => {
        search();
    }, [...dependencies, pageChosen]);


    return (
        <div>
            <SearchBar queue={queue} setQueue={setQueue}/>
            {Array.from(fields.entries()).length != 0 && 
                <OpenerComponent
                    closedLabel={<h4>Open Filter</h4>}
                    openedElement={
                        <Filter parameters={fields} values={values}/>
                    }
                />
            }
           
            
            <Pager
                elementsCount={elementsCount}
                pageSize={pageSize}
                setPageSize={setPageSize}
                pageChosen={pageChosen}
                setPageChosen={setPageChosen}
            />
            
            <button style={{width: "200px", fontSize: "20px", height: "50px", fontWeight: "bold"}} className="btn btn-success m-3" onClick={() => { count(); search(); }}>Apply</button>
            <div>
                {data.map(d => (
                    <Wrapper data={d} context={context}/>
                ))}
            </div>

            

        </div>
    );
};

export default List;