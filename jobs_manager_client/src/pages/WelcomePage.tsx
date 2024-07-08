import { useAuth } from "../authentication/AuthContext";
import { Navigate } from "react-router-dom";


export default function WelcomePage() {
    const {token} = useAuth();

    if(token == null){
        return <Navigate to="/login"/>
    }

    return (
        <div>
            <h2>Welcome</h2>
        </div>
    );
}