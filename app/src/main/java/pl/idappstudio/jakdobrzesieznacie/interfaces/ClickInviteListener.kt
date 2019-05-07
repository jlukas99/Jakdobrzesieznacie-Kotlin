package pl.idappstudio.jakdobrzesieznacie.interfaces

import pl.idappstudio.jakdobrzesieznacie.model.UserData

interface ClickInviteListener {

    fun onClickInvite(user: UserData)
    fun onClickSendInvite(user: UserData, boolean: Boolean)

}