package com.muratguzel.kotlinworkmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val data = Data.Builder().putInt("intKey", 1).build()
        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        /* val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
             .setConstraints(constraints)
             .setInputData(data)
             //.setInitialDelay(5,TimeUnit.HOURS)
             //.addTag("myTag")
             .build()
         WorkManager.getInstance(this).enqueue(myWorkRequest)*/

        val myWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<RefreshDatabase>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id)
            .observe(this, Observer {
                if (it.state == WorkInfo.State.RUNNING) {
                    println("running")
                } else if (it.state == WorkInfo.State.FAILED) {
                    println("failed")
                } else if (it.state == WorkInfo.State.SUCCEEDED) {
                    println("succeeded")
                } else if(it.state == WorkInfo.State.CANCELLED){
                    println("Cancelled")
                }

            })

        WorkManager.getInstance(this).cancelAllWork()
        //Chaining -> workmanager sen şu işlemle başla o bitsin bunu yap o ad bitince sunu yap zincirleme periyodik requestte olmaz sadece one time requestte olur
      /*  val oneTimeRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).beginWith(oneTimeRequest)
            .then(oneTimeRequest)
            .then(oneTimeRequest)
            .enqueue()*/
    }
}