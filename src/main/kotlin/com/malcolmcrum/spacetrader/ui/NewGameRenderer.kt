package com.malcolmcrum.spacetrader.ui

import com.malcolmcrum.spacetrader.model.Difficulty
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
                table {
                    tr {
                        th {
                            +"Commander Name"
                        }
                    }
                    tr {
                        td {
                            textInput {
                                name = "name"
                                value = "Jameson"
                            }
                        }
                    }
                }
                table {
                    tr {
                        th {
                        +"Difficulty"
                        }
                    }
                    Difficulty.values().forEach { difficulty ->
                        tr {
                            td {
                                radioInput {
                                    if (difficulty == Difficulty.NORMAL) checked = true
                                    name = "difficulty"
                                    value = difficulty.toString()
                                }
                                label {
                                    htmlFor = difficulty.toString()
                                    +difficulty.name
                                }
                            }
                        }
                    }
                }
                table {
                    tr {
                        th {
                            +"Skill points"
                        }
                        th {
                            +"20"
                        }
                    }
                    tr {
                        td {
                            +"Pilot"
                        }
                        td {
                            textInput {
                                name = "pilot"
                                value = "5"
                            }
                        }
                    }
                    tr {
                        td {
                            +"Fighter"
                        }
                        td {
                            textInput {
                                name = "fighter"
                                value = "5"
                            }
                        }
                    }
                    tr {
                        td {
                            +"Trader"
                        }
                        td {
                            textInput {
                                name = "trader"
                                value = "5"
                            }
                        }
                    }
                    tr {
                        td {
                            +"Engineer"
                        }
                        td {
                            textInput {
                                name = "engineer"
                                value = "5"
                            }
                        }
                    }
                }

                submitInput {
                    value = "New Game"
                }
            }
        }
    }
}
