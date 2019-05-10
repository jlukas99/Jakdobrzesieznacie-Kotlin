@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.util.regex.Pattern

class LoginActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("InflateParams", "PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

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

        emailInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = emailInput?.text.toString().trim()

                if (isEmailValid(content)) {

                    emailInput.setBackgroundResource(R.drawable.input_overlay)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon)

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        btn_send.setOnClickListener {it2 ->

            if(!isEmailValid(emailInput?.text.toString().trim())) {

                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                SnackBarUtil.setActivitySnack("Niepoprawny adres email", ColorSnackBar.ERROR, R.mipmap.email_icon, it2){ }

                return@setOnClickListener

            } else if (passwordInput.text.toString().trim().isEmpty()) {

                passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                SnackBarUtil.setActivitySnack("Wpisz hasło", ColorSnackBar.ERROR, R.mipmap.password_icon, it2){ }

                return@setOnClickListener

            }

            alertDialog.show()

            auth.signInWithEmailAndPassword(emailInput.text.toString().trim(), passwordInput.text.toString().trim()).addOnSuccessListener {

                UserUtil.initializeUser {

                    alertDialog.dismiss()

                    if(it.uid != ""){

                        SnackBarUtil.setActivitySnack("Udało się zalogować", ColorSnackBar.SUCCES, R.drawable.ic_check_icon, it2){ }

                        startActivity(intentFor<MenuActivity>().newTask().clearTask())

                    } else {

                        SnackBarUtil.setActivitySnack("Wystapił problem z twoim kontem, skontaktuj się znami.", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                    }

                }

            }.addOnCanceledListener {

                alertDialog.dismiss()

                SnackBarUtil.setActivitySnack("Anulowano logowanie", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

            }.addOnFailureListener {

                alertDialog.dismiss()

                SnackBarUtil.setActivitySnack("Niepoprawny email bądź hasło", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

        }

        btn_register.setOnClickListener {

            startActivity<RegisterActivity>()

        }

        btn_reset_password.setOnClickListener {

            startActivity<ResetPasswordActivity>()
        }

    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
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
