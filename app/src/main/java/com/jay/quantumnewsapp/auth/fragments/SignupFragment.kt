package com.jay.quantumnewsapp.auth.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.jay.quantumnewsapp.auth.MainActivity
import com.jay.quantumnewsapp.databinding.FragmentSignupBinding
import com.jay.quantumnewsapp.utils.TAG
import com.jay.quantumnewsapp.utils.hasErrorOrEmpty
import com.jay.quantumnewsapp.utils.isValidEmail

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextFormats()

        binding.apply {
            setupValidityListeners(nameEdit)
            setupValidityListeners(phoneEdit)
            setupValidityListeners(mailEdit)
            setupValidityListeners(passEdit)
            ccp.registerCarrierNumberEditText(phoneEdit)

            loginLink.setOnClickListener {
                (activity as MainActivity).changeToTab(0)
            }

            registerBtn.setOnClickListener {
                if(areAllFieldsOkay()) {
                    if(binding.checkTerms.isChecked) registerAccount()
                    else showSnackBar("Please accept the terms and conditions!")
                } else showSnackBar("Please fill all fields properly!")
            }
        }
    }

    private fun registerAccount() {
        val userMail = binding.mailEdit.text.toString()
        val userPass = binding.passEdit.text.toString()
        auth.createUserWithEmailAndPassword(userMail, userPass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showSnackBar("Created account successfully! Please login to continue!")
                    auth.signOut()
                    (activity as MainActivity).changeToTab(0)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if(task.exception is FirebaseAuthUserCollisionException) {
                        showSnackBar("Mail is already registered, Try logging in!")
                        (activity as MainActivity).changeToTab(0)
                    } else {
                        showSnackBar("Sign up failed, try again later!")
                    }
                }
            }
    }

    private fun setupTextFormats() {
        binding.checkTerms.text = HtmlCompat.fromHtml(
            "I agree with the <span style=\"color:red;\"><u>terms & conditions</u></span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.loginLink.text = HtmlCompat.fromHtml(
            "Already have an account? <span style=\"color:red\">Sign in!</span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.registerBtn, message, Snackbar.LENGTH_LONG)
        snackBar.setAction("DISMISS") { snackBar.dismiss() }
            .setAnchorView(binding.registerBtn).show()
    }

    private fun setupValidityListeners(editText: EditText) {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                when(editText.inputType) {
                    33 -> { // Int Code for text email
                        editText.error = if(p0.isNullOrBlank()) {
                            "Field cannot be empty!"
                        } else if(!p0.isValidEmail()) {
                            "Enter a valid email!"
                        } else null
                    }
                    InputType.TYPE_CLASS_PHONE -> {
                        editText.error = if(p0.isNullOrBlank()) {
                            "Field cannot be empty!"
                        } else if(!binding.ccp.isValidFullNumber) {
                            "Enter a valid phone number!"
                        } else null
                    }
                    129 -> {
                        editText.error = if(p0.isNullOrBlank()) {
                            "Field cannot be empty!"
                        } else if(p0.length <= 7) {
                            "Password must be at least 8 characters!"
                        } else null
                    }
                    else -> {
                        editText.error = if(p0.isNullOrBlank()) {
                            "Field cannot be empty!"
                        } else null
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
        editText.addTextChangedListener(watcher)
    }

    private fun areAllFieldsOkay() : Boolean {
        binding.apply {
            return !(nameEdit.hasErrorOrEmpty()
                    || mailEdit.hasErrorOrEmpty()
                    || passEdit.hasErrorOrEmpty()
                    || phoneEdit.hasErrorOrEmpty())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}