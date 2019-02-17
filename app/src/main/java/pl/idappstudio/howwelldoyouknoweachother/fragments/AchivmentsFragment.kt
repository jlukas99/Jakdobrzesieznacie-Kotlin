package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class AchivmentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_achivments, container, false)
    }

    companion object {
        fun newInstance(): AchivmentsFragment = AchivmentsFragment()
    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
    }
}
