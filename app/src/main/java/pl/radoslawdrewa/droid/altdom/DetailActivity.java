package pl.radoslawdrewa.droid.altdom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import lear2crack.asynctask.library.JSONParser;


public class DetailActivity extends ActionBarActivity {

    final static String LITEAPI_URL = "http://webapi2.otodom.pl/lite/insertions";
    private Insertion insertion;

    private TextView txtObjectType;
    private TextView txtTitle;
    private TextView txtOwner;
    private TextView txtPrice;
    private TextView txtArea;
    private TextView txtPricePerMeter;
    private ImageView imgPhoto;
    private ListView lstParams;
    private Bitmap bitmap;
    private Button btnBack;
    private Dictionary dictionary;
    private DetailValue[] detailValues;
    private DetailValueAdapter detailValueAdapter;
    private TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        txtObjectType = (TextView) findViewById(R.id.txtObjectType);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtOwner = (TextView) findViewById(R.id.txtOwner);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtArea = (TextView) findViewById(R.id.txtArea);
        txtPricePerMeter = (TextView) findViewById(R.id.txtPricePerMeter);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        lstParams = (ListView) findViewById(R.id.lstParams);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        InputStream is = getResources().openRawResource(R.raw.dictionaries);
        dictionary = new Dictionary(is);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        new JSONParse().execute();
    }


    private class JSONParse extends AsyncTask<String, String, Insertion> {

        private String url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }
            String id = extras.getString("id");
            url = LITEAPI_URL + "/" + id;
            Log.d("RESOLVE", "url: " + url);
        }

        @Override
        protected Insertion doInBackground(String... strings) {
            JSONParser parser = new JSONParser();
            StringResource stringResource = new StringResource(getResources(), getPackageName());
            InsertionFiller insertionFiller = new InsertionFiller(dictionary, stringResource);
            JSONObject json = parser.getJSONFromUrl(url);
            Insertion insertion = null;
            try {
                insertion = insertionFiller.fill(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return insertion;
        }

        @Override
        protected void onPostExecute(Insertion insertion) {
            super.onPostExecute(insertion);
            txtObjectType.setText(insertion.getObjectType().toUpperCase());
            txtOwner.setText(insertion.getOwner().toUpperCase());
            txtTitle.setText(insertion.getTitle());
            txtPrice.setText(String.valueOf((int) insertion.getPrice()) + " " + insertion.getCurrency());
            txtArea.setText(String.valueOf(insertion.getArea()) + " m2");
            String pricePerMeter = String.valueOf((int) insertion.getPrice() / insertion.getArea());
            txtPricePerMeter.setText(pricePerMeter + " " + insertion.getCurrency() + "/m2");
            txtDescription.setText(insertion.getDescription());
            String photo = insertion.getPhoto();
            if (photo != null && photo.length()!= 0) {
                new ImageResolver().execute(photo);
            }
            HashMap<String, String> details = insertion.getDetails();
            int size = details.size();
            detailValues = new DetailValue[size];
            Iterator<String> iterator = details.keySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String detail = iterator.next();
                String value = details.get(detail);
                detailValues[i] = new DetailValue(detail, value);
                i++;
            }
            detailValueAdapter = new DetailValueAdapter(getApplicationContext(),
                    R.layout.row, detailValues);
            if (lstParams != null) {
                lstParams.setAdapter(detailValueAdapter);
            }
        }
    }

    private class ImageResolver extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String photoUrl = strings[0];
            try {
                InputStream is = (InputStream) new URL(photoUrl).getContent();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(bitmap);
            if (image != null) {
                imgPhoto.setImageBitmap(bitmap);
            }
        }
    }
}
