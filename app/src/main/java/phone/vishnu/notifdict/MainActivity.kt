package phone.vishnu.notifdict

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                            Uri.fromParts(
                                "package", packageName, null
                            )
                        )
                )
                quit("Please enable notification permission from the settings")
            }

            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                Log.e("vishnu", "onCreate() called with: isGranted = $isGranted")
                if (isGranted) {
                    fetchMeaning(intent);
                } else {
                    quit("Can't post notifications without Notification permission")
                }
            }.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        val action = intent.action

        if (action == null || (action != Intent.ACTION_PROCESS_TEXT && action != Intent.ACTION_SEND)) return

        fetchMeaning(intent)
    }

    private fun fetchMeaning(intent: Intent) {
        var word =
            intent.extras?.getString(Intent.EXTRA_PROCESS_TEXT, null) ?: intent.extras?.getString(
                Intent.EXTRA_TEXT, null
            )

        if (word == null) {
            quit(getString(R.string.please_select_a_single_word))
            return
        }

        word = word.replace("\"", "").trim().split(" ")[0]

        Log.e("vishnu", "word: $word")

        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"

        NotifDictController.getInstance().addToRequestQueue(
                StringRequest(Request.Method.GET, url, { response ->

                    val meaningsArray =
                        JSONArray(response).getJSONObject(0).getJSONArray("meanings");

                    val stringBuilder = StringBuilder()

                    for (i in 0 until meaningsArray.length()) {

                        val definition = meaningsArray.getJSONObject(i)

                        val definitionsArray = definition.getJSONArray("definitions")

                        stringBuilder.append(
                            "${definition.getString("partOfSpeech")}:\n"
                        )

                        for (j in 0 until definitionsArray.length()) {

                            val meaning = definitionsArray.getJSONObject(j)

                            stringBuilder.append(
                                " -> ${
                                    meaning.getString("definition")
                                }\n"
                            )
                        }
                    }

                    NotificationHelper.createNotification(
                        this, word, stringBuilder.toString()
                    )

                }, {
                    if (it.networkResponse.statusCode == 404) {
                        quit("Word not found!")
                    } else {
                        quit("Whoops! Something went wrong!")
                    }
                })
            )

        finish()
    }

    private fun quit(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStop() {
        super.onStop()
        NotifDictController.getInstance().cancelAllRequests()
    }
}