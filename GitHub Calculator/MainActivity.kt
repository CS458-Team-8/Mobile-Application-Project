package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastNumber = false
    private var stateError = false
    private var lastDot = false

    private lateinit var expression: Expression


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onEqualClick(view: View) {

        onEqual()
        binding.dataTv.text = binding.resultTv.text.toString().drop(1)

    }


    fun onAllclearClick(view: View) {

        binding.dataTv.text = ""
        binding.resultTv.text = ""
        stateError = false
        lastDot = false
        lastNumber = false
        binding.resultTv.visibility = View.GONE

    }


    fun onDigitClick(view: View) {

        if(stateError){

            binding.dataTv.text = (view as Button).text
            stateError = false
        } else {

            binding.dataTv.append((view as Button).text)

        }

        lastNumber = true
        onEqual()
    }
    fun onOperatorClick(view: View) {

        if(!stateError && lastNumber){

            binding.dataTv.append((view as Button).text)
            lastDot = false
            lastNumber = false
            onEqual()
        }

    }


    fun onBackClick(view: View) {

        binding.dataTv.text = binding.dataTv.text.toString().dropLast(1)

        try {

            val lastChar = binding.dataTv.text.toString().last ()

            if (lastChar.isDigit()){
                onEqual()
            }

        }catch (e : Exception) {

            binding.resultTv.text = ""
            binding.resultTv.visibility = View.GONE
            Log.e("last char error", e.toString())
        }
    }


    fun onClearClick(view: View) {

        binding.dataTv.text = ""
        lastNumber = false

    }

    @SuppressLint("SetTextI18n")
    fun onEqual(){

        if(lastNumber && !stateError) {

            val txt = binding.dataTv.text.toString()

            expression = ExpressionBuilder(txt).build()

            try {

                val result = expression.evaluate()

                binding.resultTv.visibility = View.VISIBLE

                binding.resultTv.text = "=$result"

            } catch (ex: ArithmeticException) {

                Log.e("evaluate error", ex.toString())
                binding.resultTv.text = "Error"
                stateError = true
                lastNumber = false
            }
        }
    }
}

