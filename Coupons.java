package foodies.gui.lab.clientfoodies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume Labbe on 20/05/2015.
 **/

public class Coupons extends Fragment {

    public static String sendSecret = null;
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences access = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coupons, container, false);

        ListView listCoupons = (ListView) v.findViewById(R.id.listCoupons);
        access = v.getContext().getSharedPreferences(PREFS_NAME, 0);
        requestListCoupons(listCoupons, v.getContext());

        return v;
    }

    private void requestListCoupons(final ListView listCoupons, final Context context) {

        RequestParams params = new RequestParams();
        params.put("used", "false");

        FoodieRestClient.get("api/me/coupons", access.getString("access_token", ""), params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray timeline) {

                ListTextAdapter adapter = new ListTextAdapter(getActivity(), timeline);
                listCoupons.setAdapter(adapter);
                listCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            requestDialogOffer(context, timeline.getJSONObject(position).getInt("offer"), timeline.getJSONObject(position).getString("secret"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("tto", "" + errorResponse);
            }
        });
    }

    private void requestDialogOffer(final Context context, int id, final String secret) {

        FoodieRestClient.get("api/offers/" + String.valueOf(id), "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                try {
                    new MaterialDialog.Builder(context)
                            .title(response.getString("name"))
                            .content(response.getString("description") + "\n" + response.getString("expiration_date").substring(0, 10))
                            .positiveText("CHECK")
                            .negativeText("CANCEL")
                            .titleColorRes(R.color.colorPrimaryDark)
                            .contentColorRes(R.color.colorSecondaryText)
                            .backgroundColorRes(R.color.colorText)
                            .widgetColor(R.color.colorText)
                            .positiveColorRes(R.color.colorPrimaryDark)
                            .negativeColorRes(R.color.colorPrimaryDark)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    validateCoupon(secret);
                                    //sendSecret = null;
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                }
                            })
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("GET OFFER", "" + errorResponse);
            }
        });
    }

    private void validateCoupon(String secret) {
        sendSecret = secret;
    }
}
