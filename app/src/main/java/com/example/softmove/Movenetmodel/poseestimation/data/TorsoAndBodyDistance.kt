package org.tensorflow.lite.examples.poseestimation.data

data class TorsoAndBodyDistance(
    val maxTorsoYDistance: Float,
    val maxTorsoXDistance: Float,
    val maxBodyYDistance: Float,
    val maxBodyXDistance: Float
)
