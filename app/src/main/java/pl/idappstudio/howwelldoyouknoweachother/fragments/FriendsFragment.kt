package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_friends.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.LoginMenuActivity

class FriendsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        rootView.button2.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        return rootView
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }


}
