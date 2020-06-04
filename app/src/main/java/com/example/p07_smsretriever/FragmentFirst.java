package com.example.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentFirst extends Fragment {

    Button b1;
    TextView tv1, tv2;
    EditText et1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        tv1 = view.findViewById(R.id.tvFrag1);
        tv2 = view.findViewById(R.id.textView);
        et1 = view.findViewById(R.id.editText);
        b1 = view.findViewById(R.id.btnAddTextFrag1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
                if (permission != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();
                String filter = "address LIKE ?";
                String[] fArgs = {"%" + et1.getText().toString() + "%"};
                Cursor cursor = cr.query(uri, reqCols, filter, fArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateinMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateinMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        //PS only wants RECEIVED smses
                        if (type.equalsIgnoreCase("1")) {
                            smsBody += "Inbox: " + address + "\nat " + date + "\n\"" + body + "\"\n\n";
                        }
                    } while (cursor.moveToNext());
                }
                tv2.setText(smsBody);
            }
        });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    b1.performClick();
                } else {
                    Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}