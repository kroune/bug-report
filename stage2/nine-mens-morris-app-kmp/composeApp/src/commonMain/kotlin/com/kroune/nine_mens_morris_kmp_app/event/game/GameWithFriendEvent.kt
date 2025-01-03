package com.kroune.nine_mens_morris_kmp_app.event.game

sealed interface GameWithFriendEvent {
    data object StartAnalyze: GameWithFriendEvent
    data object IncreaseAnalyzeDepth: GameWithFriendEvent
    data object DecreaseAnalyzeDepth: GameWithFriendEvent
    data class OnPieceClick(val index: Int): GameWithFriendEvent
    data object Undo: GameWithFriendEvent
    data object Redo: GameWithFriendEvent
    data object Back: GameWithFriendEvent
}