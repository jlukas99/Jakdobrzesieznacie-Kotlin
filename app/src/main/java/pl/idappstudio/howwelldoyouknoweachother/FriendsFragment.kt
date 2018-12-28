package pl.idappstudio.howwelldoyouknoweachother


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_friends.view.*

class FriendsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        rootView.button2.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginMenuActivity::class.java)
            startActivity(intent)

        }

        return rootView
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }


}
