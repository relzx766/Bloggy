import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/views/Login";
import Register from "@/views/Register";
import Index from "@/views/Index"
import Trend from "@/views/Trend";
import Write from "@/views/Write";
import ArticleDetail from "@/views/ArticleDetail";
import Profile from "@/views/Profile";
import SearchDetail from "@/views/SearchDetail";
import BackstageIndex from "@/admin/views/BackstageIndex.vue";
import SortDetail from "@/views/SortDetail.vue";
import DashBoard from "@/admin/views/DashBoard.vue";
import ArticleManage from "@/admin/views/ArticleManage.vue";
import UserManage from "@/admin/views/UserManage.vue";
import AdvertisingManage from "@/admin/views/AdvertisingManage.vue";
import NotFoundPage from "@/views/NotFoundPage.vue";
import VueCookies from "vue-cookie";
import Home from "@/views/Home.vue";


// 解决ElementUI导航栏中的vue-router在3.0版本以上重复点菜单报错问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}
Vue.use(VueCookies)
Vue.use(VueRouter)

const routes = [
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
        path: '/',
        name: 'index',
        component: Index,
        children: [
            {
                path: '/home',
                component: Home
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
            },
            {
                path: '/profile',
                name: 'profile',
                component: Profile
            },
            {
                path: '/search',
                name: 'search',
                component: SearchDetail
            },
            {
                path: '/detail/sort',
                name: 'detail/sort',
                component: SortDetail
            }
        ]
    },


    {
        path: '/admin',
        component: BackstageIndex,
        meta:{requiresRole: true, role: 'admin'},
        children: [
            {
                path: 'article',
                component: ArticleManage
            },
            {
                path: 'user',
                component: UserManage
            },
            {
                path: 'ad',
                component: AdvertisingManage
            },
            {
                path: 'board',
                component: DashBoard
            }

        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFoundPage
    }
]

const router = new VueRouter({
    mode: 'history',
    routes: routes
})
router.beforeEach((to, from, next) => {
    const role=VueCookies.get("role")
    if (to.matched.length === 0){
        next({name: 'NotFound'})
    }
    else {
        if (to.meta.requiresRole){
            if (to.meta.role===role){
                next();
            }else {
                next({path:'/login'})
            }
        }else {
            next();
        }
    }

})
export default router
