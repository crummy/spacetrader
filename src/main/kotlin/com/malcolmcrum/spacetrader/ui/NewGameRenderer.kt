package com.malcolmcrum.spacetrader.ui

import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun newGame(): String {
    return createHTML().html {
        head {
            styles()
        }
        body {
            h1 {
                +"Space Trader"
            }
            form {
                action = "/new"
                method = FormMethod.post
                textInput {
                    name = "name"
                    +"Commander Name"
                }
                radioInput {
                    name = "difficulty"
                    +"test"
                }
                submitInput {
                    value = "New Game"
                }
            }
        }
    }
}
