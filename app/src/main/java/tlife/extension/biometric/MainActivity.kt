package tlife.extension.biometric

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private val RC_ENROLL_BIO = 999

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when(errorCode){

                        BiometricPrompt.ERROR_CANCELED -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {
                            val promptInfoDevice = requestPromptInfoDevice()
                            biometricPrompt.authenticate(promptInfoDevice)
                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {

                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                            }
                            startActivityForResult(enrollIntent, 999)
                        }
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_NO_SPACE -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_USER_CANCELED -> {
//                            TODO()
                        }
                        BiometricPrompt.ERROR_VENDOR -> {
//                            TODO()
                        }
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        findViewById<TextView>(R.id.tv_authen).setOnClickListener {
            checkAuthen()
        }
    }

    private fun requestPromptInfoBio(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("thử bằng cách khác đi đcm")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    private fun requestPromptInfoDevice(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(DEVICE_CREDENTIAL)
            .build()
    }

    private fun checkAuthen() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfoBio = requestPromptInfoBio()
                biometricPrompt.authenticate(promptInfoBio)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                onBioMetricNoneEnrolled()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
//                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
//                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
//                TODO()
            }
        }

    }

    private fun onBioMetricNoneEnrolled(
        enrollType: Int = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return
        val enrollIntent =  Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                enrollType)
        }
        startActivityForResult(enrollIntent, RC_ENROLL_BIO)
    }
}