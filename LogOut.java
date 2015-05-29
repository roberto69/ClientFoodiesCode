package foodies.gui.lab.clientfoodies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Guillaume Labbe on 18/05/2015.
 **/
public class LogOut extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_logout, container, false);

        final Button logout = (Button) v.findViewById(R.id.logout);
        final SharedPreferences info = v.getContext().getSharedPreferences(PREFS_NAME, 0);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = info.edit();
                editor.remove("access_token");
                editor.remove("refresh_token");
                editor.remove("email");
                editor.remove("username");
                editor.apply();

                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(myIntent);
            }
        });

        return v;
    }
}
