package com.example.softmove.Movenetmodel.poseestimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_ADD
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.softmove.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.data.PoseResult
import org.tensorflow.lite.examples.poseestimation.ml.*
import java.util.*

class PoseDetection : AppCompatActivity(), TextToSpeech.OnInitListener {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     **/
    private var modelPos = 1

    /** Default device is CPU */
    private var device = Device.CPU

    private var tts: TextToSpeech? = null

    public lateinit var exerciseType:String
    public lateinit var exerciseName:String

    private lateinit var tvClassificationValue1: TextView
    private lateinit var classficationRes : TextView
    private lateinit var swClassification: SwitchCompat
    private lateinit var vClassificationOption: View
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }

    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posedetection)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        surfaceView = findViewById(R.id.surfaceView)
        classficationRes = findViewById(R.id.result)
        tvClassificationValue1 = findViewById(R.id.tvClassificationValue1)
        swClassification = findViewById(R.id.swPoseClassification)
        vClassificationOption = findViewById(R.id.vClassificationOption)
        swClassification.setOnCheckedChangeListener(setClassificationListener)

        exerciseName = intent.getStringExtra("EXERCISE_NAME").toString()
        exerciseType = intent.getStringExtra("EXERCISE_TYPE").toString()

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        tts = TextToSpeech(this, this)
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {

                        }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            cameraSource?.result?.exerciseType = exerciseType
                            Log.i("POSE_DETECTION:::",exerciseName)
                            if (cameraSource?.result?.exerciseType.toString() == "Yoga"){
                                poseLabels?.sortedByDescending { it.second }?.let {
                                    var result : String = "- "+exerciseName+" (1.00)" ;
                                    tvClassificationValue1.text = getString(
                                        R.string.tfe_pe_tv_classification_value,
                                        convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                    )
                                    if (result == "- warrior (1.00)" && result == tvClassificationValue1.text){
                                        poseResult("Warrior pose detected.");
                                    }else if (result == "- cobra (1.00)" && result == tvClassificationValue1.text){
                                        poseResult("Cobra pose detected.");
                                    }else if(result == "- chair (1.00)" && result == tvClassificationValue1.text){
                                        poseResult("Chair pose detected");
                                    }else if (result == "- tree (1.00)" && result == tvClassificationValue1.text){
                                        poseResult("Tree pose detected");
                                    }else if (result == "- dog (1.00)" && result == tvClassificationValue1.text){
                                        poseResult("Dog pose detected")
                                    }
                                }
                            }
                            else if (cameraSource?.result?.exerciseType.toString() == "Streching"){
                                // Pose Detection Result
                                var pose : String = exerciseName

                                //var side : String = "Right"
                                if (exerciseName == "Right Hand Stretching"){
                                    tvClassificationValue1.text = cameraSource?.result?.rightHandStretchingResult.toString()
                                }else if (pose == "Left Hand Stretching"){
                                    tvClassificationValue1.text = cameraSource?.result?.leftHandStretchingResult.toString()
                                }else if (pose == "Right Leg Stretching"){
                                    tvClassificationValue1.text =   cameraSource?.result?.rightLegStretchingResult.toString()
                                }else if (pose == "Left Leg Stretching"){
                                    tvClassificationValue1.text = cameraSource?.result?.leftLegStretchingResult.toString()
                                }
//                            poseResult(tvClassificationValue1.text.toString())
                            }
                        }

                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun poseResult (res : String) {
        tts!!.speak(res, TextToSpeech.QUEUE_FLUSH, null,"")
        tts!!.playSilentUtterance(2000, QUEUE_ADD, null);
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
    }


    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                MoveNet.create(this, device, ModelType.Thunder)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                MoveNet.create(this, device, ModelType.Thunder)
            }

            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

//     Show/hide the pose classification option.
    private fun showPoseClassifier(isVisible: Boolean) {
        vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) {
            swClassification.isChecked = false
        }
    }

//     Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        tvClassificationValue1.visibility = visibility
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }

    override fun onInit(p0: Int) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
//                btnSpeak!!.isEnabled = true
            }
        }
}