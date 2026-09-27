import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'zh-CN',
  title: 'Dustbin 垃圾桶',
  description: 'Minecraft 26.2 Fabric 模组：掉落物超时自动分类进桶',
  srcDir: '.',
  ignoreDeadLinks: true,
  lastUpdated: true,
  head: [['link', { rel: 'icon', type: 'image/png', href: '/images/icon.png' }]],

  themeConfig: {
    logo: '/images/icon.png',
    nav: [
      { text: '指南', link: '/guide/getting-started', activeMatch: '/guide/' },
      { text: '版本发布', link: '/releases/release-1.1.0', activeMatch: '/releases/' },
      { text: '开发', link: '/dev/texture', activeMatch: '/dev/' },
      { text: 'GitHub', link: 'https://github.com/AUNPP-CN/Dustbin-Mod' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: '快速开始', link: '/guide/getting-started' },
            { text: '四类垃圾桶与分类规则', link: '/guide/bins' },
            { text: '配置与命令', link: '/guide/commands' }
          ]
        }
      ],
      '/releases/': [
        {
          text: '版本发布',
          items: [
            { text: '1.1.0 —— 四类垃圾桶', link: '/releases/release-1.1.0' },
            { text: '1.0.0 —— 首发', link: '/releases/release-1.0.0' }
          ]
        }
      ],
      '/dev/': [
        {
          text: '开发',
          items: [{ text: '贴图规格', link: '/dev/texture' }]
        }
      ]
    },

    socialLinks: [{ icon: 'github', link: 'https://github.com/AUNPP-CN/Dustbin-Mod' }],

    search: { provider: 'local' },

    outline: { level: [2, 3], label: '本页目录' },
    docFooter: { prev: '上一篇', next: '下一篇' },
    lastUpdated: { text: '最后更新' },
    returnToTopLabel: '回到顶部',
    sidebarMenuLabel: '菜单'
  }
})
