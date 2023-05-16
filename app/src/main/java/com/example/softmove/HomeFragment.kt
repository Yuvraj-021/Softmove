package com.example.softmove

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.example.softmove.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.fragment_home.*
//import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // card animation
        val animator = AnimatorInflater.loadAnimator(activity, R.animator.cardanim)
        animator.setTarget(homeBinding.streakcard)
        animator.start()

        // Retrieve the name from the arguments
        var name = arguments?.getString("name")
        homeBinding.homefragmentName.text=name

        // Pie Chart Code
        homeBinding.pieChart.setUsePercentValues(true)
        homeBinding.pieChart.description.isEnabled = false

        homeBinding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        homeBinding.pieChart.dragDecelerationFrictionCoef = 0.95f
        homeBinding.pieChart.setDrawHoleEnabled(true)
        //pieChart.holeColor = Color.WHITE
        homeBinding.pieChart.transparentCircleRadius = 61f

        val yValues = ArrayList<PieEntry>()
        yValues.add(PieEntry(34f, "Yoga"))
        yValues.add(PieEntry(23f, "Stretching"))
        yValues.add(PieEntry(14f, "Cycling"))
        yValues.add(PieEntry(35f, "Running"))

        val dataSet = PieDataSet(yValues, "Excercises")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = mutableListOf(
            Color.parseColor("#E91E63"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#00BCD4")
        )

        val data = PieData(dataSet)
        data.setValueTextColor(15)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.YELLOW)

        homeBinding.pieChart.data = data
        homeBinding.pieChart.rotationAngle = 180f
        homeBinding.pieChart.animateY(1400, Easing.EaseInOutQuad);
        homeBinding.pieChart.centerText="120 mins"

        homeBinding.pieChart.setCenterTextSize(24f)
        homeBinding.pieChart.invalidate()

        homeBinding.pieChart.setTransparentCircleColor(Color.WHITE);
        homeBinding.pieChart.setTransparentCircleAlpha(110);

        checkStreakCount()

        // streak
        onAttach(requireContext())
        updateStreakUI(requireContext())

        homeBinding.homefragYogaexcercise.setOnClickListener{
            val text: String = homeBinding.myImageViewText.getText().toString()
            val bitmap = (homeBinding.menuYoga.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        homeBinding.homefragStrechingexcercise.setOnClickListener{
            val text: String = homeBinding.streching.getText().toString()
            val bitmap = (homeBinding.menuStreching.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        homeBinding.homefragCyclingexcercise.setOnClickListener{
            val text: String = homeBinding.cycling.getText().toString()
            val bitmap = (homeBinding.menuCycling.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        homeBinding.homefragRunningexcercise.setOnClickListener{
            val text: String = homeBinding.running.getText().toString()
            val bitmap = (homeBinding.menuRunning.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        homeBinding.homefragMeditationexcercise.setOnClickListener{
            val text: String = homeBinding.guidedMeditation.getText().toString()
            val bitmap = (homeBinding.menuGuidedmeditation.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        return homeBinding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = requireContext().getSharedPreferences("StreakCount", Context.MODE_PRIVATE)
    }

    fun getRandomQuote(context: Context): String {
        val quotesArray = context.resources.getStringArray(R.array.motivating_quotes)
        val randomIndex = (0 until quotesArray.size).random()
        return quotesArray[randomIndex]
    }

    fun updateStreakUI(context:Context) {

        // Get the current date and the stored last updated date from SharedPreferences
        val currentDate = LocalDate.now()
        //Log.i("Current Date",currentDate.toString())
        val lastUpdatedDate = getLastUpdatedDateFromSharedPreferences() // Replace with your own method to retrieve the stored date

        // Check if the current date and last updated date are consecutive
        if (isConsecutiveDates(currentDate, lastUpdatedDate)) {
            // Update the streak count and display it in the UI
            val streakCount = getUpdatedStreakCount() // Replace with your own method to calculate and update the streak count
            homeBinding.streakcount.text = streakCount.toString()
            val quote = getRandomQuote(context)

            // Display the quote in your UI or send it as a notification
            homeBinding.quote.text=quote

            // Update the last updated date in SharedPreferences
            saveLastUpdatedDateToSharedPreferences(currentDate) // Replace with your own method to store the current date
        } else {
            val quote = getRandomQuote(context)

            // Display the quote in your UI or send it as a notification
            homeBinding.quote.text=quote

            // Reset the streak count to 0 and display it in the UI
            resetStreakCount()
            homeBinding.streakcount.text = "0"
        }
    }

    fun checkStreakCount(){
        val streakcount = sharedPreferences.getInt("StreakCount", 0)
        if(streakcount<=0){
            homeBinding.streakcard.visibility=View.GONE
        }
    }


    fun isConsecutiveDates(currentDate: LocalDate, lastUpdatedDate: LocalDate): Boolean {
        return currentDate.minusDays(1) == lastUpdatedDate
    }

    fun getLastUpdatedDateFromSharedPreferences(): LocalDate {
        // Retrieve the last updated date from SharedPreferences and convert it to LocalDate
        val dateString = sharedPreferences.getString("lastUpdatedDate", null)
        return if (dateString != null) {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            // Return a default date if the value is not found in SharedPreferences
            LocalDate.MIN
        }
    }

    fun getUpdatedStreakCount(): Int {
        // Calculate and return the updated streak count based on your logic
        // For example, you could retrieve the previous streak count from SharedPreferences and increment it
        val previousStreakCount = sharedPreferences.getInt("StreakCount", 0)
        return previousStreakCount + 1
    }

    fun saveLastUpdatedDateToSharedPreferences(date: LocalDate) {
        // Convert the LocalDate to a string format and store it in SharedPreferences
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        sharedPreferences.edit {
            putString("lastUpdatedDate", dateString)
        }
    }

    fun resetStreakCount() {
        // Reset the streak count in SharedPreferences to 0
        sharedPreferences.edit {
            putInt("StreakCount", 0)
        }
    }

//
//    private fun streakCount() {
//        val sharedPreferences = context.getSharedPreferences("MyStreakPrefs", Context.MODE_PRIVATE)
//        val streak = sharedPreferences.getInt("streak", 0)
//        val currentDate = LocalDate.now().toString()
//
//// Check if the user already completed an exercise today
//        val lastCompletedDate = sharedPreferences.getString("lastCompletedDate", "")
//
//        if (lastCompletedDate != currentDate) {
//            //val previousDate = currentDate.minus(1, ChronoUnit.DAYS)
//            // User completed an exercise today, update the streak
//            val newStreak = if (lastCompletedDate == currentDate.minusDays(1).toString()) {
//                // Streak maintained
//                streak + 1
//            } else {
//                // Streak broken, reset to 1
//                1
//            }
//
//            // Update the streak and last completed date in SharedPreferences
//            sharedPreferences.edit {
//                putInt("streak", newStreak)
//                putString("lastCompletedDate", currentDate)
//            }
//
//            // Update the UI to reflect the new streak value
//            updateStreakUI(newStreak)
//        }
//    }

    fun callIntent(text:String,bitmap:Bitmap){
        val intent = Intent(requireActivity(), Excercises_screen::class.java)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val compressedImageBytes: ByteArray = outputStream.toByteArray()
        intent.putExtra("text", text)
        intent.putExtra("image", compressedImageBytes)
        startActivity(intent)
    }


}