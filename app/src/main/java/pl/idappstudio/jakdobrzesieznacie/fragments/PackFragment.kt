package pl.idappstudio.jakdobrzesieznacie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickSetListener
import pl.idappstudio.jakdobrzesieznacie.model.SetItem

class PackFragment : androidx.fragment.app.Fragment(), ClickSetListener {

    override fun click(setItem: SetItem) {

    }

//    private lateinit var setListener: ListenerRegistration
//
private lateinit var packIcon: ImageView
    private lateinit var packText: TextView
//
//    private lateinit var pack_btn_add: MaterialButton
//
//    private lateinit var packHeader: ConstraintLayout
//    private lateinit var buttonHeader: ConstraintLayout
//
//    private lateinit var rvPack: RecyclerView
//
//    private val packSection = Section()
//
//    private val dbSet = FirebaseFirestore.getInstance().collection("set")
//
//    private val packAdapter = GroupAdapter<ViewHolder>()
//
//    private val packItems = HashMap<String, PackAdapater>()
//
//    private fun addPackItem(uid: String) : HashMap<String, PackAdapater> {
//        packItems[uid] = PackAdapater(uid, this.context!!, this)
//        return packItems
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        packSection.setHideWhenEmpty(true)
//        packAdapter.add(0, packSection)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_pack, container, false)

        packIcon = rootView.findViewById(R.id.image_none_pack)
        packText = rootView.findViewById(R.id.text_none_pack)

//        pack_btn_add = rootView.findViewById(R.id.pack_btn_add)
//
//        packHeader = rootView.findViewById(R.id.include3)
//        buttonHeader = rootView.findViewById(R.id.include)

        packIcon.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

//        rvPack = rootView.findViewById(R.id.rv_pack)
//
//        val rvPackLLM = LinearLayoutManager(this.context)
//
//        rvPackLLM.orientation = RecyclerView.VERTICAL
//
//        rvPack.layoutManager = rvPackLLM
//
//        rvPack.itemAnimator = SlideInDownAnimator()
//
//        rvPack.adapter = packAdapter
//        packHeader.head_title_text.text = "STWORZONE ZESTAWY"
//        buttonHeader.head_title_text.text = "OPCJE"
//
//        checkPackList {
//            getPacks()
//        }

        return rootView
    }

//    private fun getPacks() {
//
//        setListener = dbSet.whereEqualTo("category", UserUtil.user.uid).addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->
//
//            if(e != null){
//                return@EventListener
//            }
//
//            if(doc != null) {
//
//                for (it in doc.documentChanges){
//
//                    if(it.type == DocumentChange.Type.REMOVED){
//
//                        rvPack.itemAnimator = LandingAnimator()
//
//                        packItems.remove(it.document.id)
//                        checkPackList {
//                            packSection.update(packItems.values)
//                        }
//
//
//                    }
//
//                    if(it.type == DocumentChange.Type.ADDED){
//
//                        addPackItem(it.document.id)
//                        checkPackList {
//                            packSection.update(packItems.values)
//                        }
//
//                    }
//
//                }
//
//
//            }
//
//        })
//    }
//
//    fun checkPackList(onComplete: () -> Unit){
//
//        if(!packItems.isEmpty()){
//
//            if(UserUtil.user.type == "free"){
//
//                pack_btn_add.isEnabled = false
//
//                pack_btn_add.text = "KUP SLOT NA ZESTAW"
//                pack_btn_add.background.setColorFilter(
//                        ContextCompat.getColor(this.context!!, R.color.colorButtonSecondary
//                        ), android.graphics.PorterDuff.Mode.SRC_IN
//                )
//
//            } else {
//
//                pack_btn_add.isEnabled = true
//
//                pack_btn_add.text = "STWORZ ZESTAW"
//
//                if(context != null) {
//
//                    pack_btn_add.background.setColorFilter(
//                        ContextCompat.getColor(
//                            context!!, R.color.colorPrimary
//                        ), android.graphics.PorterDuff.Mode.SRC_IN
//                    )
//
//                }
//
//                pack_btn_add.setOnClickListener {
//
//                    val pack = SetItem("zmień nazwę swojej paczki", 0, false, UserUtil.user.uid, "all", "")
//
//                    dbSet.add(pack).addOnSuccessListener {
//
//                        dbSet.document(it.id).update("id", it.id)
//
//                        SnackBarUtil.setActivitySnack("Stworzono paczkę", ColorSnackBar.SUCCES, R.drawable.ic_pack_icon, pack_btn_add){
//
//                        }
//
//                    }
//
//                }
//
//            }
//
//            pack_icon.visibility = View.GONE
//            pack_text.visibility = View.GONE
//
//            onComplete()
//
//        } else {
//
//            pack_btn_add.isEnabled = true
//
//            pack_btn_add.text = "STWORZ ZESTAW"
//
//            if(context != null) {
//
//                pack_btn_add.background.setColorFilter(
//                    ContextCompat.getColor(
//                        context!!, R.color.colorPrimary
//                    ), android.graphics.PorterDuff.Mode.SRC_IN
//                )
//
//            }
//
//            pack_btn_add.setOnClickListener {
//
//                val pack = SetItem("zmień nazwę swojej paczki", 0, false, "", "all", "")
//
//                dbSet.add(pack).addOnSuccessListener {
//
//                    dbSet.document(it.id).update("id", it.id)
//                    dbSet.document(it.id).update("category",UserUtil.user.uid)
//
//                        SnackBarUtil.setActivitySnack("Stworzono paczkę", ColorSnackBar.SUCCES, R.drawable.ic_pack_icon, pack_btn_add){
//
//                    }
//
//                }
//
//            }
//
//            pack_icon.visibility = View.VISIBLE
//            pack_text.visibility = View.VISIBLE
//
//            onComplete()
//
//        }
//
//    }

//    override fun onPause() {
//        super.onPause()
//        setListener.remove()
//    }

}
