@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_menu.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import org.json.JSONException
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.service.MyFirebaseMessagingService
import pl.idappstudio.jakdobrzesieznacie.util.FacebookUtil
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.util.*

class LoginMenuActivity : Activity() {

    private lateinit var callbackManager: CallbackManager

    private lateinit var auth: FirebaseAuth

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("PrivateResource", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.adMob_login_ad_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

                mInterstitialAd.show()

                setContentView(R.layout.activity_login_menu)

                initialize()

            }

            override fun onAdFailedToLoad(errorCode: Int) {

                setContentView(R.layout.activity_login_menu)

                initialize()

            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

                setContentView(R.layout.activity_login_menu)

                initialize()

            }

            override fun onAdClosed() {

            }
        }

    }

    @SuppressLint("PrivateResource", "InflateParams")
    fun initialize(){

        callbackManager = CallbackManager.Factory.create()

        auth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this@LoginMenuActivity)

        val dialogBuilder = AlertDialog.Builder(this@LoginMenuActivity,
            R.style.Base_Theme_MaterialComponents_Dialog
        )
        val inflater = this@LoginMenuActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading_login, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        textView11.setOnClickListener {

            val url = "http://idappstudio.pl/privacy_policy.html"
            val i = Intent(Intent.ACTION_VIEW)

            i.data = Uri.parse(url)

            startActivity(i)

        }

        textView15.setOnClickListener {

            val url2 = "http://idappstudio.pl/terms_and_conditions.html"
            val i = Intent(Intent.ACTION_VIEW)

            i.data = Uri.parse(url2)

            startActivity(i)

        }

        btn_stworz_konto.setOnClickListener {

            startActivity<RegisterActivity>()

        }

        btn_send.setOnClickListener {

            startActivity<LoginActivity>()

        }

        btn_facebook.setOnClickListener {

            alertDialog.show()

            LoginManager.getInstance().logInWithReadPermissions(this@LoginMenuActivity, Arrays.asList("email","user_gender","user_friends","public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {

                    handleFacebookAccessToken(loginResult.accessToken)
                    makeGraphRequest(loginResult.accessToken)

                }

                override fun onCancel() {

                    alertDialog.dismiss()

                    SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.cancel_login_facebook),
                        ColorSnackBar.ERROR,
                        R.drawable.ic_error_,
                        view
                    ) { }
                }

                override fun onError(error: FacebookException) {

                    if (error is FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut()

                            SnackBarUtil.setActivitySnack(
                                resources.getString(R.string.logout_facebook),
                                ColorSnackBar.WARING,
                                R.drawable.ic_warning,
                                view
                            ) { }

                            return
                        }
                    }

                    alertDialog.dismiss()

                    SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.error_facebook_login),
                        ColorSnackBar.ERROR,
                        R.drawable.ic_error_,
                        view
                    ) { }

                }
            })

        }

    }

    fun makeGraphRequest(token: AccessToken) : String {

        var gender = ""

        val request = GraphRequest.newMeRequest(token) { a, _ ->

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

                    FirestoreUtil.registerCurrentUser(uid, profile.name, profile.id, true, gender, "free", "") {

                        val registrationToken = FirebaseInstanceId.getInstance().token
                        MyFirebaseMessagingService.addTokenToFirestore(registrationToken)

                        if(it) {

                            UserUtil.initializeUser {

                                FacebookUtil.getFacebookFriends(token, profile.name){

                                    alertDialog.dismiss()

                                    SnackBarUtil.setActivitySnack(
                                        resources.getString(R.string.login_successful_facebook),
                                        ColorSnackBar.FACEBOOK,
                                        R.drawable.com_facebook_button_login_logo,
                                        view
                                    ) {

                                        startActivity(intentFor<MenuActivity>().newTask().clearTask())

                                    }

                                }

                            }

                        } else {

                            UserUtil.initialize {

                                alertDialog.dismiss()

                                SnackBarUtil.setActivitySnack(
                                    resources.getString(R.string.login_successful_facebook),
                                    ColorSnackBar.FACEBOOK,
                                    R.drawable.com_facebook_button_login_logo,
                                    view
                                ) {

                                    startActivity(intentFor<MenuActivity>().newTask().clearTask())

                                }

                            }

                        }

                    }

                } else {

                    alertDialog.dismiss()

                    SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.error_facebook_login),
                        ColorSnackBar.ERROR,
                        R.drawable.ic_error_,
                        view
                    ) { }

                }

            }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onBackPressed() {
        hideSystemUI()
        super.onBackPressed()
    }

    private fun hideSystemUI(){

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }

    override fun onWindowFocusChanged(hasFocus:Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

}
