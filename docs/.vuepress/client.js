import {provide} from "vue"
import {defineClientConfig} from "vuepress/client";
import {version} from '../../package.json'

export default defineClientConfig({
    setup() {
        console.log("version:" + version)
        provide('version', version)
    },
})
