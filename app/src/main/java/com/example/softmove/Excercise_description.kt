package com.example.softmove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.softmove.Movenetmodel.poseestimation.PoseDetection
import com.example.softmove.databinding.ActivityExcerciseDescriptionBinding
import com.example.softmove.databinding.ActivityExcercisesScreenBinding


class Excercise_description : AppCompatActivity() {

    private lateinit var binding: ActivityExcerciseDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exerciseName = intent.getStringExtra("EXERCISE_NAME")
        val exerciseType = intent.getStringExtra("EXERCISE_TYPE")
        val exerciseTime = intent.getStringExtra("EXERCISE_TIME")
        val exerciseSets = intent.getIntExtra("EXERCISE_SETS",0)
        val exerciseReps = intent.getIntExtra("EXERCISE_REPS",0)
        val exerciseAnimation = intent.getStringExtra("EXERCISE_ANIMATION")
        //Toast.makeText(applicationContext,exerciseSets,Toast.LENGTH_SHORT).show()



        binding.setsText.text=exerciseSets.toString()+ " Sets"
        binding.setsDesc.text= "Each set of "+exerciseReps.toString()+" Reps"

        binding.startbutton.setOnClickListener{
            val ins= Intent(applicationContext, PoseDetection::class.java)
            ins.putExtra("EXERCISE_NAME",exerciseName)
            ins.putExtra("EXERCISE_TYPE",exerciseType)
            startActivity(ins)
        }

    }
}