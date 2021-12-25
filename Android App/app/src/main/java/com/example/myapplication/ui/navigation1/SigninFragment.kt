package com.example.myapplication.ui.navigation1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.SigninViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.coroutines.flow.collect

class SigninFragment : Fragment() {

    private lateinit var viewModel: SigninViewModel
    private lateinit var token: String
    private lateinit var user: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[SigninViewModel::class.java]
        createAccountBtn.setOnClickListener {
            if (validData()) {
                val displayName = createAccountDisplayName.text.trim().toString()
                val email = createAccountEmail.text.trim().toString()
                val password = createAccountPassword.text.trim().toString()
                val number = createAccountNumber.text.trim().toString()
                createToken()
                user = UserModel(displayName, email, password, number)
            }
        }
    }

    private fun createNewUser(user: UserModel) {
        viewModel.createNewUser(user)
        lifecycleScope.launchWhenCreated {
            viewModel.createUserState.collect {
                when (it) {
                    is Result.Success -> {
                        createAccountProgressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_signinFragment_to_loginFragment)
                    }
                    is Result.Loading -> {
                        createAccountProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        Toast.makeText(context, "Error... try again", Toast.LENGTH_SHORT).show()
                        createAccountProgressBar.visibility = View.GONE
                        Log.d("rafeek", "createNewUser: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
            user.token = it
            createNewUser(user)
        }
    }

    private fun validData(): Boolean {
        if (createAccountDisplayName.text.toString().trim()
                .isNotEmpty() && createAccountEmail.text.toString().trim()
                .isNotEmpty() && createAccountPassword.text.toString().trim()
                .isNotEmpty() && createAccountNumber.text.toString().trim()
                .isNotEmpty()
        ) {
            return true
        }
        return false
    }

}