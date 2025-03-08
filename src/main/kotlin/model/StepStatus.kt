package model;

import kotlinx.serialization.Serializable;

@Serializable
data class StepStatus(
    val leftVehicles: List<String>
)
