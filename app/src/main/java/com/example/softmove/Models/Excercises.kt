package com.example.softmove.Models

data class Exercises(
    val Excercise_name: String = "",
    val Sets: Int = 0,
    val Reps_each_set: Int = 0,
    val Excercise_time: String = "",
    val Excercise_animation: String = ""
    )
{
    // No-argument constructor required by Firebase Realtime Database
    constructor() : this("", 0, 0,"","")
}
