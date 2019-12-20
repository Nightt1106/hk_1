package com.night.hk_1

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*

class MainActivity : AppCompatActivity() , FirebaseAuth.AuthStateListener{

    private val RC_SIGN_IN: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        button_verify.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null)
                if (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                    FirebaseAuth.getInstance().currentUser
                        ?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful)
                                Snackbar.make(it,"Check your email",Snackbar.LENGTH_LONG).show()
                        }
                }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_sign_in ->{
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(arrayListOf(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build()
                    ))
                    .build(),
                RC_SIGN_IN)
                true
            }

            R.id.action_sign_out -> {
                Log.d("Signout","${FirebaseAuth.getInstance().currentUser} is sign out")
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Log.d("onAuthStateChanged", FirebaseAuth.getInstance()?.currentUser.toString())
        if(FirebaseAuth.getInstance().currentUser != null) {
            user_info.textView.text = "${FirebaseAuth.getInstance()?.currentUser?.email}"
            button_verify.visibility = if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified) View.GONE else View.VISIBLE
        } else {
            user_info.textView.text = "Not Log in"
        }

    }
}
