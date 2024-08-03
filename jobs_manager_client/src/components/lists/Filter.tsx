import { useEffect, useState } from "react";


export enum FieldType{
    SingleInput, MultipleInput, SingleSelection, MultipleSelection
}

export interface FieldValue{   
    setter : (val : string[]) => void   
    fieldType: FieldType,                                      
    additionalData: string[] ,                                       
}


export interface FilterProperties{
    parameters : Map<string, FieldValue>
    values : Map<string, string[]>
}

const Filter = ({parameters, values} : FilterProperties) => {

    const [multipleInput, setMultipleInput] = useState<string>("");
    const [multipleInputList, setMultipleInputList] = useState<string[]>([]);

    return (
        <div>
            {Array.from(parameters.entries()).map( ([label, fieldValue]) => (
                <div>
                    {fieldValue.fieldType == FieldType.SingleInput ?
                        <div>
                            <strong>{label}</strong>
                            <input className="form-control m-2" value={values.get(label)} onChange={(e) => fieldValue.setter([e.target.value])}/>
                        </div>
                        :
                        fieldValue.fieldType == FieldType.MultipleInput ?
                        <div>
                            <strong>{label}</strong>
                            <input className="form-control m-2" value={multipleInput} onChange={(e) => setMultipleInput(e.target.value)}/>
                            <button className="btn btn-success" onClick={(e) => {
                                fieldValue.setter([...multipleInputList, multipleInput]);
                                setMultipleInputList([...multipleInputList, multipleInput]);
                                
                            }}>Add</button>

                            {multipleInputList.map(input => <div key={label + "_" +input}>
                                <span>{input}</span>
                                <button 
                                    onClick={(e) => {
                                        fieldValue.setter(multipleInputList.filter(val => val != input));
                                        setMultipleInputList(multipleInputList.filter(val => val != input));
                                    }} 
                                    className="btn btn-danger"
                                >X</button>
                            </div>)}
                        </div>
                        :
                        fieldValue.fieldType == FieldType.SingleSelection ?
                        <div>
                            <strong>{label}</strong>
                            <select 
                                className="form-control m-2"
                                value={values.get(label)}
                                onChange={(e) => fieldValue.setter([e.target.value])}
                            >
                                {fieldValue.additionalData.map(str => <option value={str}>
                                    {str}
                                </option>)}
                            </select>
                        </div>
                        :
                        fieldValue.fieldType == FieldType.MultipleSelection ? 
                        <div>
                            <strong>{label}</strong>

                            <select className="form-control m-2" multiple value={values.get(label)} onChange={e => {
                                const currentValue = values.get(label) ?? [];
                                console.log(currentValue);
                                if(currentValue.includes(e.target.value)){
                                    fieldValue.setter(currentValue.filter(v => v != e.target.value));
                                }else{
                                    fieldValue.setter([...currentValue, e.target.value]);
                                }
                            }}>
                                {fieldValue.additionalData.map(str => <option value={str}>
                                    {str}
                                </option>)}
                            </select>
                            <br/>
                            <button className="btn btn-primary" onClick={e => fieldValue.setter([])}>Deselect</button>

                        </div>
                        :
                        <div>
                            Invalid Field Type: {fieldValue.fieldType}
                        </div>
                    }
                </div>
            ) )}
        </div>
    );
};

export default Filter;