package foodies.gui.lab.clientfoodies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.software.shell.fab.FloatingActionButton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Guillaume Labbe on 03/05/2015.
 **/

public class DescFoodie extends Fragment {

    MapView mapView;
    GoogleMap map;
    JSONObject foodie;
    TextView mail;
    TextView phone;
    TextView address;
    ImageView main_pic;
    Button discount;
    FloatingActionButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_desc, container, false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        //mapView.onResume();

        init_layouts(v);

        String descFoodie = getArguments().getString("descFoodie");
        try {
            foodie = new JSONObject(descFoodie);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mail = (TextView) v.findViewById(R.id.info_desc_mail);
        phone = (TextView) v.findViewById(R.id.info_desc_phone);
        address = (TextView) v.findViewById(R.id.info_desc_address);
        main_pic = (ImageView) v.findViewById(R.id.info_img_desc);

        try {
            requestDescRestaurant(foodie.getInt("id"), main_pic, v, savedInstanceState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void init_layouts(View content) {
        LinearLayout ll_header = (LinearLayout) content.findViewById(R.id.mapLayout);
        ll_header.getLayoutParams().height = MyUtils.getHeightRatio_16_9();
        ll_header.requestLayout();

        LinearLayout ll_header2 = (LinearLayout) content.findViewById(R.id.imageDescImg);
        ll_header2.getLayoutParams().height = MyUtils.getHeightRatio_16_9();
        ll_header2.requestLayout();
    }

    private void requestDescRestaurant(int id, final ImageView img, final View v, final Bundle savedInstanceState) {

        final String req = "api/restaurants/" + String.valueOf(id) + "/main_picture";
        final String[] url = {null};
        FoodieRestClient.get(req, "", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    url[0] = "http://foodie.dennajort.fr" + response.getString("url");
                    Glide.with(v.getContext())
                            .load(url[0])
                            .centerCrop()
                            .placeholder(R.drawable.ic_photo)
                            .crossFade()
                            .into(img);
                    initializeParameters(v, savedInstanceState);
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

    private void initializeParameters(View v, Bundle savedInstanceState) {

        try {
            mail.setText(foodie.getString("email"));
            phone.setText(foodie.getString("phone"));
            address.setText(foodie.getString("address"));
            createMap(v, savedInstanceState, foodie.getDouble("longitude"), foodie.getDouble("latitude"), foodie.getString("name"), foodie.getString("short_description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        button = (FloatingActionButton) v.findViewById(R.id.action_gallery);
        discount = (Button) v.findViewById(R.id.seeDiscount);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment descPicture = new DescPicture();

                Bundle args = new Bundle();
                try {
                    args.putString("pictures", foodie.getString("id"));
                } catch (JSONException e) {
                    args.putString("descFoodie", "");
                    e.printStackTrace();
                }
                descPicture.setArguments(args);

                ((MaterialNavigationDrawer) getActivity()).setFragmentChild(descPicture, "Gallery");
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment discount = new Discount();

                Bundle args = new Bundle();
                try {
                    args.putString("discount", foodie.getString("id"));
                } catch (JSONException e) {
                    args.putString("descFoodie", "");
                    e.printStackTrace();
                }
                discount.setArguments(args);

                ((MaterialNavigationDrawer) getActivity()).setFragmentChild(discount, "Discount");
            }
        });
    }

    public void createMap(View v, Bundle savedInstanceState, double longitude, double latitude, String name, String short_description) {

        /*MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);*/
        // Gets the MapView from the XML layout and creates it

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(v.getContext());

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
        map.animateCamera(cameraUpdate);

        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(short_description));
        marker.showInfoWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
