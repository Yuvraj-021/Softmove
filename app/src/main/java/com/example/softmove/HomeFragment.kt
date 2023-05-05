package com.example.softmove

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.softmove.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.fragment_home.*
//import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

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