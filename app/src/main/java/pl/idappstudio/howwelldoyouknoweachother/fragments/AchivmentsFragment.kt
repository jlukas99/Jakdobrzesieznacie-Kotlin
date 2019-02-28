package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class AchivmentsFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_achivments, container, false)
    }

    companion object {
        fun newInstance(): AchivmentsFragment = AchivmentsFragment()
    }

}
