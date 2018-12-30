@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_menu.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.service.MyFirebaseMessagingService
import pl.idappstudio.howwelldoyouknoweachother.util.AdMobUtil
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import java.util.*

class LoginMenuActivity : Activity() {

    private lateinit var callbackManager: CallbackManager

    private lateinit var auth: FirebaseAuth

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("PrivateResource", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        val adManager = AdMobUtil(this@LoginMenuActivity)
        val ad: InterstitialAd = adManager.getAd()

        ad.adListener = object: AdListener() {
            override fun onAdLoaded() {
                ad.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adManager.createAd(this@LoginMenuActivity)
            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {
                adManager.createAd(this@LoginMenuActivity)
            }
        }

        callbackManager = CallbackManager.Factory.create()

        auth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        val dialogBuilder = AlertDialog.Builder(this,
            R.style.Base_Theme_MaterialComponents_Dialog
        )
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading_login, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        btn_stworz_konto.setOnClickListener {

            startActivity<RegisterActivity>()

        }

        btn_send.setOnClickListener {

            startActivity<LoginActivity>()

        }

        btn_facebook.setOnClickListener {

            alertDialog.show()

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {

                    handleFacebookAccessToken(loginResult.accessToken)

                }

                override fun onCancel() {

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Anulowano logowanie za pomocą FB", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorYellow))
                    snackbar?.show()
                }

                override fun onError(error: FacebookException) {

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się zalogować!", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                    snackbar?.show()
                }
            })

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val profile = Profile.getCurrentProfile()

                    val uid = auth.currentUser?.uid.toString()
                    val genderInt = profile.firstName.lastIndex
                    val gender: String

                    gender = if(profile.firstName.substring(genderInt) == "a") "famle" else "male"

                    FirestoreUtil.registerCurrentUser(uid, profile.name, profile.id, true, gender, "free") {

                        alertDialog.dismiss()

                        val snackbar: Snackbar? = Snackbar.make(view, "Zalogowano za pomocą Facebook'a", 2500)
                        snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        snackbar?.show()

                        val registrationToken = FirebaseInstanceId.getInstance().token
                        MyFirebaseMessagingService.addTokenToFirestore(registrationToken)

                        startActivity(intentFor<MenuActivity>().newTask().clearTask())

                    }

                } else {

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się zalogować!", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                    snackbar?.show()

                }

            }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
