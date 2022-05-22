package com.jay.quantumnewsapp.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.jay.quantumnewsapp.auth.MainActivity
import com.jay.quantumnewsapp.databinding.FragmentLoginBinding
import com.jay.quantumnewsapp.utils.TAG
import com.jay.quantumnewsapp.utils.hasErrorOrEmpty
import com.jay.quantumnewsapp.utils.isValidEmail

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var facebookCallbackManager: CallbackManager

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerLink.text = HtmlCompat.fromHtml(
                "Don't have an account? <span style=\"color:red\">Register Now!</span>",
                HtmlCompat.FROM_HTML_MODE_LEGACY)
            registerLink.setOnClickListener { (activity as MainActivity).changeToTab(1) }

            setupValidityListeners(mailEdit)
            setupValidityListeners(passEdit)

            googleLogin.setOnClickListener {
                auth.signOut()
                googleSignIn()
            }

            fbLogin.setOnClickListener {
                auth.signOut()
                fbSignIn()
            }

            loginBtn.setOnClickListener {
                if(mailEdit.hasErrorOrEmpty() || passEdit.hasErrorOrEmpty()) {
                    showSnackBar("Please fill all fields properly!")
                } else {
                    loginUserWithMailPassword()
                }
            }

            resetPass.setOnClickListener {
                if(mailEdit.hasErrorOrEmpty()) {
                    Toast.makeText(requireContext(),
                        "Please enter email id to reset password", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(binding.mailEdit.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showSnackBar("Reset password email sent!")
                            } else {
                                Log.d(TAG, "Error while resseting password -> ${task.exception}" )
                                if(task.exception is FirebaseAuthInvalidUserException) {
                                    showSnackBar("User not found, try creating a new account!")
                                } else {
                                    showSnackBar("Something went wrong while resetting password!")
                                }
                            }
                        }
                }
            }
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("562348717995-cm8t7f82g19s0up80s7h325ap8f6v3i7.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.loginBtn, message, Snackbar.LENGTH_LONG)
        snackBar.setAction("DISMISS") { snackBar.dismiss() }
            .setAnchorView(binding.loginBtn).show()
    }

    private fun loginUserWithMailPassword() {
        val email = binding.mailEdit.text.toString()
        val password = binding.passEdit.text.toString()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    showSnackBar("Authentication successful!")
                    (activity as MainActivity).goToHomePage()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showSnackBar("Authentication failed.")
                }
            }
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
                    129 -> { // Int Code for text password
                        editText.error = if(p0.isNullOrBlank()) {
                            "Field cannot be empty!"
                        } else if(p0.length <= 7) {
                            "Password must be at least 8 characters!"
                        } else null
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
        editText.addTextChangedListener(watcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    * GOOGLE SIGN IN METHODS
    * */

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // FACEBOOK
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)

        // GOOGLE
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            } else {
                Log.w(TAG, exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    showSnackBar("Google sign in successful for ${auth.currentUser?.displayName}")
                    (activity as MainActivity).goToHomePage()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showSnackBar("Google Sign in failed! Please try again later!")
                }
            }
    }

    /**
     * FACEBOOK SIGN IN METHODS
     */

    private fun fbSignIn() {
        facebookCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, facebookCallbackManager, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(facebookCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "signInWithCredential:success $user")
                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}.",
                        Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).goToHomePage()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}