package org.tech.town.whaledrinkingwater.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import org.tech.town.whaledrinkingwater.R
import org.tech.town.whaledrinkingwater.databinding.ActivityMainBinding
import org.tech.town.whaledrinkingwater.databinding.ActivitySignUpBinding
import org.tech.town.whaledrinkingwater.databinding.DialogHomeBinding
import org.tech.town.whaledrinkingwater.presentation.user.LoginActivity

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initIntakeButton()
    }

    private fun initIntakeButton() {
        binding.intakeButton.setOnClickListener {
            setWaterIntakeDialog()
        }
    }

    private fun setWaterIntakeDialog() {
        val dialogBinding = DialogHomeBinding.inflate(layoutInflater)
        val intakeEditText = dialogBinding.dialogEditText

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(R.string.main_today_intake)
            setView(dialogBinding.root)
            setCancelable(false)
            setPositiveButton(R.string.positive_btn, null)
            setNegativeButton(R.string.negative_btn, null)
        }.create().apply {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (intakeEditText.text.toString() == "") {
                    Toast.makeText(context, "물 섭취량을 입력해 주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val intake = intakeEditText.text.toString().toInt()
                    val todayIntake = binding.todayIntakeTextView.text.toString().toInt()
                    binding.todayIntakeTextView.text = (intake + todayIntake).toString()
                    dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}