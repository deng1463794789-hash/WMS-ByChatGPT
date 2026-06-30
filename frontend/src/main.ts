import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { setupRouter } from './router'
import { setupStore } from './stores'
import './assets/styles/index.scss'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

setupStore(app)
setupRouter(app)
app.use(ElementPlus, { size: 'default' })
app.mount('#app')
