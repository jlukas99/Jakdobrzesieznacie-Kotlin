package pl.idappstudio.jakdobrzesieznacie.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import pl.idappstudio.jakdobrzesieznacie.R

class AchivmentsFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_achivments, container, false)

        val image: ImageView = rootView.findViewById(R.id.image_achivment)
        image.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        return rootView
    }

}
