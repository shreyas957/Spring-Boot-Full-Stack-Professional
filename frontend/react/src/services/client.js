import axios from "axios";

export const getCustomers = async () => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`)
    } catch (err) {
        throw err;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer // JSON Object (Request Body)
        )
    } catch (err) {
        throw err;
    }
}

export const deleteCustomer = async (id) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`
        )
    } catch (err) {
        throw err;
    }
}

export const updateCustomer = async (id, update) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/update/${id}`,
            update  // JSON Object (Request Body)
        )
    } catch (err) {
        throw err;
    }
}