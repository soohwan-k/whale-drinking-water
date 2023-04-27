package org.tech.town.whaledrinkingwater.presentation.intake

import android.content.ContentValues.TAG
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.tech.town.whaledrinkingwater.R
import org.tech.town.whaledrinkingwater.databinding.ActivityIntakeBinding
import org.tech.town.whaledrinkingwater.databinding.ActivityMainBinding
import org.tech.town.whaledrinkingwater.databinding.DialogHomeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IntakeActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityIntakeBinding
    private lateinit var userDB: DatabaseReference


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child("Users")

        initView()
        initIntakeButton()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        val currentUserDB = userDB.child(getCurrentUserId()).child("date").child(getTodayDate())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    saveIntake(binding.todayIntakeTextView.text.toString())
                    return
                }
                binding.todayIntakeTextView.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveIntake(ml: String) {
        userDB.child(getCurrentUserId())
            .child("date")
            .child(getTodayDate())
            .setValue(ml)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initIntakeButton() {
        binding.intakeButton.setOnClickListener {
            setWaterIntakeDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    saveIntake((intake + todayIntake).toString())
                    initView()
                    dismiss()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayDate(): String {
        val now = LocalDate.now()
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

}