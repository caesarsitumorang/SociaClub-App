package com.rpl.sicfo.data

data class Message(
    val userId: String = "",
    val username: String = "",
    val message: String = "",
    val imageUrl: String? = null, // Added this line
    val timestamp: Long = 0
)
