@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother

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
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("InflateParams", "PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        auth = FirebaseAuth.getInstance()

        val dialogBuilder = AlertDialog.Builder(this, R.style.Base_Theme_MaterialComponents_Dialog)
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

                } else {

                    emailInput?.error = "Niepoprawny email"
                    emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        btn_login.setOnClickListener {

            alertDialog.show()

            auth.signInWithEmailAndPassword(emailInput.text.toString().trim(), passwordInput.text.toString().trim()).addOnCompleteListener { task ->

                if(task.isSuccessful){

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Zalogowano", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorAccent))
                    snackbar?.show()

                } else {

                    alertDialog.dismiss()

                    val snackbar: Snackbar? = Snackbar.make(view, "Upsss, coś poszło nie tak", 2500)
                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                    snackbar?.show()

                }

            }.addOnCanceledListener {

                alertDialog.dismiss()

                val snackbar: Snackbar? = Snackbar.make(view, "Anulowano logowanie", 2500)
                snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorYellow))
                snackbar?.show()

            }.addOnFailureListener {

                alertDialog.dismiss()

                val snackbar: Snackbar? = Snackbar.make(view, "Niepoprawny email bądź hasło", 2500)
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

}
