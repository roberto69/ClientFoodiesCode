package foodies.gui.lab.clientfoodies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume Labbe on 03/05/2015.
 **/

public class Discount extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_discounts, container, false);

        String id = getArguments().getString("discount");

        requestListDiscount(id, v);

        return v;
    }

    public void requestListDiscount(String id, final View v) {

        FoodieRestClient.get("api/restaurants/" + id + "/offers", "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray timeline) {

                ListView listFoodies = (ListView) v.findViewById(R.id.listDiscount);
                ListDiscAdapter adapter = new ListDiscAdapter(v.getContext(), timeline);
                listFoodies.setAdapter(adapter);
                listFoodies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        try {
                            new MaterialDialog.Builder(v.getContext())
                                    .title(timeline.getJSONObject(position).getString("name"))
                                    .content("Add this offer")
                                    .positiveText("ADD")
                                    .negativeText("CANCEL")
                                    .titleColorRes(R.color.colorPrimaryDark)
                                    .backgroundColorRes(R.color.colorText)
                                    .contentColorRes(R.color.colorSecondaryText)
                                    .widgetColor(R.color.colorText)
                                    .positiveColorRes(R.color.colorPrimaryDark)
                                    .negativeColorRes(R.color.colorPrimaryDark)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            try {
                                                requestAddOffer(timeline.getJSONObject(position).getInt("id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
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
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void requestAddOffer(int id) {
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        FoodieRestClient.post("api/me/offers/" + String.valueOf(id) + "/coupons", settings.getString("access_token", ""), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "Offer added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray timeline) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("FAIL ADD COUPON", "" + errorResponse);
                Toast.makeText(getActivity(), "You have to register", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
