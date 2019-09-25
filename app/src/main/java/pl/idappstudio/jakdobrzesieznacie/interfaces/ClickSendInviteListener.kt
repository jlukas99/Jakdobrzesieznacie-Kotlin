package pl.idappstudio.jakdobrzesieznacie.interfaces

import pl.idappstudio.jakdobrzesieznacie.model.UserData

interface ClickSendInviteListener {

    fun onClickSendInvite(user: UserData, boolean: Boolean)

}