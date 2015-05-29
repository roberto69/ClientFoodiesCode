package foodies.gui.lab.clientfoodies;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Guillaume Labbe on 03/05/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    Context mContext;
    JSONArray foodies;

    public ListViewAdapter(Context context, JSONArray values) {
        mContext = context;
        foodies = values;
    }

    @Override
    public int getCount() {
        return foodies.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return foodies.getJSONObject(position);
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
        TextView short_desc;
        ImageView img;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.card_foodies, parent, false);
            init_layouts(convertView);
            holder.title = (TextView) convertView.findViewById(R.id.info_title_card);
            holder.short_desc = (TextView) convertView.findViewById(R.id.info_text_desc);
            holder.img = (ImageView) convertView.findViewById(R.id.img_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject foodie = (JSONObject) getItem(position);
        try {
            holder.title.setText(foodie.getString("name"));
            holder.short_desc.setText(foodie.getString("short_description"));
            requestImageRestaurant(foodie.getInt("id"), holder.img);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void init_layouts(View content) {
        LinearLayout ll_header = (LinearLayout) content.findViewById(R.id.linearFoodie);
        ll_header.getLayoutParams().height = MyUtils.getHeightRatio_16_9();
        ll_header.requestLayout();
    }

    private void requestImageRestaurant(int id, final ImageView img) {

        final String req = "api/restaurants/" + String.valueOf(id) + "/main_picture";
        final String[] url = {null};
        FoodieRestClient.get(req, "", null, new JsonHttpResponseHandler() {

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
