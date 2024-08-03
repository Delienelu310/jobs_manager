import React, { useState } from "react";


export interface PagerProperties{
    elementsCount : number,

    pageSize : number,
    setPageSize : React.Dispatch<React.SetStateAction<number>>
    pageChosen : number,
    setPageChosen : React.Dispatch<React.SetStateAction<number>>
}

const Pager = ({pageSize, setPageSize, elementsCount, pageChosen, setPageChosen} : PagerProperties) => {

    return (
        <div style={{margin : "10px 15%"}}>
            <h4>Page Size:</h4>
            <input className="form-control m-3" type="number" value={pageSize} onChange={(e) => setPageSize(Number.parseInt(e.target.value))}/>

            {Array.of(Math.ceil(elementsCount / pageSize)).map((val, index) => (
                <button 
                    onClick={
                        e => setPageChosen(index)
                    }
                    className={pageChosen == index ? "btn btn-success m-1" : "btn btn-primary"}
                >
                    {index + 1}
                </button>
            ))}
        </div>
    )
};

export default Pager;