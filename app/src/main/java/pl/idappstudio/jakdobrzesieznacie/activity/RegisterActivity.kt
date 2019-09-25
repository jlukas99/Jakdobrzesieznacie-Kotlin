package pl.idappstudio.jakdobrzesieznacie.activity

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
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.StorgeUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

@Suppress("DEPRECATION")
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
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        selectImage.setOnClickListener {

            val intent = Intent().apply {

                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

            }

            startActivityForResult(
                    Intent.createChooser(intent, resources.getString(R.string.select_picture)),
                    RC_SELECT_IMAGE
            )
        }

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

        passwordInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = passwordInput?.text.toString().trim()

                if (isPasswordValid(content)) {

                    passwordInput.setBackgroundResource(R.drawable.input_overlay)
                    passwordImage.setBackgroundResource(R.drawable.input_overlay_icon)

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

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        btn_send.setOnClickListener { it2 ->

            var error = 0

            if(!isRePasswordValid(repasswordInput?.text.toString().trim(), passwordInput?.text.toString().trim())) {

                error++

                repasswordInput.setBackgroundResource(R.drawable.input_overlay_error)
                repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.password_not_same),
                        ColorSnackBar.ERROR,
                        R.mipmap.password_icon,
                        it2
                ) { }

            }

            if(!isPasswordValid(passwordInput?.text.toString().trim())) {

                error++

                passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.password_validation),
                        ColorSnackBar.ERROR,
                        R.mipmap.password_icon,
                        it2
                ) { }

            }

            if(!isEmailValid(emailInput?.text.toString().trim())) {

                error++

                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.incoreect_mail),
                        ColorSnackBar.ERROR,
                        R.mipmap.email_icon,
                        it2
                ) { }

            }

            if(error > 0) { return@setOnClickListener }

            alertDialog.show()

            auth.createUserWithEmailAndPassword(emailInput?.text.toString().trim(), passwordInput?.text.toString().trim()).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid.toString()
                    val gender: String = if (genderSpinner.selectedItemPosition == 0) "male" else "female"

                    var email = emailInput.text.trim()
                    val emailNum = email.indexOf("@")
                    email = email.substring(0, emailNum)

                    val image: String

                    image = if (fileUri != null) {

                        uid

                    } else {

                        if (genderSpinner.selectedItemPosition == 0) "male" else "female"

                    }

                    FirestoreUtil.registerCurrentUser(uid, email, image, false, gender, "free", "") {

                        if (fileUri != null) {

                            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)

                            val outputStream = ByteArrayOutputStream()

                            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            val selectedImageBytes = outputStream.toByteArray()

                            StorgeUtil.uploadProfilePhoto(selectedImageBytes) {

                                selectImageText.visibility = View.GONE
                                selectImageIcon.visibility = View.GONE
                                selectImage.setImageURI(fileUri)

                            }

                        }

                        UserUtil.initializeUser {

                            alertDialog.dismiss()

                            SnackBarUtil.setActivitySnack(
                                    resources.getString(R.string.create_account_successful),
                                    ColorSnackBar.SUCCES,
                                    R.drawable.ic_check_icon,
                                    it2
                            ) {

                                startActivity(intentFor<MenuActivity>().newTask().clearTask())

                            }

                        }

                    }

                } else {

                    alertDialog.dismiss()

                    SnackBarUtil.setActivitySnack(
                            resources.getString(R.string.create_account_error),
                            ColorSnackBar.ERROR,
                            R.drawable.ic_error_,
                            it2
                    ) { }

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
        if (requestCode == RC_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {

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
