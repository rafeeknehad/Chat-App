package com.example.myapplication.ui.navigation2

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.extensions.setOnClickListener
import com.example.myapplication.extensions.setOnEditorAction
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream


const val CAMERA_PERMISSION = 1

class ProfileFragment : Fragment() {

    private lateinit var picker: String
    private val mStorageRef = FirebaseStorage.getInstance().getReference("ProfileImages")
    private lateinit var currentUserDetail: UserModel
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        getProfileInfo()
        initFabListenerFun()
        initEditorActionFun()
        profileFragmentTakePhoto.setOnClickListener {
            showDialogFun()
        }
        profileFragmentSubmitBtn.setOnClickListener {
            submitUpdateUserDetail()
        }
    }

    private fun submitUpdateUserDetail() {
        val updateUser = UserModel(
            profileFragmentTextDisplayName.text.toString().trim(),
            profileFragmentTextEmail.text.toString().trim(),
            profileFragmentTextPassword.text.toString().trim(),
            profileFragmentTextNumber.text.toString().trim(),
            currentUserDetail.profileImage,
            id = currentUserDetail.id
        )
        viewModel.updateUserDetail(updateUser)
        lifecycleScope.launchWhenCreated {
            viewModel.updateUser.collect {
                when (it) {
                    is Result.Loading -> {
                        profileFragmentProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        profileFragmentProgressBar.visibility = View.GONE
                        Log.d("rafeek", "submitUpdateUserDetail: ${it.data}")
                        updateShardReferenceFun(it.data)
                    }
                    is Result.Error -> {
                        Log.d("rafeek", "submitUpdateUserDetail: ${it.message}")
                    }
                    is Result.Empty -> {

                    }

                }
            }
        }
    }

    private fun updateShardReferenceFun(data: UserModel?) {
        val sp = context?.getSharedPreferences(MainActivity.SHARD_REFERENCE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sp?.edit()
        editor?.putString(MainActivity.DISPLAY_NAME, data?.displayName)
        editor?.putString(MainActivity.EMAIL, data?.email)
        editor?.putString(MainActivity.PASSWORD, data?.password)
        editor?.putString(MainActivity.PROFILE_IMAGE, data?.profileImage)
        editor?.putString(MainActivity.ID, data?.id)
        editor?.apply()
    }

    private fun showDialogFun() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_dialog)

        val cameraImageButton = dialog.findViewById<ImageButton>(R.id.bottomSheetDialogCamera)
        val galleryImageButton = dialog.findViewById<ImageButton>(R.id.bottomSheetDialogGallery)
