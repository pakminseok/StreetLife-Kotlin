package com.pakminseok.streetlife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null)
            moveMainPage(auth.currentUser)

        btn_login.setOnClickListener {
            var email = login_email.text.toString().trim()
            var password = login_password.text.toString().trim()

            if (login_email.text.isNotEmpty() && login_password.text.isNotEmpty()) {
                if(checkEmailValidation(email))
                    signInEmail(email, password)
                else
                    Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_LONG).show()
            }else if(login_email.text.isNotEmpty()){
                if(!checkEmailValidation(email))
                    Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_LONG).show()
            }
        }
        btn_sign_up.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun checkEmailValidation(email : String) : Boolean{
        var pattern : Pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        var matcher : Matcher = pattern.matcher(email)
        if(matcher.find())
            return true
        return  false
    }

    private fun signInEmail(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    moveMainPage(user)
                } else {
                    Toast.makeText(this, "가입하지 않은 이메일이거나 비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
