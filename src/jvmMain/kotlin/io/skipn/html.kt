package io.skipn

import kotlinx.html.*

//fun HTML.html() {
//    head {
//        title(appTitle)
//
//        unsafe {
//            +"""
//                <meta charset="utf-8">
//                <meta http-equiv="Cache-control" content="public">
//                <meta name="viewport" content="width=device-width, initial-scale=1">
//                <link rel="stylesheet" href="/public/${Skipn.buildHash}.css">
//
//                <link rel="apple-touch-icon" sizes="180x180" href="public/images/favicon/apple-touch-icon.png">
//                <link rel="icon" type="image/png" sizes="32x32" href="public/images/favicon/favicon-32x32.png">
//                <link rel="icon" type="image/png" sizes="16x16" href="public/images/favicon/favicon-16x16.png">
//                <link rel="manifest" href="public/images/favicon/site.webmanifest">
//                <link rel="mask-icon" href="public/images/favicon/safari-pinned-tab.svg" color="#4dc584">
//                <link rel="shortcut icon" href="public/images/favicon/favicon.ico">
//                <meta name="msapplication-TileColor" content="#00aba9">
//                <meta name="msapplication-config" content="public/images/favicon/browserconfig.xml">
//                <meta name="theme-color" content="#ffffff">
//            """.trimIndent()
//        }
//    }
//    body {
//        div {
//            id = "skipn-app"
//            appRoot()
//        }
//
//
//        unsafe {
//            + """
//                <script type="text/javascript" src="/public/${Skipn.buildHash}.js" id="skipn-main-script"></script>
//            """.trimIndent()
//        }
////        script(src = "/public/output.js", type = "text/javascript") {
////            attributes["id"] = "main-script"
////        }
//        script {
//            attributes["id"] = "skipn-res"
//            attributes["data-res"] = skipnContext.resources.createSnapshot()
//        }
//    }
//}