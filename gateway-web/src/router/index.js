import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

import Login from '../pages/Login'
import Home from '../pages/Home'
import cookie from '../utils/cookie'

const router = new VueRouter({
        routes: [{
                path: '/',
                redirect: '/login'
            },
            { //登录
                path: '/login',
                name: 'Login',
                component: Login
            },
            { //主页
                path: '/index',
                name: 'Home',
                component: Home,
                meta: { requiresAuth: true }, // 需要登录的路由
            },
            {
                path: "/:pathMatch(.*)*",
                component: () =>
                    import ('@/pages/error/404'),
                hidden: true
            },
        ]
    })
    // 添加路由守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem('username'); // 检查 token 是否存在
    // to.matched.some(record => record.meta.requiresAuth) && 
    if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
        // 如果路由需要认证且用户未登录
        next({ name: 'Login' }); // 重定向到登录页面
    } else {
        next(); // 继续导航
    }
});
export default router