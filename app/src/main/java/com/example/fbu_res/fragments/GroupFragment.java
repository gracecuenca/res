package com.example.fbu_res.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.example.fbu_res.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.PubNub;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@ParseClassName("Group")
public class GroupFragment extends Fragment {

    PubNub mPubnub_DataStream;
    DatabaseReference RootRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.Viewpager);
        GroupFragmentPagerAdapter pagerAdapter = new GroupFragmentPagerAdapter(getFragmentManager(),
                getContext());
        viewPager.setAdapter(pagerAdapter);

        RootRef = FirebaseDatabase.getInstance().getReference();
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fabBtn = view.findViewById(R.id.fabNewGroup);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewGroup(v);
            }
        });

    }

    public void requestNewGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupFragment.this.getContext()
                , R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(GroupFragment.this.getContext());
        groupNameField.setHint("e.g. Ayyo's Group?");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString() +
                        ParseUser.getCurrentUser().getUsername() + new Timestamp(System.currentTimeMillis());
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(GroupFragment.this.getContext(), "Please write more..."
                            , Toast.LENGTH_SHORT);
                } else {
                    createNewGroup(groupName, groupNameField.getText().toString());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(final String groupName, String name) {
        RootRef.child("Groups").child(groupName.replaceAll("[^a-zA-Z0-9]", "")).setValue("")
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(GroupFragment.this.getContext(), groupName + " is Created Succesfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setNumMembs(1);
        newGroup.setImage(conversionBitmapParseFile(drawableToBitmap(getResources().getDrawable(R.drawable.ic_launcher_background))));
        List<ParseUser> users = new ArrayList<>();
        users.add(ParseUser.getCurrentUser());
        newGroup.setMembers(users);
        newGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Group Fragment", "Saved succesfully");
            }
        });
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}
