package com.malcolmcrum.spacetrader.ui

import kotlinx.html.HEAD
import kotlinx.html.style

fun HEAD.styles() {
    style {
        +"body {background-color: powderblue;}"
        +"table {border: 1px solid black;}"
        +"table {display: inline-flex;}"
        +"th {background-color: gray;}"
    }
}