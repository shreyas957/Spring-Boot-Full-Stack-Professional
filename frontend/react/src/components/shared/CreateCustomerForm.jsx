import React from 'react';
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

/**
 * MyTextInput is a custom input component for Formik forms.
 * It displays a label, an input field, and an error message if the field is invalid and has been touched.
 *
 * @param {object} props - The properties passed to the component.
 * @param {string} props.label - The label for the input field.
 * @returns {JSX.Element} The rendered component.
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
 * MySelect is a custom select component for Formik forms.
 * It displays a label, a select field, and an error message if the field is invalid and has been touched.
 *
 * @param {object} props - The properties passed to the component.
 * @param {string} props.label - The label for the select field.
 * @returns {JSX.Element} The rendered component.
 */
const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
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
 * CreateCustomerForm is a Formik form for creating a new customer.
 * It validates the input fields and submits the form data to the server.
 * After successful submission, it fetches the updated list of customers.
 *
 * @param {object} props - The properties passed to the component.
 * @param {function} props.fetchCustomers - The function to fetch the updated list of customers.
 * @returns {JSX.Element} The rendered component.
 */
const CreateCustomerForm = ({onSuccess}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    password: '',
                    gender: '',
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must at less than 100 years of age')
                        .required('Required'),
                    password: Yup.string()
                        .min(4, 'Must be 4 characters or more')
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid Gender Type'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true)
                    saveCustomer(customer).then(res => {
                        console.log(res);
                        successNotification(
                            "Customer saved",
                            `${customer.name} was successfully saved`
                        )
                        onSuccess(res.headers["authorization"]);
                    }).catch(err => {
                        console.log(err)
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false)
                    })
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="20"
                            />

                            <MyTextInput
                                label="Password"
                                name="password"
                                type="password"
                                placeholder="******"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select gender type</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button disabled={!isValid || isSubmitting} type="submit"
                                    bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}} color={"white"}
                                    _hover={false}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;