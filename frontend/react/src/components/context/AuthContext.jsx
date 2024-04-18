import {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";
import {login as performLogin} from "../../services/client.js";
import {jwtDecode} from "jwt-decode";

const AuthContext = createContext({});

const AuthProvider = ({children}) => {

    const [customer, setCustomer] = useState(null)
    useEffect(() => {
        let token = localStorage.getItem("jwtAccessToken")
        if (token) {
            const decodedToken = jwtDecode(token)
            setCustomer({
                username: decodedToken.sub,
                roles: decodedToken.scopes
            })
        }
    }, [])
    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(res => {
                const jwtToken = res.headers["authorization"];
                // TODO: save the token
                localStorage.setItem("jwtAccessToken", jwtToken)
                console.log(jwtToken);
                const decodedToken = jwtDecode(jwtToken)
                setCustomer({
                    username: decodedToken.sub,
                    roles: decodedToken.scopes
                })
                resolve(res);
            }).catch(err => {
                reject(err);
            })
        })
    }

    const logOut = () => {
        localStorage.removeItem("jwtAccessToken");
        {/*
            The reason behind setting the customer to null is to re-render the components
            re-render the components meaning that the components that are using the customer state will be re-rendered
        */
        }
        setCustomer(null);
    }

    const isCustomerAuthenticated = () => {

        const jwtToken = localStorage.getItem("jwtAccessToken");
        if (!jwtToken) {
            return false;
        }
        const decodedToken = jwtDecode(jwtToken)
        if (Date.now() > decodedToken.exp * 1000) {
            logOut()
            return false
        }

        return true
    }

    return (
        <AuthContext.Provider value={{customer, login, logOut, isCustomerAuthenticated}}>
            {children}
        </AuthContext.Provider>
    )
};

export const useAuth = () => {
    return useContext(AuthContext);
}

export default AuthProvider;