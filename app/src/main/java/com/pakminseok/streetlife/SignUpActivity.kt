package com.pakminseok.streetlife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        btn_complete_sign_up.setOnClickListener {
            var email = sign_up_email.text.toString().trim()
            var password = sign_up__password.text.toString().trim()

            if (sign_up_email.text.isNotEmpty() && sign_up__password.text.isNotEmpty()) {
                if(checkEmailValidation(email))
                    signUpEmail(email, password)
                else
                    Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_LONG).show()
            }else if(sign_up_email.text.isNotEmpty()){
                if(!checkEmailValidation(email))
                    Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_LONG).show()
            }
        }
        btn_cancel_sign_up.setOnClickListener{
            moveLoginPage()
        }
    }

    private fun checkEmailValidation(email : String) : Boolean{
        var pattern : Pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        var matcher : Matcher = pattern.matcher(email)
        if(matcher.find())
            return true
        return  false
    }

    private fun signUpEmail(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "가입이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    moveLoginPage()
                } else {
                    Toast.makeText(this, "이미 가입된 이메일이 있습니다.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun moveLoginPage() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}