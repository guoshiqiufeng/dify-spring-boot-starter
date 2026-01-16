import {defineUserConfig} from 'vuepress'
import {defaultTheme} from '@vuepress/theme-default'
import {viteBundler} from '@vuepress/bundler-vite'
import {version} from '../../package.json'

export default defineUserConfig({
    lang: 'zh-CN',
    title: 'dify-spring-boot-starter',
    description: 'dify-spring-boot-starter-doc',
    base: '/dify-spring-boot-starter-doc/v2.x-SNAPSHOT/',
    locales: {
        // 键名是该语言所属的子路径
        // 作为特例，默认语言可以使用 '/' 作为其路径。
        '/': {
            lang: 'zh-CN',
            title: 'dify-spring-boot-starter',
            description: 'dify springboot 实现'
        },
        '/en/': {
            lang: 'en-US',
            title: 'dify-spring-boot-starter',
            description: 'dify springboot realization'
        }
    },
    head: [
        ['link', {rel: 'icon', href: '/dify-spring-boot-starter-doc/v2.x-SNAPSHOT/images/f.ico'}],
        ['link', {rel: 'shortcut icon', href: '/dify-spring-boot-starter-doc/v2.x-SNAPSHOT/images/f.ico'}]
    ],
    bundler: viteBundler(),
    theme: defaultTheme({
        // logo: "/images/logo.png",
        // logoDark: "/images/logo-d.png",
        locales: {
            '/': {
                label: '简体中文',
                selectLanguageText: '选择语言',
                selectLanguageName: '简体中文',
                ariaLabel: '选择语言',
                editLinkText: '在 GitHub 上编辑此页',
                lastUpdated: true,
                lastUpdatedText: '上次更新',
                tip: '提示',
                warning: '注意',
                danger: '警告',
                // 404 page
                notFound: [
                    '这里什么都没有',
                    '我们怎么到这来了？',
                    '这是一个 404 页面',
                    '看起来我们进入了错误的链接',
                ],
                contributors: false,
                sidebar: {
                    "/guide/": [
                        {
                            text: '指南',
                            collapsible: true,
                            children: [
                                '/guide/introduction',
                                '/guide/getting-started',
                                '/guide/install',
                                '/guide/config',
                                '/guide/builder',
                                '/guide/status',
                                // '/guide/annotation',
                            ]
                        },
                        {
                            text: '功能',
                            collapsible: true,
                            children: [
                                '/guide/feature/chat',
                                '/guide/feature/workflow',
                                '/guide/feature/dataset',
                                '/guide/feature/server',
                            ]
                        },
                        {
                            text: '事件',
                            collapsible: true,
                            children: [
                                '/guide/event/introduction',
                                '/guide/event/chat',
                            ]
                        }
                    ],
                    "/config/": [
                        {
                            text: '配置',
                            children: [
                                '/config/introduction',
                                '/config/custom',
                            ]
                        }
                    ]
                },
                navbar: [
                    {
                        text: '指南',
                        //link: '/guide/',
                        children: [
                            '/guide/introduction',
                            '/guide/getting-started',
                            '/guide/install',
                            '/guide/config',
                            '/guide/builder',
                            '/guide/status',
                            // '/guide/annotation',
                            {
                                text: '功能',
                                children: [
                                    '/guide/feature/chat',
                                    '/guide/feature/workflow',
                                    '/guide/feature/dataset',
                                    '/guide/feature/server',
                                ]
                            },
                            {
                                text: '事件',
                                children: [
                                    '/guide/event/introduction',
                                    '/guide/event/chat'
                                ]
                            }
                        ]
                    },
                    {
                        text: '配置',
                        children: [
                            '/config/introduction',
                            '/config/custom',
                        ]
                    },
                    {
                        text: `v${version}`,
                        children: [
                            {
                                text: 'v2.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter/',
                            }, {
                                text: 'v1.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter-doc/v1.x/',
                            }, {
                                text: 'v0.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter-doc/v0.x/',
                            }, {
                                text: '更新日志',
                                link: 'https://github.com/guoshiqiufeng/dify-spring-boot-starter/releases',
                            }
                        ]
                    }
                ]
            },
            '/en/': {
                label: 'English',
                selectLanguageText: 'Languages',
                selectLanguageName: 'English',
                ariaLabel: 'Select language',
                editLinkText: 'Edit this page on GitHub',
                lastUpdated: true,
                lastUpdatedText: 'Last Updated',
                contributors: false,
                notFound: [
                    'There\'s nothing here',
                    'How did we get here？',
                    'This is a 404 page',
                    'Looks like we ran into the wrong link',
                ],
                sidebar: {
                    "/en/guide/": [
                        {
                            text: 'Guide',
                            collapsible: true,
                            children: [
                                '/en/guide/introduction',
                                '/en/guide/getting-started',
                                '/en/guide/install',
                                '/en/guide/config',
                                '/en/guide/builder',
                                '/en/guide/status',
                                // '/en/guide/annotation',
                            ]
                        },
                        {
                            text: 'Features',
                            collapsible: true,
                            children: [
                                '/en/guide/feature/chat',
                                '/en/guide/feature/workflow',
                                '/en/guide/feature/dataset',
                                '/en/guide/feature/server',
                            ]
                        },
                        {
                            text: 'Event',
                            collapsible: true,
                            children: [
                                '/en/guide/event/introduction',
                                '/en/guide/event/chat',
                            ]
                        }
                    ],
                    "/en/config/": [
                        {
                            text: 'Config',
                            children: [
                                '/en/config/introduction',
                                '/en/config/custom',
                            ]
                        }
                    ]
                },
                navbar: [
                    {
                        text: 'Guide',
                        // link: '/en/guide/',
                        children: [
                            '/en/guide/introduction',
                            '/en/guide/getting-started',
                            '/en/guide/install',
                            '/en/guide/config',
                            '/en/guide/builder',
                            '/en/guide/status',
                            // '/en/guide/annotation',
                            {
                                text: 'Features',
                                children: [
                                    '/en/guide/feature/chat',
                                    '/en/guide/feature/workflow',
                                    '/en/guide/feature/dataset',
                                    '/en/guide/feature/server',
                                ]
                            },
                            {
                                text: 'Event',
                                children: [
                                    '/en/guide/event/introduction',
                                    '/en/guide/event/chat',
                                ]
                            }
                        ]
                    },
                    {
                        text: 'Config',
                        children: [
                            '/en/config/introduction',
                            '/en/config/custom',
                        ]
                    },
                    {
                        text: `v${version}`,
                        children: [
                            {
                                text: 'v2.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter/',
                            }, {
                                text: 'v1.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter-doc/v1.x/',
                            }, {
                                text: 'v0.x',
                                link: 'https://guoshiqiufeng.github.io/dify-spring-boot-starter-doc/v0.x/',
                            }, {
                                text: 'Changelog',
                                link: 'https://github.com/guoshiqiufeng/dify-spring-boot-starter/releases',
                            }
                        ]
                    }
                ]
            },
        },
        repo: "https://github.com/guoshiqiufeng/dify-spring-boot-starter"
    }),
    plugins: []
})
