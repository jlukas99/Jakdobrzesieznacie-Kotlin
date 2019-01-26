package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class FriendsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }


}
