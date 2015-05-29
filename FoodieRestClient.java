package foodies.gui.lab.clientfoodies;

import android.util.Log;

import com.loopj.android.http.*;

/**
 * Created by Guillaume Labbe on 03/05/2015.
 */
public class FoodieRestClient {
    private static final String BASE_URL = "http://foodie.dennajort.fr/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        addHeader(token);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        addHeader(token);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        addHeader(token);
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static void addHeader(String header){
        if (!(header.equals(""))){
            client.addHeader("Authorization", "Bearer " + header);
        }
    }
}
