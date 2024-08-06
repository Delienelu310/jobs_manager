
import { useState } from "react";
import { useAuth } from "../authentication/AuthContext";
import { Navigate } from "react-router-dom";

const LoginPage : React.FC = () => {


    const {login, authentication} = useAuth();

    const [areCredentialsInvalid, setCredentialsInvalid] = useState<boolean>(false);

    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    // if(token != null){
    //     return <Navigate to="/"/>
    // }

    return (
        <div style={{width: "50%", margin: "20px 25%"}}>
            
            <h4>Username: </h4>
            <input className="m-2 form-control" value={username} onChange={ (e) => setUsername(e.target.value)}/>
            <br/>
            <h4>Password: </h4>
            <input type="password" className="m-2 form-control" value={password} onChange={ (e) => setPassword(e.target.value)}/>
            <br/>
            {areCredentialsInvalid && <b style={{color: "red"}} className="m-2"> Invalid credentials!</b>}
            <br/>
            <button className="m-2 btn btn-success" onClick={(e) => {
                if(login !== undefined) login({username, password}).catch(exception => {
                    if(exception.response.status == 401) setCredentialsInvalid(true);
                });
            }}>Log in</button>
        </div>
    );
}

export default LoginPage;