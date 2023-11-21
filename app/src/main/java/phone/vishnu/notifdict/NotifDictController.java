package phone.vishnu.notifdict;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NotifDictController extends Application {

    private static final String TAG = NotifDictController.class.getSimpleName();
    private static NotifDictController instance;
    private RequestQueue requestQueue;

    public static synchronized NotifDictController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelAllRequests() {
        requestQueue.cancelAll(TAG);
    }
}
