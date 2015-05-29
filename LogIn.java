package foodies.gui.lab.clientfoodies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

import javax.security.auth.callback.Callback;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

import static foodies.gui.lab.fuckfoodies.InterfaceCom.*;

/**
 * Created by Guillaume Labbe on 02/05/2015.
 **/

public class LogIn extends Fragment{

    MaterialEditText mail;
    MaterialEditText password;
    SharedPreferences settings;
    InterfaceCom mCallback;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_login, container, false);

        final Button login = (Button) v.findViewById(R.id.login);
        final Button signin = (Button) v.findViewById(R.id.signin);

        init_fields(v);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOn(view);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MaterialNavigationDrawer) v.getContext()).setFragmentChild(new SignIn(), "Registration");
            }
        });
        return v;
    }

    private void init_fields(View v) {
        mail = (MaterialEditText) v.findViewById(R.id.mailText2);
        password = (MaterialEditText) v.findViewById(R.id.passwordText2);

        password.addValidator(new RegexpValidator("The password is required and cannot be empty.", "^.+$"));
        mail.addValidator(new RegexpValidator("The email address is required and cannot be empty.", "^.+$"))
                .addValidator(new RegexpValidator("The email address is not valid.", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"));

        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    private void logOn(final View view) {

        final Boolean email = mail.validate();
        final Boolean pass = password.validate();
        Log.e("CLICK", "" + "VALIDATION");

        if (email && pass) {
            RequestParams params = new RequestParams();
            params.put("grant_type", "password");
            params.put("username", mail.getText().toString());
            params.put("password", password.getText().toString());

            FoodieRestClient.post("oauth/access_token", "", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences.Editor editor = settings.edit();
                    try {
                        editor.putString("access_token", response.getString("access_token"));
                        editor.putString("refresh_token", response.getString("refresh_token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.apply();
                    mCallback = (InterfaceCom)view.getContext();
                    mCallback.setDrawer(settings.getString("email", ""), settings.getString("username", ""), "");
                    ((MaterialNavigationDrawer)getActivity()).setFragment(new Foodies(), "Foodies");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.getStackTraceString(throwable);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfile();
    }

    private void setProfile() {
        String email = settings.getString("email", "");
        mail.setText(email);
    }
}
