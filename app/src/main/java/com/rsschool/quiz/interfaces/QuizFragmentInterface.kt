package com.rsschool.quiz.interfaces

interface QuizFragmentInterface {
    fun clickNextFragmentButton(count: Int, playerAnswer: String) // , playerAnswerCount: Int?

    fun clickBackFragmentButton(count: Int)
}