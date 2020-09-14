package in.alertmeu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;


public class SubCatListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<SubCatModeDAO> data;
    SubCatModeDAO current;
    String id, id1;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj;
    JSONArray jsonArray;
    boolean status;
    String message = "";
    String msg = "";
    String deleteResponse = "", businessCatResponse = "";

    private static Listener mListener;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    // create constructor to innitilize context and data sent from MainActivity
    public SubCatListAdpter(Context context, List<SubCatModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        res = context.getResources();
        loadLanguage(context);

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_subcat_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);

        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.notes.setText(current.getSubcategory_name());
            myHolder.notes.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.notes.setText(current.getSubcategory_name_hindi());
            myHolder.notes.setTag(position);
        }

        if (!current.getImage_path().equals("")) {
           // ImageLoader imageLoader = new ImageLoader(context);
          //  imageLoader.DisplayImage(current.getImage_path(), myHolder.subimage);
            myHolder.subimage.setTag(position);
            Picasso.with(context).load(current.getImage_path()).noPlaceholder().into((ImageView) myHolder.subimage);

        } else {

            myHolder.subimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_sub_category));
            myHolder.subimage.setTag(position);

        }
        myHolder.id.setText(current.getId());
        myHolder.id.setTag(position);
        myHolder.chkBox.setTag(position);
        myHolder.chkBox.setChecked(data.get(position).isSelected());
        myHolder.chkBox.setTag(data.get(position));
        if (current.getChecked_status().equals("1")) {
            myHolder.chkBox.setChecked(true);
            current.setSelected(true);
        } else {
            myHolder.chkBox.setChecked(false);
            current.setSelected(false);
        }
        myHolder.chkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                SubCatModeDAO contact = (SubCatModeDAO) cb.getTag();
                contact.setSelected(cb.isChecked());
                data.get(pos).setSelected(cb.isChecked());
              //  mListener.messageReceived(message);
                /*if (cb.isChecked()) {
                    //Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked() + data.get(pos).getId(), Toast.LENGTH_LONG).show();
                    Config.VALUE.add(data.get(pos).getId());
                    id = data.get(pos).getId();

                    myHolder.chkBox.setChecked(true);
                    new submitData().execute();
                } else if (!cb.isChecked()) {
                    // Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked() + data.get(pos).getId(), Toast.LENGTH_LONG).show();
                    id1 = data.get(pos).getId();
                    myHolder.chkBox.setChecked(false);

                    new deleteSale().execute();
                    Config.VALUE.remove(data.get(pos).getId());

                }*/


            }
        });
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView txt_date, notes, id;
        CheckBox chkBox;
        ImageView subimage;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            notes = (TextView) itemView.findViewById(R.id.comments);
            notes = (TextView) itemView.findViewById(R.id.comments);
            id = (TextView) itemView.findViewById(R.id.id);
            chkBox = (CheckBox) itemView.findViewById(R.id.chkBox);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);

        }

    }

    private class submitData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            //   mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //   mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("subbc_id", id);
                        put("user_id", preferences.getString("user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            businessCatResponse = serviceAccess.SendHttpPost(Config.URL_SAVEUSERSUBCAT, jsonLeadObj);
            Log.i("resp", "businessCatResponse" + businessCatResponse);


            if (businessCatResponse.compareTo("") != 0) {
                if (isJSONValid(businessCatResponse)) {


                    try {

                        JSONObject jsonObject = new JSONObject(businessCatResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {


//            Toast.makeText(context, "Please check your webservice", Toast.LENGTH_LONG).show();


                }
            } else {

                // Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                //  mProgressDialog.dismiss();

            }
        }
    }

    // method to access in activity after updating selection
    public List<SubCatModeDAO> getSservicelist() {
        return data;
    }
    public static void bindListener(Listener listener) {
        mListener = listener;
    }
    //
    private class deleteSale extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("subbc_id", id1);
                        put("user_id", preferences.getString("user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            deleteResponse = serviceAccess.SendHttpPost(Config.URL_DELETEUSERSUBCATEGORY, jsonLeadObj);
            Log.i("resp", "leadListResponse" + deleteResponse);


            if (deleteResponse.compareTo("") != 0) {
                if (isJSONValid(deleteResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(deleteResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(deleteResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    //Toast.makeText(context, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                // Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            }
            // Close the progressdialog
            // mProgressDialog.dismiss();
        }
    }

    //
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

    private String getLangCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void loadLanguage(Context context) {
        Locale locale = new Locale(getLangCode(context));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
