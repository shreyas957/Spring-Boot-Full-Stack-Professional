import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import React, {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack} from "@chakra-ui/react";
import loginImage from "../../assets/loginImg.png";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {
    const {customer, setCustomerFromToken} = useAuth();
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
                    <Heading fontSize={'2xl'} mb={15}>Register your account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
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