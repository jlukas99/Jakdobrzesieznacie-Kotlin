@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern
import com.google.firebase.auth.FirebaseAuth
import android.support.design.widget.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.service.MyFirebaseMessagingService
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.StorgeUtil
import java.io.ByteArrayOutputStream

class RegisterActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    private var fileUri: Uri? = null

    @SuppressLint("InflateParams", "PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        val adapter = ArrayAdapter.createFromResource(this,
            R.array.gender_list,
            R.layout.gender_list_layout
        )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(this, R.style.Base_Theme_MaterialComponents_Dialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading_create_account, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        selectImage.setOnClickListener {

            val intent = Intent().apply {

                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

            }

            startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), RC_SELECT_IMAGE)
        }

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

        passwordInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = passwordInput?.text.toString().trim()

                if (isPasswordValid(content)) {

                    passwordInput.setBackgroundResource(R.drawable.input_overlay)
                    passwordImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    passwordInput?.error = "Hasło musi składać się przynajmniej z 6 liter lub cyfr"
                    passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                    passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        repasswordInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = repasswordInput?.text.toString().trim()
                val content2 = passwordInput?.text.toString().trim()

                if (isRePasswordValid(content, content2)) {

                    repasswordInput.setBackgroundResource(R.drawable.input_overlay)
                    repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    repasswordInput?.error = "Hasła się nie zgadzają"
                    repasswordInput.setBackgroundResource(R.drawable.input_overlay_error)
                    repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        btn_send.setOnClickListener {

            var error = 0

            if(!isEmailValid(emailInput?.text.toString().trim())) {

                error++

                emailInput?.error = "Niepoprawny email"
                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(!isPasswordValid(passwordInput?.text.toString().trim())) {

                error++

                passwordInput?.error = "Hasło musi składać się przynajmniej z 6 liter lub cyfr"
                passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(!isRePasswordValid(repasswordInput?.text.toString().trim(), passwordInput?.text.toString().trim())) {

                error++

                repasswordInput?.error = "Hasła się nie zgadzają"
                repasswordInput.setBackgroundResource(R.drawable.input_overlay_error)
                repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(error > 0) { return@setOnClickListener }

            alertDialog.show()

            auth.createUserWithEmailAndPassword(emailInput?.text.toString().trim(), passwordInput?.text.toString().trim()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid.toString()
                        val gender: String = if(genderSpinner.selectedItemPosition == 0) "male" else "famle"

                        var email = emailInput.text.trim()
                        val emailNum = email.indexOf("@")
                        email = email.substring(0, emailNum)

                        FirestoreUtil.registerCurrentUser(uid, email, uid, false, gender, "free") {

                            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)

                            val outputStream = ByteArrayOutputStream()

                            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            val selectedImageBytes = outputStream.toByteArray()

                            StorgeUtil.uploadProfilePhoto(selectedImageBytes) {

                                selectImageText.visibility = View.GONE
                                selectImageIcon.visibility = View.GONE
                                selectImage.setImageURI(fileUri)

                            }

                            alertDialog.dismiss()

                            val snackbar: Snackbar? = Snackbar.make(view, "Utworzono konto", 2500)
                            snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorAccent))
                            snackbar?.show()

                            startActivity(intentFor<MenuActivity>().newTask().clearTask())

                            val registrationToken = FirebaseInstanceId.getInstance().token
                            MyFirebaseMessagingService.addTokenToFirestore(registrationToken)

                        }

                    } else {

                        alertDialog.dismiss()

                        val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się utworzyć konta!", 2500)
                        snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                        snackbar?.show()

                    }
                }

        }

        btn_zaloguj.setOnClickListener {

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            fileUri = data.data
            selectImageText.visibility = View.GONE
            selectImageIcon.visibility = View.GONE
            selectImage.setImageURI(fileUri)

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

    fun isPasswordValid(password: String): Boolean {
        return Pattern.compile(
            "^[a-zA-Z0-9]{6,}$"
        ).matcher(password).matches()
    }

    fun isRePasswordValid(repassword: String, password: String): Boolean {
        return (repassword == password)
    }

    companion object {
        private const val RC_SELECT_IMAGE = 2
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

}
