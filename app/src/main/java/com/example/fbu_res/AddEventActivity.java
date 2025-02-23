package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Event;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddEventActivity extends AppCompatActivity {

    // push notification logic
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" +
            "AAAATqE5Zos:APA91bHgAP71ezc4Ir2F042-RFZ19KOKMC3pSPyDFjtKom0kwR-vCCaZ7fMOv2P5T7BKoL--" +
            "93g0qIAG1jdq0os6XCHAD_fnCDX2ln1qeoqD8v12sP3XIgO_O9I8C0_Q1DU1OuRKXWo6";
    private static final String contentType = "application/json";

    // request queue
    private RequestQueue requestQueue;

    public static final String APP_TAG = "AddEventActivity";

    // date regex
    private static final Pattern date =
            Pattern.compile("^((0|1)\\d{1})/((0|1|2)\\d{1})/((19|20)\\d{2})");
    public static final Pattern zipcode =
            Pattern.compile("^\\d{5}$");

    // fields that are required by user when creating an event
    EditText etName;
    EditText etLocationName;
    EditText etAddressLine1;
    EditText etAddressLine2;
    EditText etZipcode;
    EditText etCity;
    String state;
    EditText etCountry;
    EditText etDate;
    EditText etDescription;
    ImageView ivPreview;

    // next button
    Button btnNext;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    // public String photoFileName;
    // File photoFile;
    ParseFile pf; // photo
    Bitmap selectedImage = null; // bitmap

    // button to launch gallery and select image
    Button btnSelectImage;

    // button to officially create the event and add to relation
    Button btnCreateEvent;

    // for the loading screen
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // setting up the request queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        viewDialog = new ViewDialog(this);

        etName = (EditText) findViewById(R.id.etName);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etName);
                validateNextButton();
            }
        });
        etLocationName = (EditText) findViewById(R.id.etLocationName);
        etLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etLocationName);
                validateNextButton();
            }
        });
        etAddressLine1 = (EditText) findViewById(R.id.etAdressline1);
        etAddressLine1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etAddressLine1);
                validateNextButton();
            }
        });
        etAddressLine2 = (EditText) findViewById(R.id.etAddressline2);
        etZipcode = (EditText) findViewById(R.id.etZipcode);
        etZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etZipcode);
                setZipcodeError(etZipcode);
                validateNextButton();
            }
        });
        etCity = (EditText) findViewById(R.id.etCity);
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etCity);
                validateNextButton();
            }
        });
        // setting up sorting by spinner input here
        Spinner spinner = (Spinner) findViewById(R.id.spinnerState);

        // creating adapter for the spinner
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(this, R.array.state_arrays, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = parent.getItemAtPosition(position).toString();
                validateNextButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                state = "Select state";
            }
        });

        etCountry = (EditText) findViewById(R.id.etCountry);
        etCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTextError(etCountry);
                validateNextButton();
            }
        });

        // initially making the state of the button unclickable
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setEnabled(false);
        btnNext.setClickable(false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_add_event_next);
                // setting up the rest of the attributes
                etDate = (EditText) findViewById(R.id.etDate);
                etDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isInputEmpty(etDate)) setTextError(etDate);
                        setDateError(etDate);
                        validateCreateButton();
                    }
                });
                etDescription = (EditText) findViewById(R.id.etDescription);
                etDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isInputEmpty(etDescription)) setTextError(etDescription);
                        validateCreateButton();
                    }
                });

                ivPreview = (ImageView) findViewById(R.id.ivPreview);

                btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
                btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);

                // initially making the create button unclickable
                btnCreateEvent.setEnabled(false);
                btnCreateEvent.setClickable(false);

                btnSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPickPhoto(v);
                    }
                });

                btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewDialog.showDialog();

                        final User user = (User) ParseUser.getCurrentUser();

                        final Address address = new Address();
                        address.setName(etLocationName.getText().toString());
                        address.setAddressline1(etAddressLine1.getText().toString());
                        address.setAddressline2(etAddressLine2.getText().toString());
                        address.setCity(etCity.getText().toString());
                        address.setState(state);
                        address.setZipcode(etZipcode.getText().toString());
                        address.setCountry(etCountry.getText().toString());

                        address.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                final Event event = new Event();

                                // setting the attributes for an event
                                event.setName(etName.getText().toString());
                                event.setDate(etDate.getText().toString());
                                event.setDescription(etDescription.getText().toString());
                                event.setLocation(address);
                                event.setOwner(user);
                                // compressing image
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                final byte[] d = stream.toByteArray();
                                pf = new ParseFile("image.png",d);
                                pf.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        event.setImage(pf);
                                        event.setOwner(ParseUser.getCurrentUser());
                                        event.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                user.addCreatedEvents(event);
                                                String strAddresss = address.getAddressline1() + " "+
                                                        address.getAddressline2() + ", " +
                                                        address.getCity() + ", " + address.getState()+ " "+
                                                        address.getZipcode() + ", "+
                                                        address.getCountry()+ " ";
                                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                                List<android.location.Address> addresses;
                                                try{
                                                    addresses = geocoder.getFromLocationName(strAddresss, 5);
                                                    android.location.Address loc = addresses.get(0);
                                                    address.setPin(new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));
                                                    event.setDistanceToUser(user.getLocation().distanceInMilesTo(address.getParseGeoPoint(Address.KEY_PIN)));
                                                }catch (Exception eo){
                                                    eo.printStackTrace();
                                                }
                                                Toast.makeText(getApplicationContext(),
                                                        "successfully created the event", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(AddEventActivity.this, HomeActivity.class);
                                                startActivity(i);
                                                viewDialog.hideDialog();
                                                // the firebase push notification logic
                                                FirebaseMessaging.getInstance().subscribeToTopic("/topics/event_alert");
                                                JSONObject notification = new JSONObject();
                                                JSONObject notificationBody = new JSONObject();
                                                String topic = "/topics/event_alert";

                                                try{
                                                    notificationBody.put("title","yo");
                                                    notificationBody.put("message", "testing");
                                                    notification.put("to", topic);
                                                    notification.put("data", notificationBody);
                                                } catch (JSONException er) {
                                                    Log.e("TAG", er.getMessage());
                                                    e.printStackTrace();
                                                }

                                                sendNotification(notification);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    // send notification method
    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TAG", "onResponse");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "sendNotification");
                        error.printStackTrace();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }

        };
        requestQueue.add(jsonObjectRequest);
    }

    // does the input text have anything in it
    public boolean isInputEmpty(EditText et){
        return et.getText().toString().length() == 0;
    }

    // setting the error
    public void setTextError(EditText et){
        if(isInputEmpty(et)) et.setError("This field cannot be empty");
        else et.setError(null);
    }

    // setting date error
    public void setDateError(EditText et){
        if(!isCorrectDate(et)){
            et.setError("Please format valid date as: mm/dd/yyyy");
            return;
        }
        et.setError(null);
    }

    // setting zipcode error
    public void setZipcodeError(EditText et){
        if(!isCorrectZipcde(et)){
            et.setError("Please format valid zipcode as: xxxxx");
            return;
        }
        et.setError(null);
    }

    // does the input text match the correct date format
    public boolean isCorrectDate(EditText et){
        return date.matcher(et.getText().toString()).matches() &&
                Integer.parseInt(et.getText().toString().substring(6)) >= Calendar.getInstance().get(Calendar.YEAR);
    }

    // is this a correct zipcode format
    public boolean isCorrectZipcde(EditText et){
        return zipcode.matcher(et.getText().toString()).matches();
    }

    // create button only clickable if all fields are valid
    public void validateCreateButton(){
        if(!isInputEmpty(etDate) && isCorrectDate(etDate) && !isInputEmpty(etDescription) &&
        ivPreview.getDrawable() != null){
            btnCreateEvent.setClickable(true);
            btnCreateEvent.setEnabled(true);
        }else{
            btnCreateEvent.setEnabled(false);
            btnCreateEvent.setClickable(false);
        }
    }

    // next button only clickable if all fields are valid
    public void validateNextButton(){
        if(!isInputEmpty(etName) && !isInputEmpty(etLocationName) && !isInputEmpty(etAddressLine1) &&
                !isInputEmpty(etZipcode) && !isInputEmpty(etCity) &&
                !state.equals("Select state") && !isInputEmpty(etCountry)){
            btnNext.setClickable(true);
            btnNext.setEnabled(true);
        } else{
            btnNext.setClickable(false);
            btnNext.setEnabled(false);
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivPreview.setImageBitmap(selectedImage);
            validateCreateButton();
        }
    }

}
