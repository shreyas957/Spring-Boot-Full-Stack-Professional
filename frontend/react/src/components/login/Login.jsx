'use client'

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

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
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
                    // TODO: Redirect to dashboard
                    navigate('/dashboard')
                    console.log("Successfully logged in");
                }).catch(err => {
                    console.log(err);
                    errorNotification(err.code, err.response.data.message)
                }).finally(() => {
                    setSubmitting(false);   // I can submit form again
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

export default Login;