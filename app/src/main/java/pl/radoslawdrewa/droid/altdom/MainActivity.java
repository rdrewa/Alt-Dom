package pl.radoslawdrewa.droid.altdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import lear2crack.asynctask.library.JSONParser;


public class MainActivity extends ActionBarActivity {

    final static String LITEAPI_URL = "http://webapi2.otodom.pl/lite/insertions";
    private static final int REQUEST_CODE_PREFERENCES = 1;
    private static final int RESULTS_PER_PAGE = 20;
    private MapView mapView;
    private MapController mapController;
    private Criteria criteria;
    private LocationManager locationManager;
    private Spinner spinObjectName;
    private Spinner spinOfferType;
    private String bestProvider;
    private Button btnSearch;
    private EditText edtPriceFrom;
    private EditText edtPriceTo;
    private EditText edtAreaFrom;
    private EditText edtAreaTo;
    private ResourceProxy resourceProxy;
    private ItemizedOverlayWithFocus<OverlayItem> myLocationOverlay;
    private double countryLat = 52.025459;
    private double countryLng = 19.204102;
    private int countryZoom = 6;
    private Dictionary dictionary;
    private ScaleBarOverlay scaleBarOverlay;
    private SharedPreferences settings;
    private SearchQuery prevSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resourceProxy = new ResourceProxyImpl(getApplicationContext());
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        spinObjectName = (Spinner) findViewById(R.id.spinObjectName);
        spinOfferType = (Spinner) findViewById(R.id.spinOfferType);
        edtPriceFrom = (EditText) findViewById(R.id.edtPriceFrom);
        edtPriceTo = (EditText) findViewById(R.id.edtPriceTo);
        edtAreaFrom = (EditText) findViewById(R.id.edtAreaFrom);
        edtAreaTo = (EditText) findViewById(R.id.edtAreaTo);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONParse().execute();
            }
        });

        InputStream is = getResources().openRawResource(R.raw.dictionaries);
        dictionary = new Dictionary(is);
        prevSearch = new SearchQuery(RESULTS_PER_PAGE);
        initMap();
    }

    private void refreshControls() {
        boolean prefScaleActive = settings.getBoolean("prefScaleActivity", true);
        boolean prefZoomActive = settings.getBoolean("prefZoomActivity", true);
        mapView.setBuiltInZoomControls(prefZoomActive);
        scaleBarOverlay.setEnabled(prefScaleActive);
        mapView.invalidate();
    }

    private SearchQuery prepareSearchQuery() {
        SearchQuery searchQuery = new SearchQuery(RESULTS_PER_PAGE);
        searchQuery.setObjectName(spinObjectName.getSelectedItemPosition());
        searchQuery.setOfferType(spinOfferType.getSelectedItemPosition());

        if (edtPriceFrom.length() != 0) {
            searchQuery.setPriceFrom(Integer.parseInt(edtPriceFrom.getText().toString()));
        }
        if (edtPriceTo.length() != 0) {
            searchQuery.setPriceTo(Integer.parseInt(edtPriceTo.getText().toString()));
        }
        if (edtAreaFrom.length() != 0) {
            searchQuery.setAreaFrom(Integer.parseInt(edtAreaFrom.getText().toString()));
        }
        if (edtAreaTo.length() != 0) {
            searchQuery.setAreaTo(Integer.parseInt(edtAreaTo.getText().toString()));
        }
        GeoPoint mapCenter = (GeoPoint) mapView.getMapCenter();

        float centerLatitude = (float)(mapCenter.getLatitudeE6())/1000000;
        float centerLongitude = (float)(mapCenter.getLongitudeE6())/451000000;

        GeoPoint mapTopLeft = (GeoPoint) mapView.getProjection().fromPixels(0, 0);
        float latTo = (float)(mapTopLeft.getLatitudeE6())/1000000;
        float lngFrom = (float)(mapTopLeft.getLongitudeE6())/1000000;

        GeoPoint mapBottomRight
                = (GeoPoint) mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
        float latFrom = (float)(mapBottomRight.getLatitudeE6())/1000000;
        float lngTo = (float)(mapBottomRight.getLongitudeE6())/1000000;

        int zoomLevel = mapView.getZoomLevel();

        searchQuery.setLatTo(latTo);
        searchQuery.setLngFrom(lngFrom);
        searchQuery.setLatFrom(latFrom);
        searchQuery.setLngTo(lngTo);

        if (searchQuery.equals(prevSearch)) {
            searchQuery.setPage(prevSearch.getPage() + 1);
        } else {
            searchQuery.setPage(1);
            mapView.getOverlays().clear();
        }
        prevSearch.fill(searchQuery);

        String info =
                "Center: " + centerLatitude + "/" + centerLongitude + "\n"
                        + "Top Left: " + latTo + "/" + lngFrom + "\n"
                        + "Bottom Right: " + latFrom + "/" + lngTo + "\n"
                        + "zoomLevel: " + zoomLevel;

        Toast.makeText(MainActivity.this, info, Toast.LENGTH_LONG);
        Log.d("SQ", info);
        Log.d("SQ:B", searchQuery.prepareParams());

        return searchQuery;
    }

    private void initMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setClickable(true);
        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(this, mapView);
        mapView.getOverlays().add(locationNewOverlay);
        mapController = (MapController) mapView.getController();
        scaleBarOverlay = new ScaleBarOverlay(this, resourceProxy);
        mapView.getOverlays().add(scaleBarOverlay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        scaleBarOverlay.setScaleBarOffset((int) (getResources().getDisplayMetrics().widthPixels / 2 - getResources()
                .getDisplayMetrics().xdpi / 2), 10);
        concentrateAndRefresh(countryLat, countryLng, countryZoom);
    }

    private void concentrateAndRefresh(double lat, double lng, int zoom) {
        mapController.setZoom(zoom);
        GeoPoint point = new GeoPoint(lat, lng);
        mapController.setCenter(point);
        mapView.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_clear:
                mapView.getOverlays().clear();
                mapView.invalidate();
                break;
            case R.id.action_big_city_poznan:
                concentrateAndRefresh(52.4063740, 16.9251681, 11);
                break;
            case R.id.action_big_city_warszawa:
                concentrateAndRefresh(52.2296756, 21.0122287, 11);
                break;
            case R.id.action_big_city_gdansk:
                concentrateAndRefresh(54.3520252, 18.6466384, 11);
                break;
            case R.id.action_big_city_gdynia:
                concentrateAndRefresh(54.5188898, 18.5305409, 11);
                break;
            case R.id.action_big_city_szczecin:
                concentrateAndRefresh(53.4285438, 14.5528116, 11);
                break;
            case R.id.action_big_city_katowice:
                concentrateAndRefresh(50.2648919, 19.0237815, 11);
                break;
            case R.id.action_big_city_bydgoszcz:
                concentrateAndRefresh(53.1234804, 18.0084378, 11);
                break;
            case R.id.action_big_city_lublin:
                concentrateAndRefresh(51.2464536, 22.5684463, 11);
                break;
            case R.id.action_big_city_jelenia_gora:
                concentrateAndRefresh(50.9044171, 15.7193616, 11);
                break;
            case R.id.action_country:
                concentrateAndRefresh(countryLat, countryLng, countryZoom);
                break;
            case R.id.action_gps:
                criteria = new Criteria();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                bestProvider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location instanceof Location && location != null) {
                    concentrateAndRefresh(location.getLatitude(), location.getLongitude(), 15);
                } else {
                    String msg = "Nie udało się ustalić lokalizacji z GPS";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT);
                }
                break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, REQUEST_CODE_PREFERENCES);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREFERENCES) {
            refreshControls();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshControls();
    }

    private class JSONParse extends AsyncTask<String, String, ArrayList<Insertion>> {

        private String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SearchQuery searchQuery = prepareSearchQuery();
            prevSearch.fill(searchQuery);

            String params = searchQuery.prepareParams();
            try {
                url = LITEAPI_URL + "?q=" + URLEncoder.encode(params, "UTF-8");
                Log.d("URL", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<Insertion> doInBackground(String... strings) {
            JSONParser parser = new JSONParser();
            StringResource stringResource = new StringResource(getResources(), getPackageName());
            InsertionFiller insertionFiller = new InsertionFiller(dictionary, stringResource);
            JSONObject json = parser.getJSONFromUrl(url);
            ArrayList<Insertion> insertions = new ArrayList<Insertion>();
            try {
                JSONArray jsonItems = json.getJSONArray("Results");
                for (int i=0; i<jsonItems.length(); i++) {
                    Insertion insertion = insertionFiller.fill(jsonItems.getJSONObject(i));
                    insertions.add(insertion);
                }
            } catch (JSONException e) {

            }
            Log.d("JSON", "Found " + insertions.size());
            return insertions;
        }

        @Override
        protected void onPostExecute(ArrayList<Insertion> insertions) {
            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            String title, snippet;
            GeoPoint point;
            for (Insertion insertion : insertions) {
                title = "Ogłoszenie: " + insertion.getId();
                if (insertion.getTitle() != null && insertion.getTitle().length() != 0) {
                    snippet = insertion.getTitle();
                } else {
                    snippet = "Cena: " + insertion.getPrice() + ", powierzchnia: "
                            + insertion.getArea() + " m2";
                }
                point = new GeoPoint(insertion.getLat(), insertion.getLng());
                items.add(new OverlayItem(title, snippet, point));
            }

            ItemizedIconOverlay<OverlayItem> itemizedIconOverlay =
                    new ItemizedIconOverlay<OverlayItem>(MainActivity.this, items, null);
            mapView.getOverlays().add(itemizedIconOverlay);

            myLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Toast.makeText(
                                MainActivity.this,
                                item.getTitle() + "\n" + item.getSnippet(), Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        String matches[] = item.getTitle().split(" ");
                        String id = matches[1];
                        Intent i = new Intent(MainActivity.this.getApplicationContext(), DetailActivity.class);
                        i.putExtra("id", id);
                        startActivityForResult(i, 1);
                        return false;
                    }
                },
            resourceProxy);

            mapView.getOverlays().add(myLocationOverlay);
            mapView.invalidate();
        }
    }
}
