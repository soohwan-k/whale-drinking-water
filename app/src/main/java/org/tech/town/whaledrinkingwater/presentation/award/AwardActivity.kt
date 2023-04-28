package org.tech.town.whaledrinkingwater.presentation.award


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.tech.town.whaledrinkingwater.DBKey.Companion.AWARDS
import org.tech.town.whaledrinkingwater.DBKey.Companion.FIRST_DRINK
import org.tech.town.whaledrinkingwater.DBKey.Companion.TOTAL_INTAKE_AWARD1
import org.tech.town.whaledrinkingwater.DBKey.Companion.TOTAL_INTAKE_AWARD2
import org.tech.town.whaledrinkingwater.DBKey.Companion.TOTAL_INTAKE_AWARD3
import org.tech.town.whaledrinkingwater.DBKey.Companion.USERS
import org.tech.town.whaledrinkingwater.R
import org.tech.town.whaledrinkingwater.data.award.AwardAdapter
import org.tech.town.whaledrinkingwater.data.award.AwardItem
import org.tech.town.whaledrinkingwater.databinding.ActivityAwardBinding

class AwardActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityAwardBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var adapter: AwardAdapter
    private val awardItems = mutableListOf<AwardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAwardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(USERS)

        initAwardRecyclerView()


        initView()

    }


    private fun initAwardRecyclerView() {
        adapter = AwardAdapter()
        binding.awardRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.awardRecyclerView.adapter = adapter
    }


    private fun initView() {
        val currentUserDB = userDB.child(getCurrentUserId()).child(AWARDS)
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: ${snapshot.child(FIRST_DRINK).value}")
                if (snapshot.child(FIRST_DRINK).value != null && snapshot.child(FIRST_DRINK).value == true) {
                    awardItems.add(AwardItem(R.drawable.ic_fab_intake))
                }
                if (snapshot.child(TOTAL_INTAKE_AWARD1).value != null && snapshot.child(
                        TOTAL_INTAKE_AWARD1
                    ).value == true
                ) {
                    awardItems.add(AwardItem(R.drawable.ic_fab_awards))
                }
                if (snapshot.child(TOTAL_INTAKE_AWARD2).value != null && snapshot.child(
                        TOTAL_INTAKE_AWARD2
                    ).value == true
                ) {
                    awardItems.add(AwardItem(R.drawable.ic_fab_main))
                }
                if (snapshot.child(TOTAL_INTAKE_AWARD3).value != null && snapshot.child(
                        TOTAL_INTAKE_AWARD3
                    ).value == true
                ) {
                    awardItems.add(AwardItem(R.drawable.ic_fab_log))
                }
                adapter.submitList(awardItems)
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

}