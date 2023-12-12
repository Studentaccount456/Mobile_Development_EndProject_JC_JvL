package com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory

data class Match(
    val clubName: String = "",
    val clubEstablishmentName: String = "",
    val courtName: String = "",
    val matchId: String = "",
    val clubEstablishmentAddress: String = "",
    val timeslot: String = "",
    val dateReservation: String = "",
    val typeOfMatch: String = "",
    val gendersAllowed: String = "",
    val participators: Map<String, String> = emptyMap()
)

