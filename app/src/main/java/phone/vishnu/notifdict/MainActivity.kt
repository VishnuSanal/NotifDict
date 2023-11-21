package phone.vishnu.notifdict

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action

        if (action == null ||
            (action != Intent.ACTION_PROCESS_TEXT &&
                    action != Intent.ACTION_SEND)
        ) return

        Log.e("vishnu", "onCreate() called with: $intent")

        val word = intent.extras?.getString(Intent.EXTRA_PROCESS_TEXT, null)

        if (word == null || word.split(" ").size > 1) {
            Toast.makeText(this, getString(R.string.please_select_a_single_word), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        NotificationHelper.createNotification(this, word)

        finish()
    }
}