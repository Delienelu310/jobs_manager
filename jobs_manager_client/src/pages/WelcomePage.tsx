import { useAuth } from "../authentication/AuthContext";
import { Navigate } from "react-router-dom";


export default function WelcomePage() {
    const {authentication} = useAuth();

    if(authentication == null){
        return <Navigate to="/login"/>
    }

    return (
        <div>
            <h2>Welcome</h2>
        </div>
    );
}