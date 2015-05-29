package foodies.gui.lab.clientfoodies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume Labbe on 03/05/2015.
 **/
public class ListDiscAdapter extends BaseAdapter {

    Context mContext;
    JSONArray items;
    public static final String PREFS_NAME = "MyPrefsFile";

    public ListDiscAdapter(Context context, JSONArray values) {
        mContext = context;
        items = values;
    }

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return items.getJSONObject(position);
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
        TextView desc;
        TextView finish;
        ImageView img;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.card_discount, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.disc_text);
            holder.desc = (TextView) convertView.findViewById(R.id.disc_desc);
            holder.finish = (TextView) convertView.findViewById(R.id.disc_finish);
            holder.img = (ImageView) convertView.findViewById(R.id.disc_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject foodie = (JSONObject) getItem(position);
        try {
            holder.title.setText(foodie.getString("name"));
            holder.desc.setText(foodie.getString("description"));
            holder.finish.setText(foodie.getString("expiration_date").substring(0, 10));
            requestImageDiscount(foodie.getInt("restaurant"), holder.img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void requestImageDiscount(int id, final ImageView img) {

        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        final String req = "api/restaurants/" + String.valueOf(id) + "/main_picture";
        final String[] url = {null};
        FoodieRestClient.get(req, settings.getString("access_token", ""), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    url[0] = "http://foodie.dennajort.fr" + response.getString("url");
                    Glide.with(mContext)
                            .load(url[0])
                            .centerCrop()
                            .placeholder(R.drawable.ic_photo)
                            .crossFade()
                            .into(img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("tto " + req, "" + errorResponse);
            }
        });
    }
}