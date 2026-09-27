import { h } from 'vue'
import DefaultTheme from 'vitepress/theme'
import Footer from './Footer.vue'

export default {
  extends: DefaultTheme,
  Layout: () =>
    h(DefaultTheme.Layout, null, {
      'layout-bottom': () => h(Footer)
    })
}
