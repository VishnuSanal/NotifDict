package phone.vishnu.notifdict

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action

        if (action == null ||
            (action != Intent.ACTION_PROCESS_TEXT &&
                    action != Intent.ACTION_SEND)
        ) return

        val word = intent.extras?.getString(Intent.EXTRA_PROCESS_TEXT, null)

        if (word == null || word.split(" ").size > 1) {
            quit(getString(R.string.please_select_a_single_word))
            return
        }

        Log.e("vishnu", "word: $word")

        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"

        NotifDictController.getInstance().addToRequestQueue(
            StringRequest(
                Request.Method.GET, url,
                { response ->

                    val meaningsArray =
                        JSONArray(response).getJSONObject(0).getJSONArray("meanings");

                    val stringBuilder = StringBuilder()

                    for (i in 0 until meaningsArray.length()) {

                        val definition = meaningsArray.getJSONObject(i)

                        val definitionsArray =
                            definition.getJSONArray("definitions")

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

                },
                {
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