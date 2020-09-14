package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.adapter.ExpandableListAdapter;
import in.alertmeu.adapter.MainCatListAdpter;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.imageUtils.ImageloaderNew;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;
import in.alertmeu.view.SubCatDetailsView;

public class BusinessMainCategoryActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    RecyclerView mainCatList;
    JSONObject jsonLeadObj, jsonSchedule;
    JSONArray jsonArray;
    String myPlaceListResponse = "", imagePathResponse = "";
    List<MainCatModeDAO> data;
    MainCatListAdpter mainCatListAdpter;
    LinearLayout showhide;
    ProgressDialog mProgressDialog;
    LinearLayout btnNext, shmsg;
    Resources res;
    boolean status;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_business_main_category);

        mainCatList = (RecyclerView) findViewById(R.id.mainCatList);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        shmsg = (LinearLayout) findViewById(R.id.shmsg);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        data = new ArrayList<>();
        // ImageloaderNew imageLoader = new ImageloaderNew(BusinessMainCategoryActivity.this);
        //  imageLoader.clearCache();
        ImageLoader imageLoader = new ImageLoader(BusinessMainCategoryActivity.this);
        imageLoader.clearCache();
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new getMainCategoryList().execute();
            getImagePath();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        SubCatDetailsView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    new getMainCategoryList().execute();
                    getImagePath();
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    /*Intent intent = new Intent(BusinessMainCategoryActivity.this, BusinessSubCategoryActivity.class);
                    startActivity(intent);*/
                    Intent intent = new Intent(BusinessMainCategoryActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class getMainCategoryList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BusinessMainCategoryActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLUSERMAINCATEGORY, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {


                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseMainCatList(myPlaceListResponse);
                                jsonArray = new JSONArray(myPlaceListResponse);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (data.size() > 0) {
                mainCatListAdpter = new MainCatListAdpter(BusinessMainCategoryActivity.this, data);
                mainCatList.setAdapter(mainCatListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(BusinessMainCategoryActivity.this));
                mainCatListAdpter.notifyDataSetChanged();

            } else {

            }
        }
    }

    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("user_id", preferences.getString("user_id", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };


        Thread objectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonSchedule);
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_CUCBID, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                shmsg.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                            } else {
                                btnNext.setVisibility(View.GONE);
                                shmsg.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        objectThread.start();

    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
