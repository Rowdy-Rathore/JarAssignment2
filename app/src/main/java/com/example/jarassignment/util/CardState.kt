package com.example.jarassignment.util

sealed class CardState {
    object Offscreen : CardState()          // not visible (start)
    object Halfscreen : CardState()         // entering from bottom, half shown
    object Expanding : CardState()          // fully expanded
    object CollapsingTilted : CardState()   // collapsing with tilt
    object Collapsed : CardState()          // final stacked state (rotation reset)
}