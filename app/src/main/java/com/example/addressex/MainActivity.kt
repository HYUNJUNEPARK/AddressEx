package com.example.addressex

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.addressex.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Timber.d("activityResultLauncher result: $result")

        if (result.resultCode == Activity.RESULT_OK) {
            //웹뷰에서 가격 수정 PIN 확인 성공 응답 -> 가격 확정
            val address = result.data?.getStringExtra("ADDRESS")
            Timber.d("activityResultLauncher address: $address")

            binding.tvAddress.text = address
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etAddress.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }
}