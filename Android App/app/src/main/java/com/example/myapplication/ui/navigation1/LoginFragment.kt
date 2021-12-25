package com.example.myapplication.ui.navigation1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.MainActivity2
import com.example.myapplication.R
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.extensions.onDataChange
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.collect

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        sharedPreferences =
            context?.getSharedPreferences(MainActivity.SHARD_REFERENCE, Context.MODE_PRIVATE)!!
        loginBtn.setOnClickListener {
            validationInputFun()
        }
        loginEmailLayout.onDataChange()
        loginPassLayout.onDataChange()
        loginNumberLayout.onDataChange()
    }

    private fun validationInputFun() {
        if (loginEmail.text.toString().trim().isEmpty()) {
            loginEmailLayout.isErrorEnabled = true
            loginEmailLayout.error = "Email can`t be empty"
        }
        if (loginPassword.text.toString().trim().isEmpty()) {
            loginPassLayout.isErrorEnabled = true
            loginPassLayout.error = "Password can`t be empty"
        }
        if (loginNumber.text.toString().trim().isEmpty()) {
            loginNumberLayout.isErrorEnabled = true
            loginNumberLayout.error = "Number can`t be empty"
        }
        if (loginEmail.text.toString().trim().isNotEmpty() && loginPassword.text.toString()
                .isNotEmpty() && loginNumber.text.toString().isNotEmpty()
        ) {
            loginFun()
        }
    }

    private fun loginFun() {
        viewModel.findUserDetail(
            loginEmail.text.toString().trim(),
            loginPassword.text.toString().trim()
        )
        lifecycleScope.launchWhenCreated {
            viewModel.userDetail.collect {
                when (it) {
                    is Result.Success -> {
                        loginProgressBar.visibility = View.GONE
                        val userModel = it.data
                        if (loginPassword.text.toString()
                                .trim() == userModel?.password && loginNumber.text.toString()
                                .trim() == userModel.number
                        ) {
                            Toast.makeText(
                                context,
                                "welcome ${userModel.displayName}",
                                Toast.LENGTH_SHORT
                            ).show()
                            saveInSharedReferenceFun(userModel)
                            navigationFun()
                        }
                        if (loginPassword.text.toString().trim() != userModel?.password) {
                            loginPassLayout.isErrorEnabled = true
                            loginPassLayout.error = "Please check the password"
                        }
                        if (loginNumber.text.toString() != userModel?.number) {
                            loginNumberLayout.isErrorEnabled = true
                            loginNumberLayout.error = "Number is not correct"
                        }
                    }
                    is Result.Loading -> {
                        Log.d("rafeek", "onViewCreated: loading")
                        loginProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        loginProgressBar.visibility = View.GONE
                        Snackbar.make(
                            loginFragmentScrollView1,
                            "Please create a account !",
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun navigationFun() {
        Log.d("rafeek", "navigationFun: e7na hena")
        val intent = Intent(context, MainActivity2::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun saveInSharedReferenceFun(userModel: UserModel) {
        val email = userModel.email
        val number = userModel.number
        val password = userModel.password
        val name = userModel.displayName
        val id = userModel.id
        val profileImage = userModel.profileImage

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(MainActivity.EMAIL, email)
        editor.putString(MainActivity.NUMBER, number)
        editor.putString(MainActivity.PASSWORD, password)
        editor.putString(MainActivity.DISPLAY_NAME, name)
        editor.putString(MainActivity.ID, id)
        editor.putString(MainActivity.PROFILE_IMAGE, profileImage)

        editor.apply()
    }
}