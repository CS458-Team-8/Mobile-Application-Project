package com.example.firebaselogin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Calculator extends AppCompatActivity {

    private TextView dataTv;
    private TextView resultTv;
    private boolean lastNumber = false;
    private boolean stateError = false;
    private boolean lastDot = false;

    private Expression expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator); // Set the layout directly

        // Initialize views using findViewById()
        dataTv = findViewById(R.id.data_tv);
        resultTv = findViewById(R.id.result_tv);

        // Initialize digit buttons
        Button btn0 = findViewById(R.id.btn_0);
        Button btn1 = findViewById(R.id.btn_1);
        Button btn2 = findViewById(R.id.btn_2);
        Button btn3 = findViewById(R.id.btn_3);
        Button btn4 = findViewById(R.id.btn_4);
        Button btn5 = findViewById(R.id.btn_5);
        Button btn6 = findViewById(R.id.btn_6);
        Button btn7 = findViewById(R.id.btn_7);
        Button btn8 = findViewById(R.id.btn_8);
        Button btn9 = findViewById(R.id.btn_9);

        // Initialize operator buttons
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnSubtract = findViewById(R.id.btn_subtract);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnModulo = findViewById(R.id.btn_modulo);

        // Initialize other buttons
        Button btnEqual = findViewById(R.id.btn_equal);
        Button btnAllClear = findViewById(R.id.btn_allclear);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnBack = findViewById(R.id.btn_back);
        Button btnDot = findViewById(R.id.btn_dot);

        // Set listeners for digit buttons
        btn0.setOnClickListener(this::onDigitClick);
        btn1.setOnClickListener(this::onDigitClick);
        btn2.setOnClickListener(this::onDigitClick);
        btn3.setOnClickListener(this::onDigitClick);
        btn4.setOnClickListener(this::onDigitClick);
        btn5.setOnClickListener(this::onDigitClick);
        btn6.setOnClickListener(this::onDigitClick);
        btn7.setOnClickListener(this::onDigitClick);
        btn8.setOnClickListener(this::onDigitClick);
        btn9.setOnClickListener(this::onDigitClick);

        // Set listeners for operator buttons
        btnAdd.setOnClickListener(this::onOperatorClick);
        btnSubtract.setOnClickListener(this::onOperatorClick);
        btnMultiply.setOnClickListener(this::onOperatorClick);
        btnDivide.setOnClickListener(this::onOperatorClick);
        btnModulo.setOnClickListener(this::onOperatorClick);

        // Set listeners for other buttons
        btnEqual.setOnClickListener(this::onEqualClick);
        btnAllClear.setOnClickListener(this::onAllclearClick);
        btnClear.setOnClickListener(this::onClearClick);
        btnBack.setOnClickListener(this::onBackClick);
        btnDot.setOnClickListener(this::onDigitClick); // Treat dot as a digit here
    }

    public void onEqualClick(View view) {
        onEqual();
        dataTv.setText(resultTv.getText().toString().substring(1));
    }

    public void onAllclearClick(View view) {
        dataTv.setText("");
        resultTv.setText("");
        stateError = false;
        lastDot = false;
        lastNumber = false;
        resultTv.setVisibility(View.GONE);
    }

    public void onClearClick(View view) {
        dataTv.setText("");
        lastNumber = false;
    }

    public void onDigitClick(View view) {
        if (stateError) {
            dataTv.setText(((Button) view).getText());
            stateError = false;
        } else {
            dataTv.append(((Button) view).getText());
        }
        lastNumber = true;
        onEqual(); // Automatically update the result as the digits are pressed
    }

    public void onOperatorClick(View view) {
        if (!stateError && lastNumber) {
            dataTv.append(((Button) view).getText());
            lastDot = false;
            lastNumber = false;
            onEqual(); // Automatically update the result when operators are pressed
        }
    }

    public void onBackClick(View view) {
        String text = dataTv.getText().toString();
        if (!text.isEmpty()) {
            dataTv.setText(text.substring(0, text.length() - 1));
            try {
                char lastChar = dataTv.getText().toString().charAt(dataTv.getText().length() - 1);
                if (Character.isDigit(lastChar)) {
                    onEqual();
                }
            } catch (Exception e) {
                resultTv.setText("");
                resultTv.setVisibility(View.GONE);
                Log.e("last char error", e.toString());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void onEqual() {
        if (lastNumber && !stateError) {
            String txt = dataTv.getText().toString();
            expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                resultTv.setVisibility(View.VISIBLE);
                resultTv.setText("=" + result);
            } catch (ArithmeticException ex) {
                Log.e("evaluate error", ex.toString());
                resultTv.setText("Error");
                stateError = true;
                lastNumber = false;
            }
        }
    }
}
