package com.example.myapplication.ui.navigation2

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ContactAdapter
import com.example.myapplication.datasource.models.ContactModel
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.ContactViewModel
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

const val CONTACT_READ_REQUEST = 1

class ContactFragment : Fragment() {
    private val contactList = arrayListOf<ContactModel>()
    private val numberSet = HashSet<String>()
    private lateinit var viewModel: ContactViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("rafeek  e7na hena", "onViewCreated: ")
        ContactFragmentProgressBar.visibility = View.VISIBLE
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[ContactViewModel::class.java]
        checkPermissionFun()
    }

    @DelicateCoroutinesApi
    private fun checkPermissionFun() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_CONTACTS
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("rafeek if", "checkPermissionFun: ")
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    CONTACT_READ_REQUEST
                )
            }
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                getContactList()
            }
        }
    }

    @DelicateCoroutinesApi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == CONTACT_READ_REQUEST) {
            GlobalScope.launch(Dispatchers.IO) {
                getContactList()
            }
        } else {
            Toast.makeText(context, "Permission Denied .", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Recycle")
    private suspend fun getContactList() {
        val uri = ContactsContract.Contacts.CONTENT_URI
        val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        val cursor = context?.contentResolver?.query(uri, null, null, null, sort)
        Log.d("rafeek", "getContactList: $cursor $uri")
        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(_ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?"
                val phoneCursor =
                    context?.contentResolver?.query(uriPhone, null, selection, arrayOf(id), null)
                if (phoneCursor?.moveToNext()!!) {
                    var phone =
                        phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    phone = (phone.replace("\\s".toRegex(), ""))
                    if (phone.contains("+2")) {
                        phone = phone.drop(2)
                    }
                    phone = phone.trim()
                    if (!numberSet.contains(phone)) {
                        numberSet.add(phone)
                        val contact = ContactModel(name, phone, null)
                        contactList.add(contact)
                    }
                    phoneCursor.close()
                }
            }
        }
        withContext(Dispatchers.Main) {
            initRecyclerViewFun()
        }
        Log.d("rafeek", "getContactList: ${contactList.size}")
    }

    private fun initRecyclerViewFun() {
        ContactFragmentProgressBar.visibility = View.GONE
        val adapter = ContactAdapter(requireContext(), contactList)
        ContactFragmentRecyclerView.adapter = adapter
        ContactFragmentRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                val contactDetail = contactList[position]
                checkContactDefinedFun(contactDetail)
            }
        })
    }

    private fun checkContactDefinedFun(contactDetail: ContactModel) {
        viewModel.getContactInfo(contactDetail.phoneNumber)
        lifecycleScope.launchWhenCreated {
            viewModel.contactDetail.collect {
                when (it) {
                    is Result.Success -> {
                        if (it.data?.number == contactDetail.phoneNumber) {
                            withContext(Dispatchers.Main) {
                                val navController = findNavController()
                                val action =
                                    ContactFragmentDirections.actionContactFragmentToMessagesBodyFragment(
                                        contactDetail.name,
                                        contactDetail,
                                        null,
                                        null
                                    )
                                Log.d(
                                    "rafeek",
                                    "checkContactDefinedFun: ${navController.currentDestination}"
                                )
                                navController.navigate(action)
                            }
                        }
                        Log.d("rafeek", "checkContactDefinedFun: e7na hena")
                    }
                    is Result.Error -> {
                        if (contactDetail.phoneNumber == it.message) {
                            Log.d("rafeek", "checkContactDefinedFun: error")
                            Toast.makeText(
                                context,
                                "${contactDetail.name} not have an account",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    else -> Unit
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        activity?.mainActivityBtn?.visibility = View.GONE
    }
}