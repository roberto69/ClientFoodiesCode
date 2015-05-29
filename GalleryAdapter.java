package foodies.gui.lab.clientfoodies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume Labbe on 13/05/2015.
 */
public class GalleryAdapter extends BaseAdapter{

    Context mContext;
    JSONArray foodies;

    public GalleryAdapter(Context context, JSONArray values) {
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
        ImageView img;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_gallery, parent, false);
            init_layouts(convertView);
            holder.img = (ImageView) convertView.findViewById(R.id.img_gallery);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject picture = (JSONObject) getItem(position);
        try {
            Glide.with(mContext)
                    .load("http://foodie.dennajort.fr" + picture.getString("url"))
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo)
                    .crossFade()
                    .into(holder.img);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private void init_layouts(View content) {
        LinearLayout ll_header = (LinearLayout) content.findViewById(R.id.linearGallery);
        ll_header.getLayoutParams().height = MyUtils.getHeightRatio_16_9();
        ll_header.requestLayout();
    }
}

