package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calculator.databinding.ActivityMainBinding;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean lastNumber = false;
    private boolean stateError = false;
    private boolean lastDot = false;

    private Expression expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onEqualClick(View view) {
        onEqual();
        binding.dataTv.setText(binding.resultTv.getText().toString().substring(1));
    }

    public void onAllclearClick(View view) {
        binding.dataTv.setText("");
        binding.resultTv.setText("");
        stateError = false;
        lastDot = false;
        lastNumber = false;
        binding.resultTv.setVisibility(View.GONE);
    }

    public void onDigitClick(View view) {
        if (stateError) {
            binding.dataTv.setText(((Button) view).getText());
            stateError = false;
        } else {
            binding.dataTv.append(((Button) view).getText());
        }
        lastNumber = true;
        onEqual();
    }

    public void onOperatorClick(View view) {
        if (!stateError && lastNumber) {
            binding.dataTv.append(((Button) view).getText());
            lastDot = false;
            lastNumber = false;
            onEqual();
        }
    }

    public void onBackClick(View view) {
        String text = binding.dataTv.getText().toString();
        if (!text.isEmpty()) {
            binding.dataTv.setText(text.substring(0, text.length() - 1));
            try {
                char lastChar = binding.dataTv.getText().toString().charAt(binding.dataTv.getText().length() - 1);
                if (Character.isDigit(lastChar)) {
                    onEqual();
                }
            } catch (Exception e) {
                binding.resultTv.setText("");
                binding.resultTv.setVisibility(View.GONE);
                Log.e("last char error", e.toString());
            }
        }
    }

    public void onClearClick(View view) {
        binding.dataTv.setText("");
        lastNumber = false;
    }

    @SuppressLint("SetTextI18n")
    private void onEqual() {
        if (lastNumber && !stateError) {
            String txt = binding.dataTv.getText().toString();
            expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                binding.resultTv.setVisibility(View.VISIBLE);
                binding.resultTv.setText("=" + result);
            } catch (ArithmeticException ex) {
                Log.e("evaluate error", ex.toString());
                binding.resultTv.setText("Error");
                stateError = true;
                lastNumber = false;
            }
        }
    }
}
