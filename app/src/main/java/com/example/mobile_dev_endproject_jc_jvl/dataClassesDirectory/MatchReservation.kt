package com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory

data class MatchReservation(
    val clubName: String = "",
    val clubEstablishmentName: String = "",
    val courtName: String = "",
    val matchId: String = "",
    val clubEstablishmentAddress: String = "",
    val timeslot: String = "",
    val dateReservation: String = "",
    val participators: Map<String, Any> = emptyMap()
)
