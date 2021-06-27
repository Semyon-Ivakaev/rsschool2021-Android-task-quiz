package com.rsschool.quiz.fragments

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rsschool.quiz.MainActivity
import com.rsschool.quiz.R
import com.rsschool.quiz.data.AllDataApp
import com.rsschool.quiz.databinding.FragmentQuizBinding
import com.rsschool.quiz.interfaces.QuizFragmentInterface

class QuizFragment: Fragment() {
    private val dataApp: AllDataApp = AllDataApp()
    private val allQuestions = dataApp.questions
    private val allAnswers = dataApp.answers
    private val allRightAnswers = dataApp.rightAnswers
    private var count: Int? = null
    var playerAnswer = ""

    // Interfaces
    private var quizFragmentInterface: QuizFragmentInterface? = null
    private var mainActivityView: MainActivity? = null

    private var _binding: FragmentQuizBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        count = 0
        count = arguments?.getInt("COUNT")

        Log.v("App", "Count $count")
        initTheme()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()
        checkCurrentAnswer()

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.optionOne.id -> playerAnswer = binding.optionOne.text.toString()
                binding.optionTwo.id -> playerAnswer = binding.optionTwo.text.toString()
                binding.optionThree.id -> playerAnswer = binding.optionThree.text.toString()
                binding.optionFour.id -> playerAnswer = binding.optionFour.text.toString()
                binding.optionFive.id -> playerAnswer = binding.optionFive.text.toString()
            }
            binding.nextButton.isEnabled = true
        }

        // Проверка, что ответ правильный(есть в мапе) и, что ответа нет в мапе с ответами пользователя
        // Иначе если пользователь вернулся и поменял верный на неправильный, то меняем его ответ и минусуем.
        binding.nextButton.setOnClickListener {
            checkAnswer()
            count = count?.plus(1)
            Log.v("AppVerbose", "$playerAnswer +++ ${mainActivityView?.playerAnswersCount}")
            quizFragmentInterface?.clickNextFragmentButton(count!!, playerAnswer) // , mainActivityView?.playerAnswersCount
        }

        binding.toolbar.getChildAt(1).setOnClickListener {
            count = count?.minus(1)
            quizFragmentInterface?.clickBackFragmentButton(count!!)
        }

        binding.previousButton.setOnClickListener {
            count = count?.minus(1)
            quizFragmentInterface?.clickBackFragmentButton(count!!)
        }

        return view
    }

    /**
     * метод для проверки правильности ответа при нажатии на кнопку Next / Submit
     */
    private fun checkAnswer() {
        if (playerAnswer == allRightAnswers[count] && mainActivityView?.playerAnswers?.containsValue(playerAnswer) == false) {
            mainActivityView?.playerAnswersCount = mainActivityView?.playerAnswersCount?.plus(1)!!
        } else if (playerAnswer != allRightAnswers[count]) {
            // этот if моя гордость, давай расскажу, что он значит. Есть такая проблема, что при возврате назад и смене правильного ответа
            // на неправильный, нужно минуснуть количество правильных ответов.
            // mainActivityView?.playerAnswers?.containsValue(allRightAnswers[count]) - смотрим, что в списке правильных ответов игрока
            // есть уже правильный ответ на этот вопрос, но сейчас игрок поменял ответ на неправильный playerAnswer != allRightAnswers[count]
            if ( mainActivityView?.playerAnswers?.containsValue(allRightAnswers[count]) == true) {
                mainActivityView?.playerAnswersCount = mainActivityView?.playerAnswersCount?.minus(1)!!
            }
        }
    }

    private fun initTheme() {
        when (count) {
            0 -> requireContext().setTheme(R.style.Theme_Quiz_First)
            1 -> requireContext().setTheme(R.style.Theme_Quiz_Second)
            2 -> requireContext().setTheme(R.style.Theme_Quiz_Third)
            3 -> requireContext().setTheme(R.style.Theme_Quiz_Four)
            4 -> requireContext().setTheme(R.style.Theme_Quiz_Five)
        }
    }

    private fun initViews() {
        binding.toolbar.title = "Question ${count?.plus(1)}"
        binding.question.text = allQuestions[count]
        binding.optionOne.text = allAnswers[count]?.get(0)
        binding.optionTwo.text = allAnswers[count]?.get(1)
        binding.optionThree.text = allAnswers[count]?.get(2)
        binding.optionFour.text = allAnswers[count]?.get(3)
        binding.optionFive.text = allAnswers[count]?.get(4)
        if (count == 4) binding.nextButton.text = "Submit"
        binding.nextButton.isEnabled = false
        if (count == 0) {
            binding.previousButton.isEnabled = false
            binding.toolbar.getChildAt(1).visibility = View.GONE
        }
    }

    // метод для установки уже выбранных значений, при нажатии на кнопку назад
    private fun checkCurrentAnswer() {
        if (mainActivityView?.playerAnswers?.containsKey(count) == true) {
            when (mainActivityView?.playerAnswers?.get(count)) {
                binding.optionOne.text -> {
                    binding.optionOne.isChecked = true
                    playerAnswer = binding.optionOne.text.toString()
                }
                binding.optionTwo.text -> {
                    binding.optionTwo.isChecked = true
                    playerAnswer = binding.optionTwo.text.toString()
                }
                binding.optionThree.text -> {
                    binding.optionThree.isChecked = true
                    playerAnswer = binding.optionThree.text.toString()
                }
                binding.optionFour.text -> {
                    binding.optionFour.isChecked = true
                    playerAnswer = binding.optionFour.text.toString()
                }
                binding.optionFive.text -> {
                    binding.optionFive.isChecked = true
                    playerAnswer = binding.optionFive.text.toString()
                }
            }
            binding.nextButton.isEnabled = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QuizFragmentInterface) {
            quizFragmentInterface = context
        }
        mainActivityView = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        quizFragmentInterface = null
        mainActivityView = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(count: Int): QuizFragment {
            val args = Bundle()
            args.putInt("COUNT", count)
            val fragment = QuizFragment()
            fragment.arguments = args
            return fragment
        }
    }
}