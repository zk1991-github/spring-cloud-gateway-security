import { reqCategoryList } from "../../api"
const state = {
    categoryList:[]
}
const mutations = {
    REVEICE_CATEGORYLIST(state,categoryList){
        state.categoryList = categoryList
    }
}
const actions = {
   async getCategoryList({commit}){
        const result = await reqCategoryList()
        if(result.code === 200){
            commit('REVEICE_CATEGORYLIST',result.data)
        }
    }
}
const getters = {}



export default {
    state,
    mutations,
    actions,
    getters
}