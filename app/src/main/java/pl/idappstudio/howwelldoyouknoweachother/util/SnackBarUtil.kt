package pl.idappstudio.howwelldoyouknoweachother.util

import android.graphics.Typeface
import android.view.View
import pl.idappstudio.howwelldoyouknoweachother.R
import android.widget.TextView
import com.androidadvance.topsnackbar.TSnackbar


object SnackBarUtil {

    fun setActivitySnack(message: String, color: Int, image: Int,view: View, onComplete: () -> Unit) {

            val snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG)
            snackbar.setIconLeft(image, 24F)
            snackbar.setIconPadding(16)

            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(view.resources.getColor(color))

            val textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
            textView.setTextColor(view.resources.getColor(R.color.colorWhite))
            textView.setTypeface(textView.typeface, Typeface.BOLD)

            snackbar.show()

            onComplete()

    }

}