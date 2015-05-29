package foodies.gui.lab.clientfoodies;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Guillaume Labbe on 15/05/2015.
 */
public class DescPicture extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        String id = getArguments().getString("pictures");

        requestGallery(id, v);

        return v;
    }

    private void requestGallery(String id, final View v) {

        FoodieRestClient.get("api/restaurants/" + id + "/gallery", "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                GridView gridview = (GridView) v.findViewById(R.id.gridview);
                gridview.setAdapter(new GalleryAdapter(v.getContext(), response));

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.getStackTraceString(throwable);
                Toast.makeText(getActivity(), "Fail log in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
