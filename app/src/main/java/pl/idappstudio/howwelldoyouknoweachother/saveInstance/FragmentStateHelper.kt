package pl.idappstudio.howwelldoyouknoweachother.saveInstance

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

class FragmentStateHelper(val fragmentManager: FragmentManager) {

    private val fragmentSavedStates = mutableMapOf<String, Fragment.SavedState?>()

    fun restoreState(fragment: Fragment, key: String) {
        fragmentSavedStates[key]?.let { savedState ->
            if (!fragment.isAdded) {
                fragment.setInitialSavedState(savedState)
            }
        }
    }

    fun saveState(fragment: Fragment, key: String) {
        if (fragment.isAdded) {
            fragmentSavedStates[key] = fragmentManager.saveFragmentInstanceState(fragment)
        }
    }

}