
export interface SearchBarParameters{
    queue : string,
    setQueue: React.Dispatch<React.SetStateAction<string>>

}  

const SearchBar = ({queue, setQueue} : SearchBarParameters) => {
    return (
        <div style={{margin: "30px 15%"}}>
            <h3>Search</h3>
            <input className="form-control" placeholder="search..." value={queue} onChange={e => setQueue(e.target.value)}/>
        </div>
    );
};


export default SearchBar;