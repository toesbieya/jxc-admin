import Vue from 'vue'
import dragDialog from './dragDialog'
import ripple from './ripple/main'

Vue.directive('drag-dialog', dragDialog)
Vue.directive('ripple', ripple)