//        val alwaysUseBtn = dialog.findViewById<Button>(R.id.bottomSheetDialogAlwaysUse)
//        val justOneBtn = dialog.findViewById<Button>(R.id.bottomSheetDialogJustOne)

        cameraImageButton.setOnClickListener {
            picker = "Camera"
            openCameraFun()
            dialog.dismiss()
        }

        galleryImageButton.setOnClickListener {
            picker = "Gallery"
            openGalleryFun()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun openGalleryFun() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it != null && it.data != null && picker == "Gallery") {
                val bitMap = MediaStore.Images.Media.getBitmap(
                    context?.contentResolver,
                    it.data?.data
                )
                profileFragmentUserPhoto.setImageBitmap(bitMap)
                uploadImageFun(it.data?.data, null)
            }
            if (it.resultCode == Activity.RESULT_OK && it != null && it.data != null && picker == "Camera") {
                val bitMap = it.data?.extras?.get("data") as Bitmap
                profileFragmentUserPhoto.setImageBitmap(bitMap)
                val stream = ByteArrayOutputStream()
                bitMap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val byteOfStream = stream.toByteArray()
                uploadImageFun(null, byteOfStream)
            }
        }

    private fun uploadImageFun(uri: Uri?, byteOfStream: ByteArray?) {
        val fileReference = mStorageRef.child(currentUserDetail.number)
        if (uri != null) {
            fileReference.putFile(uri)
                .addOnSuccessListener { it ->
                    Log.d("rafeek ss", "uploadImageFun: ${it.metadata?.reference?.downloadUrl}")
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        Log.d("rafeek ss", "uploadImageFun: $it")
                        currentUserDetail.profileImage = uri.toString()
                        val sp =
                            context?.getSharedPreferences(
                                MainActivity.SHARD_REFERENCE,
                                Context.MODE_PRIVATE
                            )
                        val editor = sp?.edit()
                        editor?.putString(MainActivity.PROFILE_IMAGE, uri.toString())
                        editor?.apply()
                    }
                }
                .addOnFailureListener {
                    Log.d("rafeek", "uploadImageFun: ${it.message}")
                }
        }
        if (byteOfStream != null) {
            fileReference.putBytes(byteOfStream)
                .addOnSuccessListener {
                    Log.d("rafeek ss", "uploadImageFun: ${it.metadata?.reference?.downloadUrl}")
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        Log.d("rafeek ss", "uploadImageFun: $uri")
                        currentUserDetail.profileImage = uri.toString()
                        val sp =
                            context?.getSharedPreferences(
                                MainActivity.SHARD_REFERENCE,
                                Context.MODE_PRIVATE
                            )
                        val editor = sp?.edit()
                        editor?.putString(MainActivity.PROFILE_IMAGE, uri.toString())
                        editor?.apply()
                    }
                }
                .addOnFailureListener {
                    Log.d("rafeek", "uploadImageFun: ${it.message}")
                }
        }
    }

    private fun openCameraFun() {
        checkCameraPermissionFun()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getResult.launch(intent)
    }

    private fun checkCameraPermissionFun() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION
            )
        }
    }

    private fun initEditorActionFun() {
        profileFragmentEditEmail.setOnEditorAction(
            profileFragmentTextEmail
        )
        profileFragmentEditTextDisplayName.setOnEditorAction(
            profileFragmentTextDisplayName
        )
        profileFragmentEditTextNumber.setOnEditorAction(
            profileFragmentTextNumber
        )
        profileFragmentEditTextPassword.setOnEditorAction(
            profileFragmentTextPassword
        )
    }

    private fun initFabListenerFun() {
        profileFragmentFab1.setOnClickListener(
            profileFragmentTextDisplayName,
            profileFragmentEditTextDisplayName
        )
        profileFragmentFab2.setOnClickListener(
            profileFragmentTextEmail,
            profileFragmentEditEmail
        )
        profileFragmentFab3.setOnClickListener(
            profileFragmentTextNumber,
            profileFragmentEditTextNumber
        )
        profileFragmentFab4.setOnClickListener(
            profileFragmentTextPassword,
            profileFragmentTextLayoutPassword
        )
    }

    private fun getProfileInfo() {
        val sp =
            context?.getSharedPreferences(MainActivity.SHARD_REFERENCE, Context.MODE_PRIVATE)
        currentUserDetail = UserModel(
            sp?.getString(MainActivity.DISPLAY_NAME, "")!!,
            sp.getString(MainActivity.EMAIL, "")!!,
            sp.getString(MainActivity.PASSWORD, "")!!,
            sp.getString(MainActivity.NUMBER, "")!!,
            sp.getString(MainActivity.PROFILE_IMAGE, "")!!,
            id = sp.getString(MainActivity.ID, "")
        )
        initUserDetailFun()
        //getProfileImage()
    }

    private fun initUserDetailFun() {
        profileFragmentTextDisplayName.text = currentUserDetail.displayName
        profileFragmentEditTextDisplayName.setText(currentUserDetail.displayName)

        profileFragmentTextEmail.text = currentUserDetail.email
        profileFragmentEditEmail.setText((currentUserDetail.email))

        profileFragmentTextNumber.text = currentUserDetail.number
        profileFragmentEditTextNumber.setText(currentUserDetail.number)

        profileFragmentTextPassword.text = currentUserDetail.password
        profileFragmentEditTextPassword.setText(currentUserDetail.password)

        if (currentUserDetail.profileImage != "") {
            Picasso.with(context)
                .load(currentUserDetail.profileImage)
                .into(profileFragmentUserPhoto)
        }
    }

    private fun getProfileImage() {
        viewModel.getProfileImage(currentUserDetail.number)
        lifecycleScope.launchWhenCreated {
            viewModel.profileImage.collect {
                when (it) {
                    Result.Loading -> {
                        profileFragmentUserImageProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        profileFragmentUserImageProgressBar.visibility = View.GONE
                        Log.d("rafeek", "getProfileImage: ${it.data}")
                    }
                    is Result.Error -> {
                        profileFragmentUserImageProgressBar.visibility = View.GONE
                        Log.d("rafeek", "getProfileImage: ${it.message}")
                    }
                    is Result.Empty -> {

                    }
                }
            }
        }
    }
}