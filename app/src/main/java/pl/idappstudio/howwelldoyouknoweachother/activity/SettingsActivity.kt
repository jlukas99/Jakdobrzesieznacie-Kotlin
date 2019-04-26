package pl.idappstudio.howwelldoyouknoweachother.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import pl.idappstudio.howwelldoyouknoweachother.enums.StatusMessage
import pl.idappstudio.howwelldoyouknoweachother.util.StorgeUtil
import pl.idappstudio.howwelldoyouknoweachother.util.UserUtil
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

                val snackbar = Snackbar.make(view, "Posiadasz nie zapisane zmiany", 2500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                snackbar.show()

            } else {

                finish()

            }

        }

        version_text.text = getVersion()

        public_profile_switch.isChecked = UserUtil.user.public
        notification_switch.isChecked = UserUtil.user.notification

        public_profile_switch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

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

        notification_switch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

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
            button_nick.visibility = View.GONE

            new_nick.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE

            cardView_change_nick.isEnabled = false

        }

        save_button.setOnClickListener {

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

                    val snackbar: Snackbar

                    if (it.isSuccessful) {

                        snackbar = Snackbar.make(view, "Pomyślnie zmieniono nick", 2500)
                        snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorAccent))
                        snackbar.show()

                    } else {

                        snackbar = Snackbar.make(view, "Nie udało się zmienić nicku", 2500)
                        snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                        snackbar.show()

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

                    val snackbar: Snackbar = Snackbar.make(view, "Nie udało się zmienić nicku", 2500)

                    snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                    snackbar.show()

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

                val snackbar = Snackbar.make(view, "Nie możesz zmienić avataru, ponieważ jesteś zalogowany przez Facebook'a", 3500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                snackbar.show()

            }

        }

        save_image.setOnClickListener {

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

                            val snackbar = Snackbar.make(view, "Pomyślnie zmieniono zdjęcie", 2500)
                            snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorAccent))
                            snackbar.show()

                            text_image.text = "Zmień swój avatar"

                            button_image.visibility = View.VISIBLE
                            save_image.visibility = View.GONE

                            loading_image.visibility = View.GONE

                            cardView_change_avatar.isEnabled = true

                        } else {

                            val snackbar = Snackbar.make(view, "Nie udało się zmienić zdjęcia", 2500)
                            snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                            snackbar.show()

                            text_image.text = "Zmień swój avatar"

                            button_image.visibility = View.VISIBLE
                            save_image.visibility = View.GONE

                            loading_image.visibility = View.GONE

                            cardView_change_avatar.isEnabled = true

                        }

                    }.addOnFailureListener {

                        val snackbar = Snackbar.make(view, "Nie udało się zmienić zdjęcia", 2500)
                        snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                        snackbar.show()

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

                val snackbar = Snackbar.make(view, "Nie możesz zmienić płci, ponieważ jesteś zalogowany przez Facebook'a", 3500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                snackbar.show()

            }

        }

        save_gender.setOnClickListener {

            cardView_change_gender.isEnabled = false
            loading_gender.visibility = View.VISIBLE

            gender_button.visibility = View.GONE
            save_gender.visibility = View.GONE

            val gender: String = if(choose == 0) { "male" } else { "female" }

            db.update("gender", gender).addOnCompleteListener {

                if(it.isSuccessful){

                    val snackbar = Snackbar.make(view, "Pomyślnie zmieniono płeć", 2500)
                    snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorAccent))
                    snackbar.show()

                    text_gender.text = "Zmień swoją płeć"

                    cardView_change_gender.isEnabled = true
                    loading_gender.visibility = View.GONE

                    gender_button.visibility = View.VISIBLE
                    save_gender.visibility = View.GONE

                } else {

                    val snackbar = Snackbar.make(view, "Nie udało się zmienić płci", 2500)
                    snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                    snackbar.show()

                    text_gender.text = "Zmień swoją płeć"

                    cardView_change_gender.isEnabled = true
                    loading_gender.visibility = View.GONE

                    gender_button.visibility = View.VISIBLE
                    save_gender.visibility = View.GONE

                }
            }.addOnFailureListener {

                val snackbar = Snackbar.make(view, "Nie udało się zmienić płci", 2500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorRed))
                snackbar.show()

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

                val snackbar = Snackbar.make(view, "Aby włączyć tą opcje, musisz posiadać konto Premium", 2500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorYellow))
                snackbar.show()

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
        hideNavigationBar()
        UserUtil.updateStatus(StatusMessage.insettings)
    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.systemUiVisibility = flags

    }
}
