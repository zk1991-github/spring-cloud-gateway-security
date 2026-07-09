import  Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)
import home from './modules/home'
import login from './modules/login'
import learningresources from './modules/login'
import stationexample from './modules/login'
import teachingadministration from './modules/login'


const state = {}
const mutations = {}
const actions = {}
const getters = {}



export default new Vuex.Store({
    state,
    mutations,
    actions,
    getters,

    modules:{
        home,
        login,
        learningresources,
        stationexample,
        teachingadministration
    }
})