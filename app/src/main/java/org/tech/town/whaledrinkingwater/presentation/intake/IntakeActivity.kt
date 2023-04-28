package org.tech.town.whaledrinkingwater.presentation.intake

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import org.tech.town.whaledrinkingwater.DBKey
import org.tech.town.whaledrinkingwater.DBKey.Companion.AWARDS
import org.tech.town.whaledrinkingwater.DBKey.Companion.DATE
import org.tech.town.whaledrinkingwater.DBKey.Companion.TOTAL_INTAKE
import org.tech.town.whaledrinkingwater.DBKey.Companion.USERS
import org.tech.town.whaledrinkingwater.R
import org.tech.town.whaledrinkingwater.databinding.ActivityIntakeBinding
import org.tech.town.whaledrinkingwater.databinding.DialogHomeBinding
import org.tech.town.whaledrinkingwater.presentation.award.AwardActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class IntakeActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityIntakeBinding
    private lateinit var userDB: DatabaseReference

    private var isFabClicked = false
    private val rotateOpenAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_rotate_open_animation) }
    private val rotateCloseAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_rotate_close_animation) }
    private val fromBottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_from_bottom_animation) }
    private val toBottomAnimation: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_to_bottom_animation) }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(USERS)


        initView()
        initIntakeButton()
        giveAwards()
        initFabClickEvent()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        val currentUserDB = userDB.child(getCurrentUserId()).child(DATE).child(getTodayDate())
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
    private fun giveAwards() {
        val currentUserDB = userDB.child(getCurrentUserId()).child(TOTAL_INTAKE)
        currentUserDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    saveAward(DBKey.FIRST_DRINK)
                    if (snapshot.value.toString().toInt() >= 1000) {
                        saveAward(DBKey.TOTAL_INTAKE_AWARD1)
                    }
                    if (snapshot.value.toString().toInt() >= 10000) {
                        saveAward(DBKey.TOTAL_INTAKE_AWARD2)
                    }
                    if (snapshot.value.toString().toInt() > 100000) {
                        saveAward(DBKey.TOTAL_INTAKE_AWARD3)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveTotalIntake(ml: String) {
        var totalIntake: String
        userDB.child(getCurrentUserId())
            .child(TOTAL_INTAKE).get().addOnSuccessListener {
                if (it.value != null) {
                    totalIntake = it.value.toString()
                }else{
                    totalIntake = "0"
                }
                userDB.child(getCurrentUserId())
                    .child(TOTAL_INTAKE)
                    .setValue(ml.toInt() + totalIntake.toInt())
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveIntake(ml: String) {
        userDB.child(getCurrentUserId())
            .child(DATE)
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
                    saveTotalIntake(intake.toString())
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

    private fun initFabClickEvent() {
        with(binding) {
            intakeFab.setOnClickListener { onMainFabClicked() }
            awardsFab.setOnClickListener {
                startActivity(Intent(this@IntakeActivity, AwardActivity::class.java))
            }
        }
    }

    private fun onMainFabClicked() {
        setVisibility()
        setAnimation()

        isFabClicked = !isFabClicked
    }

    private fun setVisibility() {
        val visibility = if (!isFabClicked) View.VISIBLE else View.INVISIBLE

        with(binding) {
            awardsFab.visibility = visibility
        }
    }

    private fun setAnimation() {
        val rotateAnimation = if (!isFabClicked) rotateOpenAnimation else rotateCloseAnimation
        val animation = if (!isFabClicked) fromBottomAnimation else toBottomAnimation

        with(binding) {
            intakeFab.startAnimation(rotateAnimation)
            awardsFab.startAnimation(animation)
        }
    }

    private fun saveAward(award: String) {
        userDB.child(getCurrentUserId())
            .child(AWARDS)
            .child(award).setValue(true)
    }




}