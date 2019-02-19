@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
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
import com.facebook.GraphRequest
import com.facebook.AccessToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pl.idappstudio.howwelldoyouknoweachother.util.FacebookUtil
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

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_gender","user_friends","public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {

                    handleFacebookAccessToken(loginResult.accessToken)
                    makeGraphRequest(loginResult.accessToken)

                }

                override fun onCancel() {

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Anulowano logowanie za pomocą FB", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorYellow))
                    snackbar?.show()
                }

                override fun onError(error: FacebookException) {

                    if (error is FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut()

                            alertDialog.dismiss()

                            val snackbar: Snackbar? = Snackbar.make(view, "Wylogowano z poprzedniego konta, spróbuj zalogować się ponownie!", 2500)
                            snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                            snackbar?.show()

                            return
                        }
                    }

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się zalogować!", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                    snackbar?.show()

                }
            })

        }

    }

    fun makeGraphRequest(token: AccessToken) : String {

        var gender = ""

        val request = GraphRequest.newMeRequest(token) { a, response ->

            try {
               gender = a.getString("gender")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        val parameters = Bundle()
        parameters.putString("fields", "gender")
        request.parameters = parameters
        request.executeAsync()

        return gender
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
                    var gender: String

                    gender = makeGraphRequest(token)

                    if(gender == "") {

                        gender = if (profile.firstName.substring(genderInt) == "a") "famle" else "male"

                    }

                    FirestoreUtil.registerCurrentUser(uid, profile.name, profile.id, true, gender, "free") {

                        alertDialog.dismiss()

                        val snackbar: Snackbar? = Snackbar.make(view, "Zalogowano za pomocą Facebook'a", 2500)
                        snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        snackbar?.show()

                        val registrationToken = FirebaseInstanceId.getInstance().token
                        MyFirebaseMessagingService.addTokenToFirestore(registrationToken)

                        if(it) {
                            FacebookUtil.getFacebookFriends(token, profile.name)
                        }

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
        hideNavigationBar()
    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.systemUiVisibility = flags

        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = flags
            }
        }
    }
}
