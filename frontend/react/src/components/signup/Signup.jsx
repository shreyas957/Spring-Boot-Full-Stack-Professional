import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import React, {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack} from "@chakra-ui/react";
import loginImage from "../../assets/loginImg.png";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

/**
 * `Signup` is a React functional component that provides a signup form for new customers.
 * It uses the `useAuth` hook to get the current customer and a function to set the customer from a token.
 * It also uses the `useNavigate` hook from `react-router-dom` to navigate to different routes.
 *
 * If a customer is already logged in, they are redirected to the dashboard.
 *
 * The component returns a JSX element that includes a `CreateCustomerForm` component.
 * When the form is successfully submitted, the JWT token is stored in local storage, the customer is set from the token,
 * and the user is navigated to the dashboard.
 *
 * @returns {JSX.Element} The Signup component
 */

const Signup = () => {
    const {customer, setCustomerFromToken} = useAuth();
    const navigate = useNavigate();

    /**
     * Use the `useEffect` hook to navigate to the dashboard if a customer is already logged in.
     */
    useEffect(() => {
        if (customer) {
            navigate('/dashboard')
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Register your account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        /**
                         * On successful form submission, store the JWT token in local storage,
                         * set the customer from the token, and navigate to the dashboard.
                         */
                        localStorage.setItem("jwtAccessToken", token)
                        setCustomerFromToken()
                        navigate('/dashboard')
                    }}/>
                    <Link color={"blue.500"} href={"/"}>
                        Have account? Login now.
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1} p={10}
                  flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                {/*<Text fontSize={'6xl'} color={'white'} fontWeight={'bold'} mb={5}>*/}
                {/*    <Link href={'www.linkedin.com/in/shreyas-shevale'}> My linkedin profile</Link>*/}
                {/*</Text>*/}
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        loginImage
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Signup;