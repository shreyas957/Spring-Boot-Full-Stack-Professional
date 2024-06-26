import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

const ProtectedRoutes = ({children}) => {

    const {isCustomerAuthenticated} = useAuth();
    const navigate = useNavigate();

    // Check if user is authenticated
    useEffect(() => {
        if (!isCustomerAuthenticated()) {
            navigate("/")
        }
    });

    return isCustomerAuthenticated() ? children : "";
}

export default ProtectedRoutes;