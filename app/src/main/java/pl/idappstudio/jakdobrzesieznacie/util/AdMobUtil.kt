package pl.idappstudio.jakdobrzesieznacie.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class AdMobUtil(context: Context, id: String) {

    companion object {

        lateinit var interstitialAd: InterstitialAd

    }

    init {
        createAd(context, id)
    }

    fun createAd(context: Context, id: String) {

        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = id
        interstitialAd.loadAd(AdRequest.Builder().build())

    }

    fun getAd(): InterstitialAd {
        return interstitialAd
    }

}