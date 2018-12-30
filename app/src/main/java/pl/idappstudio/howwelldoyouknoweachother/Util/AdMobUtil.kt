package pl.idappstudio.howwelldoyouknoweachother.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import pl.idappstudio.howwelldoyouknoweachother.R

class AdMobUtil(context: Context) {

    companion object {
        lateinit var interstitialAd: InterstitialAd
    }

    init {
        createAd(context)
    }

    fun createAd(context: Context) {
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = context.resources.getString(R.string.adMob_menu_ad_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    fun getAd(): InterstitialAd {
        return interstitialAd
    }

}