package com.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.event.other.ViewAccountScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase

class ViewAccountScreenComponent(
    val onNavigationBack: () -> Unit,
    val isOwnAccount: Boolean,
    accountId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {

    private val _accountName = mutableStateOf<Result<String>?>(null)
    var accountName by _accountName
    private val _accountRating = mutableStateOf<Result<Long>?>(null)
    var accountRating by _accountRating
    private var _accountCreationDate = mutableStateOf<Result<Triple<Int, Int, Int>>?>(null)
    var accountCreationDate by _accountCreationDate
    private var _accountPicture = mutableStateOf<Result<ByteArray>?>(null)
    var accountPicture by _accountPicture

    private val accountInfoUseCase = AccountInfoUseCase(
        accountId,
        playerInfo = AccountInfoUseCase.PlayerInfo(
            name = _accountName,
            rating = _accountRating,
            creationDate = _accountCreationDate,
            accountPicture = _accountPicture
        )
    )

    fun onEvent(event: ViewAccountScreenEvent) {
        when (event) {
            ViewAccountScreenEvent.Logout -> {
                jwtTokenInteractor.logout()
                onNavigationBack()
            }

            ViewAccountScreenEvent.ReloadCreationDate -> {
                accountInfoUseCase.reloadCreationDate()
            }

            ViewAccountScreenEvent.ReloadIcon -> {
                accountInfoUseCase.reloadPicture()
            }

            ViewAccountScreenEvent.ReloadName -> {
                accountInfoUseCase.reloadName()
            }

            ViewAccountScreenEvent.ReloadRating -> {
                accountInfoUseCase.reloadRating()
            }

            ViewAccountScreenEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(ViewAccountScreenEvent.Back)
    }
}