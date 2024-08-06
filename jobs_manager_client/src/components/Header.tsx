import { Link } from "react-router-dom";
import "../css/components/header.css"
import { useAuth } from "../authentication/AuthContext";

const Header = () => {

    const {authentication, logout} = useAuth();

    return (
        <div className="header">
            <Link to="/" className="header_element header_element_left">WelcomePage</Link>
            <Link to="/projects" className="header_element header_element_left">Projects</Link>
            <Link to="/users" className="header_element header_element_left">Users</Link>
            {authentication ? 
                <div onClick={logout} className="header_element header_element_right">Logout</div> :
                <Link to="/login" className="header_element header_element_right">Login</Link>   
            }
            
            


        </div>
    )
}

export default Header;