package com.example.jarassignment.util

sealed class EducationDest(val route: String) {
    object Intro : EducationDest("intro")
    object Cards : EducationDest("cards")
    object Landing : EducationDest("landing")
}