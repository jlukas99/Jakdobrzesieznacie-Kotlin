package pl.idappstudio.howwelldoyouknoweachother.interfaces

import pl.idappstudio.howwelldoyouknoweachother.model.UserData

interface ClickInviteListener {

    fun onClickInvite(user: UserData)
    fun onClickSendInvite(user: UserData, boolean: Boolean)

}