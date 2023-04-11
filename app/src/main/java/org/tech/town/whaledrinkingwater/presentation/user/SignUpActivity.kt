package org.tech.town.whaledrinkingwater.presentation.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.tech.town.whaledrinkingwater.databinding.ActivitySignUpBinding
import org.tech.town.whaledrinkingwater.presentation.main.MainActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        initSignUpButton()
    }

    private fun initSignUpButton() {
        binding.signupButton.setOnClickListener {
            val email = binding.idEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다..", Toast.LENGTH_SHORT).show()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task2 ->
                                if (task2.isSuccessful) {
                                    finish()
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        this,
                                        "로그인에 실패했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
}