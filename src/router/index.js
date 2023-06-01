import Vue from 'vue'
import VueRouter from 'vue-router'

import Login from "@/views/Login";
import Register from "@/views/Register";
import Index from "@/views/Index"
import Trend from "@/views/Trend";
import Write from "@/views/Write";
import ArticleDetail from "@/views/ArticleDetail";
// 解决ElementUI导航栏中的vue-router在3.0版本以上重复点菜单报错问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}
Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'index',
        component: Index
    },
    {
        path: '/login',
        name: 'login',
        component: Login
    },
    {
        path: '/register',
        name: 'register',
        component: Register
    },
    {
        path: '/index',
        name: 'index',
        component: Index
    },
    {
        path: '/trend',
        name: 'trend',
        component: Trend
    },
    {
        path: '/write',
        name: 'trend',
        component: Write
    },
    {
        path: '/detail',
        name: 'detail',
        component: ArticleDetail
    }
]

const router = new VueRouter({
    mode: 'history',
    routes: routes
})

export default router
