package pl.idappstudio.jakdobrzesieznacie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import com.google.android.material.snackbar.Snackbar
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.enums.StatusMessage
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.StorgeUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.io.ByteArrayOutputStream

class SettingsActivity : AppCompatActivity() {

    private val db: DocumentReference = FirebaseFirestore.getInstance().collection("users").document(UserUtil.user.uid)

    private var fileUri: Uri? = null
    private val items = arrayOf("Mężczyzna", "Kobieta")
    private var choose: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        exit_settings.setOnClickListener {

            if(save_button.visibility == View.VISIBLE || save_image.visibility == View.VISIBLE || save_gender.visibility == View.VISIBLE){

                SnackBarUtil.setActivitySnack("Posiadasz nie zapisane zmiany", ColorSnackBar.ERROR, R.drawable.ic_error_, it){ }

            } else {

                finish()

            }

        }

        version_text.text = getVersion()

        public_profile_switch.isChecked = UserUtil.user.public
        notification_switch.isChecked = UserUtil.user.notification

        public_profile_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->

            if(b){

                UserUtil.setPublic(true){

                    UserUtil.initializeUser {}

                }

            } else {

                UserUtil.setPublic(false){

                    UserUtil.initializeUser {}

                }

            }

        }

        notification_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->

            if(b){

                UserUtil.setNotification(true){

                    UserUtil.initializeUser {}

                }

            } else {

                UserUtil.setNotification(false){

                    UserUtil.initializeUser {}

                }

            }

        }

        cardView_change_nick.setOnClickListener {

            text_nick.visibility = View.GONE

            new_nick.setText(UserUtil.user.name)
            new_nick.isEnabled = true

            button_nick.visibility = View.GONE

            new_nick.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE

            cardView_change_nick.isEnabled = false

        }

        save_button.setOnClickListener {it2 ->

            save_button.visibility = View.GONE
            new_nick.isEnabled = false
            loading_nick.visibility = View.VISIBLE

            val nick = new_nick.text

            if(nick.length > 1) {

                new_nick.error = null

                nick.trim()

                db.update("name", nick.toString()).addOnCompleteListener {

                    new_nick.visibility = View.GONE
                    save_button.visibility = View.GONE

                    loading_nick.visibility = View.GONE

                    text_nick.visibility = View.VISIBLE
                    button_nick.visibility = View.VISIBLE

                    cardView_change_nick.isEnabled = true

                    if (it.isSuccessful) {

                        SnackBarUtil.setActivitySnack("Pomyślnie zmieniono nick", ColorSnackBar.SUCCES, R.drawable.ic_check_icon, it2){ }

                    } else {

                        SnackBarUtil.setActivitySnack("Nie udało się zmienić nicku", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                    }

                }.addOnFailureListener {

                    new_nick.visibility = View.GONE
                    save_button.visibility = View.GONE

                    loading_nick.visibility = View.GONE

                    text_nick.visibility = View.VISIBLE
                    button_nick.visibility = View.VISIBLE

                    cardView_change_nick.isEnabled = true
                    new_nick.isEnabled = true

                    new_nick.setText("")

                    SnackBarUtil.setActivitySnack("Nie udało się zmienić nicku", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                }

            } else {

                new_nick.error = "Nick musi składać sie przynajmniej z 2 znaków"

                loading_nick.visibility = View.GONE
                new_nick.isEnabled = true

                new_nick.visibility = View.VISIBLE
                save_button.visibility = View.VISIBLE

            }

        }

        cardView_change_avatar.setOnClickListener {

            if(!UserUtil.user.fb){

                val intent = Intent().apply {

                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

                }

                startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), 2)

            } else {

                SnackBarUtil.setActivitySnack("Nie możesz zmienić avataru, ponieważ jesteś zalogowany przez Facebook'a", ColorSnackBar.ERROR, R.drawable.ic_error_, it){ }

            }

        }

        save_image.setOnClickListener {it2 ->

            if (fileUri != null){

                cardView_change_avatar.isEnabled = false
                loading_image.visibility = View.VISIBLE

                button_image.visibility = View.GONE
                save_image.visibility = View.GONE

                val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)

                val outputStream = ByteArrayOutputStream()

                selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                val selectedImageBytes = outputStream.toByteArray()

                StorgeUtil.uploadProfilePhoto(selectedImageBytes) {

                    db.update("image", UserUtil.user.uid).addOnCompleteListener {

                        if (it.isSuccessful){

                            SnackBarUtil.setActivitySnack("Pomyślnie zmieniono zdjęcie", ColorSnackBar.SUCCES, R.drawable.ic_check_icon, it2){ }

                            text_image.text = "Zmień swój avatar"

                            button_image.visibility = View.VISIBLE
                            save_image.visibility = View.GONE

                            loading_image.visibility = View.GONE

                            cardView_change_avatar.isEnabled = true

                        } else {

                            SnackBarUtil.setActivitySnack("Nie udało się zmienić zdjęcia", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                            text_image.text = "Zmień swój avatar"

                            button_image.visibility = View.VISIBLE
                            save_image.visibility = View.GONE

                            loading_image.visibility = View.GONE

                            cardView_change_avatar.isEnabled = true

                        }

                    }.addOnFailureListener {

                        SnackBarUtil.setActivitySnack("Nie udało się zmienić zdjęcia", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                        text_image.text = "Zmień swój avatar"

                        button_image.visibility = View.VISIBLE
                        save_image.visibility = View.GONE

                        loading_image.visibility = View.GONE

                        cardView_change_avatar.isEnabled = true

                    }

                }

            } else {

                text_image.text = "Zmień swój avatar"

                button_image.visibility = View.VISIBLE
                save_image.visibility = View.GONE

                loading_image.visibility = View.GONE

                cardView_change_avatar.isEnabled = true

            }

        }

        cardView_change_gender.setOnClickListener {

            if(!UserUtil.user.fb) {

                val builder = AlertDialog.Builder(this)
                with(builder) {
                    setTitle("Wybierz swoją płeć")
                    setItems(items) { dialog, which ->

                        choose = which

                        text_gender.text = items[which]

                        gender_button.visibility = View.GONE
                        save_gender.visibility = View.VISIBLE

                    }

                    show()
                }

            } else {

                SnackBarUtil.setActivitySnack("Nie możesz zmienić płci, ponieważ jesteś zalogowany przez Facebook'a", ColorSnackBar.ERROR, R.drawable.ic_error_, it){ }

            }

        }

        save_gender.setOnClickListener {it2 ->

            cardView_change_gender.isEnabled = false
            loading_gender.visibility = View.VISIBLE

            gender_button.visibility = View.GONE
            save_gender.visibility = View.GONE

            val gender: String = if(choose == 0) { "male" } else { "female" }

            db.update("gender", gender).addOnCompleteListener {

                if(it.isSuccessful){

                    SnackBarUtil.setActivitySnack("Pomyślnie zmieniono płeć", ColorSnackBar.SUCCES, R.drawable.ic_check_icon, it2){ }

                    text_gender.text = "Zmień swoją płeć"

                    cardView_change_gender.isEnabled = true
                    loading_gender.visibility = View.GONE

                    gender_button.visibility = View.VISIBLE
                    save_gender.visibility = View.GONE

                } else {

                    SnackBarUtil.setActivitySnack("Nie udało się zmienić płci", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                    text_gender.text = "Zmień swoją płeć"

                    cardView_change_gender.isEnabled = true
                    loading_gender.visibility = View.GONE

                    gender_button.visibility = View.VISIBLE
                    save_gender.visibility = View.GONE

                }
            }.addOnFailureListener {

                SnackBarUtil.setActivitySnack("Nie udało się zmienić płci", ColorSnackBar.ERROR, R.drawable.ic_error_, it2){ }

                text_gender.text = "Zmień swoją płeć"

                cardView_change_gender.isEnabled = true
                loading_gender.visibility = View.GONE

                gender_button.visibility = View.VISIBLE
                save_gender.visibility = View.GONE

            }

        }

        if(UserUtil.user.type == "free"){

            premium_color_switch.isChecked = false
            premium_color_switch.setOnClickListener {

                SnackBarUtil.setActivitySnack("Aby użyć tej opcji, musisz posiadać konto Premium", ColorSnackBar.WARING, R.drawable.ic_corn, it){ }

                premium_color_switch.isChecked = false
            }

        } else {

        }

    }

    private fun getVersion() : String{
        return try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "error"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            fileUri = data.data

            button_image.visibility = View.GONE
            loading_image.visibility = View.GONE
            save_image.visibility = View.VISIBLE

            cardView_change_avatar.isEnabled = false

            text_image.text = fileUri?.lastPathSegment.toString()

        }

    }

    override fun onResume() {
        super.onResume()
        UserUtil.updateStatus(StatusMessage.insettings)
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
        exit_settings.callOnClick()
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
