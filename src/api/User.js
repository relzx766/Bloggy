import {FormPost, Get, JsonPost} from "../../http";

export const login = async (data = {}) => {
    return FormPost({
        url: '/user/login',
        data: data
    });
}
export const register = async (data = {}) => {
    return JsonPost({
        url: "/user/register",
        data: data
    });
}
export const verify = async (email, code) => {
    return FormPost({
        url: '/user/verify',
        data: {
            email: email,
            code: code
        }
    });
}
export const getUserProfile = async (id) => {
    return Get({
        url: '/user/profile',
        params: {id}
    })
}
export const postProfile = async (data) => {
    return FormPost({
        url: '/user/profile/update',
        data: data
    })
}
export const searchUser = async (keyword) => {
    return Get({
        url: '/user/search',
        params: {keyword}
    })
}