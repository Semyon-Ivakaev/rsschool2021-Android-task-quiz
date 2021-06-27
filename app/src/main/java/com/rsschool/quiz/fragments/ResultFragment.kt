package com.rsschool.quiz.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rsschool.quiz.MainActivity
import com.rsschool.quiz.data.AllDataApp
import com.rsschool.quiz.databinding.ResultFragmentBinding

class ResultFragment: Fragment() {
    // Interface
    private var mainActivityView: MainActivity? = null

    private var allDataApp = AllDataApp()
    private var result: Int? = null

    private var _binding: ResultFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ResultFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        result = if (arguments?.getInt("RESULT")!! < 0) 0 else arguments?.getInt("RESULT")

        initView()
        binding.share.setOnClickListener {
            mainActivityView?.clickOnShareButton(generateQuizReport())
        }

        binding.back.setOnClickListener {
            mainActivityView?.clickOnBackButton()
        }

        binding.close.setOnClickListener {
            mainActivityView?.clickOnCloseButton()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivityView = context
        }
    }

    private fun initView() {
        binding.result.text = "$result%"
    }

    private fun generateQuizReport(): String {
        var message = "Your result: $result%\n\n"
        for (i in 1..5) {
            message += "$i) ${allDataApp.questions[i - 1]}\n"
            message += "Your answer: ${mainActivityView?.playerAnswers?.get(i - 1)}\n\n"

        }
        return message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(result: Int?, playerAnswers: MutableMap<Int, String>): ResultFragment {
            Log.v("App", "In Result $result")
            val fragment = ResultFragment()

            val args = Bundle()
            if (result != null) {
                args.putInt("RESULT", result)
            }

            fragment.arguments = args
            return fragment
        }
    }
}