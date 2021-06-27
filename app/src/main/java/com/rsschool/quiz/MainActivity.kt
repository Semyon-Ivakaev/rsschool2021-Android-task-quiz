package com.rsschool.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rsschool.quiz.data.AllDataApp
import com.rsschool.quiz.fragments.QuizFragment
import com.rsschool.quiz.fragments.ResultFragment
import com.rsschool.quiz.interfaces.QuizFragmentInterface
import com.rsschool.quiz.interfaces.ResultFragmentInterface
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), QuizFragmentInterface, ResultFragmentInterface {
    var count = 0
    var playerAnswers = mutableMapOf<Int, String>()
    var playerAnswersCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, QuizFragment.newInstance(count))
                .commit()
        }
    }

    override fun clickNextFragmentButton(count: Int, playerAnswer: String) { // , playerAnswerCount: Int?
        this.count = count
        // Чтобы не перезаписывать выбранное значение, если возвращаемся на предыдущий фрагмент
        if (!playerAnswers.containsValue(playerAnswer)) {
            playerAnswers[count - 1] = playerAnswer
        }

        if (this.count > 4) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, ResultFragment.newInstance(resultPercent(), playerAnswers))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, QuizFragment.newInstance(count))
                .commit()
        }
    }

    override fun clickBackFragmentButton(count: Int) {
        this.count = count
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, QuizFragment.newInstance(count))
            .commit()
    }

    override fun clickOnShareButton(message: String) {
        val shareIntent = Intent()
        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Quiz Result")
            putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }
    }

    override fun clickOnBackButton() {
        this.count = 0
        playerAnswers = mutableMapOf()
        playerAnswersCount = 0
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, QuizFragment.newInstance(count))
            .commit()
    }

    override fun clickOnCloseButton() {
        finish()
        exitProcess(0)
    }

    private fun resultPercent(): Int {
        return (playerAnswersCount / 5.0 * 100).toInt()
    }
}