import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import {ChakraProvider} from '@chakra-ui/react'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoutes from "./components/shared/ProtectedRoutes.jsx";
import './index.css'

const {ToastContainer} = createStandaloneToast()

{/*
    These are the routes that the browser router will use to navigate between pages
    protected routes are wrapped in the ProtectedRoutes component to ensure that the user is authenticated
*/
}
const browserRouter = createBrowserRouter([
    {
        path: '/',
        element: <Login/>
    },
    {
        path: 'dashboard',
        element: <ProtectedRoutes><App/></ProtectedRoutes>
    }
])

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <ChakraProvider>
            <AuthProvider>
                {/*here we provide the browser router to the router provider*/}
                <RouterProvider router={browserRouter}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </React.StrictMode>
)
