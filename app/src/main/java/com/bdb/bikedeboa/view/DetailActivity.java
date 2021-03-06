package com.bdb.bikedeboa.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.databinding.ActivityDetailBinding;
import com.bdb.bikedeboa.viewmodel.DetailViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bdb.bikedeboa.util.Constants.RACK_ID;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

	private static final String TAG = DetailActivity.class.getSimpleName();
	private int rackId;
	private ActivityDetailBinding binding;
	private DetailViewModel detailViewModel;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rackId = extras.getInt(RACK_ID);
			detailViewModel = new DetailViewModel(rackId, this);
			binding.setViewModel(detailViewModel);
		} else {
			// Something's not right, finish this activity
			this.finish();
		}

		// Set click listeners
		binding.howToGetThere.setOnClickListener(getMeThere);
		binding.rackPhoto.setOnClickListener(expandImage);

		// Fire up map lite
		binding.mapLite.onCreate(null);
		binding.mapLite.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		MapsInitializer.initialize(this);
		customizeMap(googleMap);
		detailViewModel.setUpMap(googleMap);
	}

	private void customizeMap(GoogleMap googleMap) {
		// Disable little map icons
		googleMap.getUiSettings().setMapToolbarEnabled(false);
		// Customise the styling of the base map using a JSON object defined in a raw resource file.
		try {
			boolean success = googleMap.setMapStyle(
					MapStyleOptions.loadRawResourceStyle(this, R.raw.styles_map));

			if (!success) {
				Log.e(TAG, "Style parsing failed.");
			}
		} catch (Resources.NotFoundException e) {
			Log.e(TAG, "Can't find style. Error: ", e);
		}
	}

	private View.OnClickListener getMeThere = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// <?q=%f,%f> necessary for pin positioning
			String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f",
					detailViewModel.getLatitude(), detailViewModel.getLongitude(),
					detailViewModel.getLatitude(), detailViewModel.getLongitude());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
		}
	};

	private View.OnClickListener expandImage = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(DetailActivity.this, ExpandImageActivity.class);
			intent.putExtra(RACK_ID, rackId);
			startActivity(intent);
		}
	};
}
