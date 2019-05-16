package pl.idappstudio.jakdobrzesieznacie.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.androidadvance.topsnackbar.TSnackbar
import pl.idappstudio.jakdobrzesieznacie.R

object SnackBarUtil {

    fun setActivitySnack(message: String, color: Int, image: Int, view: View, onComplete: () -> Unit) {

            val snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG)
            snackbar.setIconLeft(image, 24F)
            snackbar.setIconPadding(16)

            val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.context, color))

            val textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(view.context, R.color.colorWhite))

            snackbar.show()

            onComplete()

    }

}