package pl.idappstudio.howwelldoyouknoweachother.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object StorgeUtil {

    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference get() = storageInstance.reference.child("profile_image/")

    fun uploadProfilePhoto(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child("${FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("UID is null.")}-image")
        ref.putBytes(imageBytes).addOnSuccessListener { onSuccess(ref.path) }
    }

}