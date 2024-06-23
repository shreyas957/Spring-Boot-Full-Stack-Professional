/**
 * 'use client' is a directive that tells the browser to run the script in a so-called "modern" way.
 */
'use client'

// Importing necessary components and hooks from various libraries
import {
    Button,
    Flex,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link, Box, Alert, AlertIcon,
} from '@chakra-ui/react'
import loginImage from '../../assets/loginImg.png';
import {Form, Formik, useField} from "formik";
import * as Yup from 'yup';
import React, {useEffect} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";
import {useNavigate} from "react-router-dom";

/**
 * MyTextInput is a custom input component that includes form validation.
 * @param {object} props - The properties passed to the component.
 * @returns {JSX.Element} - A Box component containing the FormLabel, Input and Alert components.
 */
const MyTextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

/**
 * LoginForm is a component that handles the login form submission.
 * @returns {JSX.Element} - A Formik component containing the form and its validation logic.
 */
const LoginForm = () => {
    const {login} = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string().email('Must be valid email').required('Email is required'),
                    password: Yup.string().max(20, 'Password cannot be more than 20 characters').required('Password is required')
                })
            }
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true);
                login(values).then(res => {
                    navigate('/dashboard')
                    console.log("Successfully logged in");
                }).catch(err => {
                    console.log(err);
                    errorNotification(err.code, err.response.data.message)
                }).finally(() => {
                    setSubmitting(false);
                })
            }}>

            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack spacing={4}>
                        <MyTextInput
                            label={"Email Address"}
                            name={"username"}
                            type={"email"}
                            placeholder={"hello@shreyas.com"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"********"}
                        />
                        <Button disabled={!isValid || isSubmitting} type={"submit"}
                                bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}} color={"white"} _hover={false}>Log
                            in</Button>
                    </Stack>
                </Form>
            )}

        </Formik>

    )
}

/**
 * Login is the main component that renders the login page.
 * @returns {JSX.Element} - A Stack component containing the LoginForm and other UI elements.
 */
const Login = () => {
    const {customer} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate('/dashboard')
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm/>
                    <Link color={"blue.500"} href={"/signup"}>
                        Don't have account? Signup now.
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1} p={10}
                  flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
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

// Exporting the Login component to be used in other parts of the application
export default Login;