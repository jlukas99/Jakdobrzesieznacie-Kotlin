@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*
import pl.idappstudio.howwelldoyouknoweachother.R
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class ResetPasswordActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("PrivateResource", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()

        val dialogBuilder = AlertDialog.Builder(this, R.style.Base_Theme_MaterialComponents_Dialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = emailInput?.text.toString().trim()

                if (isEmailValid(content)) {

                    emailInput.setBackgroundResource(R.drawable.input_overlay)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    emailInput?.error = "Niepoprawny email"
                    emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        btn_send.setOnClickListener {

            emailInput.setBackgroundResource(R.drawable.input_overlay)
            emailImage.setBackgroundResource(R.drawable.input_overlay_icon)

            if (emailInput.text.toString().trim().isEmpty()) {

                emailInput?.error = "Wpisz email"
                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                return@setOnClickListener

            }

            alertDialog.show()

            auth.sendPasswordResetEmail(emailInput.text.toString().trim()).addOnSuccessListener {

                alertDialog.dismiss()

                val snackbar: Snackbar? = Snackbar.make(view, "Wysłano wiadomość, sprawdź mail'a", 2500)
                snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorAccent))
                snackbar?.show()

                Timer("startIntent", false).schedule(700) {

                    finish()

                }

            }.addOnCanceledListener {

                alertDialog.dismiss()

                val snackbar: Snackbar? = Snackbar.make(view, "Anulowano przywracanie hasła", 2500)
                snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorYellow))
                snackbar?.show()

            }.addOnFailureListener {

                alertDialog.dismiss()

                val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się wysłać mail'a", 2500)
                snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar?.show()

            }

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
