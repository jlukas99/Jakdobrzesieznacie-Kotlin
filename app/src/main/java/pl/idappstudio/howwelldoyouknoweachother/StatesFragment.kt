package pl.idappstudio.howwelldoyouknoweachother


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_states, container, false)
    }

    companion object {
        fun newInstance(): StatesFragment = StatesFragment()
    }


}
