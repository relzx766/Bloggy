import axios from "axios";
import {FormPost, Get, JsonPost} from "../http";
export  const  login=async ( data={})=>{
    return FormPost({
        url: '/user/login',
        data: data
    });
}
export const register=async (data={})=>{
   return JsonPost({
       url: "/user/register",
       data: data
   });
}
export const verify=async (email,code)=>{
    return FormPost({
        url: '/user/verify',
        data: {
            email: email,
            code: code
        }
    });
}
export const getArticlePage=async (page)=>{
    return Get({
        url:'/article/page',
        params: {page}
    })
}
export const getArticleDetail=async (id)=>{
    return Get({
        url:'/article/detail',
        params:{id}
    })
}
export const getArticleTrend=async ()=>{
    return Get({
        url:"/trend/article"
    })
}
export const getUserState=async (id)=>{
    return Get({
        url:'/user/state',
        params:{id}
    })
}
export const postArticle=async (data={})=>{
    return JsonPost({
        url:"/article/publish",
        data: data
    })
}
export const uploadFile=async (data={})=>{
    return FormPost({
        url:'/file/upload/image',
        data:data
    })
}
export const getArticleIsLike=async (id)=>{
    return Get({
        url:'/article/like/get',
        params:{id}
    })
}
export const likeArticle=async (id)=>{
    return Get({
        url:'/article/like',
        params:{id}
    })
}
export const cancelLikeArticle=async (id)=>{
    return Get({
        url:'/article/like/cancel',
        params:{id}
    })
}
export const getAd=async ()=>{
    return Get({
        url:'/trend/ad'
    })
}
export const getComment=async (id,page)=>{
    return Get({
        url:'/comment/article',
        params:{id,page}
    })
}
export const getReply=async (id)=>{
    return Get({
        url:'/comment/reply',
        params:{id}
    })
}
export const postComment=async (id,content)=>{
    return JsonPost({
        url:'/comment/article/post',
        data:{
            articleId:id,
            content:content
        }
    })
}
export const postReply=async (id,content)=>{
    return JsonPost({
        url:'comment/reply/post',
        data:{
            commentId:id,
            content:content
        }

    })
}

