package foodies.gui.lab.clientfoodies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.software.shell.fab.FloatingActionButton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Guillaume Labbe on 02/05/2015.
 **/
public class SignIn  extends Fragment {

    String mail;
    InterfaceCom mCallback;
    public static final String PREFS_NAME = "MyPrefsFile";

    MaterialEditText mMail;
    MaterialEditText mPassword;
    MaterialEditText mName;
    ImageView profile;
    Uri targetPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_signin, container, false);

        final Button button = (Button) v.findViewById(R.id.siginChild);
        final FloatingActionButton photo = (FloatingActionButton) v.findViewById(R.id.action_button);

        init_fields(v);
        init_layouts(v);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLICK", "" + "SIGN IN");
                validateAccount(view);
            }
        });

        return v;
    }

    private void init_layouts(View content) {
        LinearLayout ll_header = (LinearLayout) content.findViewById(R.id.linearProfileImg);
        ll_header.getLayoutParams().height = MyUtils.getHeightRatio_16_9();
        ll_header.requestLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            targetPhoto = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(new FlushedInputStream(getActivity().getContentResolver().openInputStream(targetPhoto)));
                profile.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    private void init_fields(View v) {
        mMail = (MaterialEditText) v.findViewById(R.id.mailText);
        mName = (MaterialEditText) v.findViewById(R.id.nameText);
        mPassword = (MaterialEditText) v.findViewById(R.id.passwordText);
        profile = (ImageView) v.findViewById(R.id.profileImage);

        mName.addValidator(new RegexpValidator("The firstname is required and cannot be empty.", "^.+$"));
        mPassword.addValidator(new RegexpValidator("The password is required and cannot be empty.", "^.+$"));
        mMail.addValidator(new RegexpValidator("The email address is required and cannot be empty.", "^.+$"))
                .addValidator(new RegexpValidator("The email address is not valid.", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"));
    }

    public void validateAccount(final View view) {

        final Boolean firstname = mName.validate();
        final Boolean email = mMail.validate();
        final Boolean password = mPassword.validate();
        Log.e("CLICK", "" + "VALIDATION");

        if (firstname && email && password) {
            RequestParams params = new RequestParams();
            params.add("firstname", mName.getText().toString());
            params.add("lastname", "test");
            params.add("email", mMail.getText().toString());
            params.add("password", mPassword.getText().toString());
            try {
                if (targetPhoto != null) {
                    params.put("picture", new File(getPath(targetPhoto)));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
            Log.e("POST", "" + "SIGN IN");

            FoodieRestClient.post("api/users", "", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("email", mMail.getText().toString());
                    editor.putString("username", mName.getText().toString());
                    editor.apply();
                    Log.e("SUCCESS 1", "" + response);
                    getActivity().onBackPressed();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.e("SUCCESS 2", "" + response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("tto", "" + errorResponse);
                }
            });
        }
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
