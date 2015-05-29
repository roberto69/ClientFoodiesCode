package foodies.gui.lab.clientfoodies;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * Created by Guillaume Labbe on 02/05/2015.
 **/

public class Foodies extends Fragment {

    ListView listFoodies;
    ListViewAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_foodies, container, false);

        listFoodies = (ListView)v.findViewById(R.id.listFoodies);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        requestAllRestaurants();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        return v;
    }

    private void requestAllRestaurants() {

        FoodieRestClient.get("api/restaurants", "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray timeline) {

                adapter = new ListViewAdapter(getActivity(), timeline);
                listFoodies.setAdapter(adapter);
                listFoodies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Fragment descFoodie = new DescFoodie();

                            JSONObject foodie = timeline.getJSONObject(position);
                            Bundle args = new Bundle();
                            String description = foodie.toString();
                            args.putString("descFoodie", description);
                            descFoodie.setArguments(args);

                            ((MaterialNavigationDrawer) getActivity()).setFragmentChild(descFoodie, foodie.getString("name"));
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

    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestAllRestaurants();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2);
    }
}


