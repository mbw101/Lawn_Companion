package com.mbw101.lawn_companion.weather

data class AlertsItem(val start: Int = 0,
                      val description: String = "",
                      val senderName: String = "",
                      val end: Int = 0,
                      val event: String = "",
                      val tags: List<String>?)