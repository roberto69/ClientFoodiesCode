package foodies.gui.lab.clientfoodies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume Labbe on 21/05/2015.
 **/
public class ListTextAdapter extends BaseAdapter {

    Context mContext;
    JSONArray coupons;

    public ListTextAdapter(Context context, JSONArray values) {
        mContext = context;
        coupons = values;
    }

    @Override
    public int getCount() {
        return coupons.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return coupons.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.card_coupons, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.coupons_secret);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject foodie = (JSONObject) getItem(position);
        try {
            requestNameOffer(foodie.getInt("offer"), holder.title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void requestNameOffer(int id, final TextView txt) {

        FoodieRestClient.get("api/offers/" + String.valueOf(id), "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                try {
                    txt.setText(response.getString("name") + " - " + response.getString("description"));
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
}