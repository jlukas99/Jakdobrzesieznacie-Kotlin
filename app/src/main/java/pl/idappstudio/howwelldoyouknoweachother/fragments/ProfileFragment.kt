package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.LoginMenuActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.SettingsActivity
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class ProfileFragment : Fragment() {

    private lateinit var name: TextView
    private lateinit var image: CircleImageView

    private lateinit var loading: SpinKitView

    private lateinit var logout: Button
    private lateinit var settings: Button
    private lateinit var share: Button

    private val currentUser = FirestoreUtil.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_profile, container, false)

        name = rootView.findViewById(R.id.profile_name)
        image = rootView.findViewById(R.id.profile_image)

        loading = rootView.findViewById(R.id.profile_loading_image)

        logout = rootView.findViewById(R.id.profile_logout_btn)
        settings = rootView.findViewById(R.id.profile_settings_button)
        share = rootView.findViewById(R.id.profile_share_button)

        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        settings.setOnClickListener {

            startActivity<SettingsActivity>()
            onStop()

        }

        share.setOnClickListener {

//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "Cześć, pobierz aplikacje i zagraj ze mną\n\nhttps://howwelldoyouknoweachother.page.link/download_game")
//            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Jak Dobrze Się Znacie?")
//            sendIntent.type = "text/plain"
//            startActivity(Intent.createChooser(sendIntent, FirestoreUtil.currentUser.name)
//
//            )

        }

        return rootView
    }

    fun setInformation(){

        loading.visibility = View.VISIBLE

        name.text = currentUser.name
        if(currentUser.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    loading.visibility = View.GONE
                    return false
                }

            }).into(image)

        } else {

            if (currentUser.fb) {

                GlideApp.with(this).load("http://graph.facebook.com/${currentUser.image}/picture?type=large").diskCacheStrategy(
                    DiskCacheStrategy.AUTOMATIC)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.visibility = View.GONE
                            return false
                        }

                    }).into(image)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(currentUser.image + "-image")
                        .downloadUrl
                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(this).load(Uri.toString()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.visibility = View.GONE
                            return false
                        }

                    }).into(image)

                }.addOnFailureListener {

                    GlideApp.with(this).load(R.mipmap.logo).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.visibility = View.GONE
                            return false
                        }

                    }).into(image)

                }
            }
        }

    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onResume() {
        super.onResume()

        FirestoreUtil.initialize()
        setInformation()

    }


}
