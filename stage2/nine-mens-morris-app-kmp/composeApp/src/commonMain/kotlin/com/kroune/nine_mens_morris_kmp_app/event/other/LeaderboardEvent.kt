package com.kroune.nine_mens_morris_kmp_app.event.other

sealed interface LeaderboardEvent {
    data class ReloadIcon(val index: Int): LeaderboardEvent
    data class ReloadName(val index: Int): LeaderboardEvent
    data class ReloadRating(val index: Int): LeaderboardEvent
    data object Back: LeaderboardEvent
}