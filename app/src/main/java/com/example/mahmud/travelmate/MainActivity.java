package com.example.mahmud.travelmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.AddEventListener;
import com.example.mahmud.travelmate.Interface.AddEventOption;
import com.example.mahmud.travelmate.Interface.AddMainGallery;
import com.example.mahmud.travelmate.Interface.BackFromTakePhotoFragment;
import com.example.mahmud.travelmate.Interface.BackToEventFromAEF;
import com.example.mahmud.travelmate.Interface.BackToEventFromAFListener;
import com.example.mahmud.travelmate.Interface.BackToMainGalleryFromPictureEvent;
import com.example.mahmud.travelmate.Interface.CheckPermission;
import com.example.mahmud.travelmate.Interface.EventAddedAction;
import com.example.mahmud.travelmate.Interface.FullScreenImageListener;
import com.example.mahmud.travelmate.Interface.HandlingBackInEventListener;
import com.example.mahmud.travelmate.Interface.ImageFunctionListener;
import com.example.mahmud.travelmate.Interface.ListItemClickListener;
import com.example.mahmud.travelmate.Interface.LoginAction;
import com.example.mahmud.travelmate.Interface.MenuEditOperation;
import com.example.mahmud.travelmate.Interface.RegToLoginListener;
import com.example.mahmud.travelmate.Interface.SignUpAction;
import com.example.mahmud.travelmate.POJO.Event;
import com.example.mahmud.travelmate.POJO.SearchSuggestionProvider;
import com.example.mahmud.travelmate.POJO.UserWithImg;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddEventOption,
        EventAddedAction ,MenuEditOperation, LoginAction ,SignUpAction, ListItemClickListener,
        ImageFunctionListener,FullScreenImageListener ,RegToLoginListener ,
        HandlingBackInEventListener,BackToEventFromAEF, AddEventListener, BackToEventFromAFListener,
        AddMainGallery , BackToMainGalleryFromPictureEvent,
        CheckPermission ,BackFromTakePhotoFragment {
    private static final int REQUEST_STORAGE_WRITE_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FragmentManager fragmentManager;
//-----

    //----private FloatingActionButton fab;
    private Toolbar toolbarEmpty;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SearchView mSearchView;
    private FusedLocationProviderClient mClient;
    private double mLatitude;
    private double mLongitude;
    private Location mLocation;
    private Geocoder mGeocoder;
    private String mCity;
    private List<Address> mAddresses = new ArrayList<>();
    private List<Address> mSearchAddresses = new ArrayList<>();
    private String mAddressLine;
    private TextView mNavUserNameTV;
    private TextView mNaveUserEmailTV;
    private DatabaseReference mUserRef;
    private String mUserName;
    private String mUserPhone;
    private String mUserImageUrl;
    private CircleImageView mUserImageIMG;
    private File mAppDir;
    private String mUserImageName;


    //private AccountFragment accountFragment1;
    //private FloatingActionButton fabMapSearch;
    //private FloatingActionButton fabAddExpense;
    //private FloatingActionButton fabWeatherSearch;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocPermission();
        checkWriteStoragePermission();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarEmpty = (Toolbar) findViewById(R.id.toolbar_blank);
        mGeocoder = new Geocoder(this);
        mAppDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //fabMapSearch = findViewById(R.id.fab_search);
        //fabWeatherSearch = findViewById(R.id.fab_weather_search);
        //fabAddExpense = findViewById(R.id.fab_add_expense);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Events");
        //toolbar.setTitle("Events");

        mClient = LocationServices.getFusedLocationProviderClient(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        mNavUserNameTV = headerView.findViewById(R.id.nav_user_name_tv);
        mNaveUserEmailTV = headerView.findViewById(R.id.nav_user_email_tv);
        mUserImageIMG = headerView.findViewById(R.id.user_nav_img);
        navigationView.setNavigationItemSelectedListener(this);


//---------
        /*fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                AddEventFragment addEventFragment = new AddEventFragment();
                ft.replace(R.id.fragment_container_main,addEventFragment);
                fab.setVisibility(View.GONE);
                //----------------------------------------------------------------------------changed lasttime
                //ft.addToBackStack(null);
                ft.commit();

            }
        });*/

        //-----------------Working
        /*fabMapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainActivity.this);
                    startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });*/
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fragmentManager = getSupportFragmentManager();



        if (mUser != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            EventFragment eventFragment = new EventFragment();
            ft.add(R.id.fragment_container_main,eventFragment);
            toolbarEmpty.setVisibility(View.GONE);
            whenUserUpdated();

            if (checkPermission()){
                mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            Toast.makeText(MainActivity.this, "Location Got", Toast.LENGTH_SHORT).show();
                            mLocation = location;
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                            try {
                                mAddresses = mGeocoder.getFromLocation(mLatitude,mLongitude,1);
                                mAddressLine = mAddresses.get(0).getAddressLine(0);
                                mCity = mAddresses.get(0).getLocality();
                                if (mCity != null){
                                    Toast.makeText(MainActivity.this, mCity, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });
            }
            //ft.addToBackStack(null);
            ft.commit();
        } else {
            loadLoginPage();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Place place = PlaceAutocomplete.getPlace(this,data);
                Toast.makeText(this, place.getName().toString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("-------", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search_mn).getActionView();
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(MainActivity.this,SearchSuggestionProvider.AUTHORITY,SearchSuggestionProvider.MODE);
                suggestions.saveRecentQuery(s,null);
                mSearchView.setQuery("",false);

                try {
                    mSearchAddresses = mGeocoder.getFromLocationName(s,1);
                    if (mSearchAddresses.size()>0) {
                        if (mSearchAddresses.get(0).hasLatitude() && mSearchAddresses.get(0).hasLongitude()){
                            Bundle bundle = new Bundle();
                            bundle.putDouble("latitude",mSearchAddresses.get(0).getLatitude());
                            bundle.putDouble("longitude",mSearchAddresses.get(0).getLongitude());
                            Toast.makeText(MainActivity.this, "latitude is "
                                    +mSearchAddresses.get(0).getLatitude()+" Longitude is "
                                    +mSearchAddresses.get(0).getLongitude(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Bundle bundle = new Bundle();
                bundle.putString("city",s);
                WeatherFragment weatherFragment = new WeatherFragment();
                weatherFragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,weatherFragment);
                ft.commit();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(i);
                String query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                mSearchView.setQuery(query,false);

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.sign_out_mn){
            mAuth.signOut();
            loadLoginPage();
            /*Intent intent = new Intent(this,AccessActivity.class);
            startActivity(intent);*/
        }
        if (id == R.id.account_mn){
            if (mUserName !=null && mUserPhone !=null){
                Bundle bundle = new Bundle();
                UserWithImg userWithImg = new UserWithImg(mUserName,mUserPhone,mUserImageName,mUserImageUrl);
                bundle.putSerializable("userwithimg",userWithImg);
                AccountFragment accountFragment = new AccountFragment();
                accountFragment.setArguments(bundle);
                //Toast.makeText(this, mUserName+mUserPhone, Toast.LENGTH_SHORT).show();


            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_event) {
            FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new EventFragment());
            getSupportActionBar().show();
            ft.commit();
        } else if (id == R.id.nav_gallery) {
            getSupportActionBar().show();
            openMainGallery();
        }

        else if (id == R.id.nav_settings) {
            getSupportActionBar().show();
            if (mUserName !=null && mUserPhone !=null){
                Bundle bundle = new Bundle();
                UserWithImg userWithImg = new UserWithImg(mUserName,mUserPhone,mUserImageName,mUserImageUrl);
                bundle.putSerializable("userwithimg",userWithImg);
                AccountFragment accountFragment = new AccountFragment();
                accountFragment.setArguments(bundle);
                //Toast.makeText(this, mUserName+mUserPhone, Toast.LENGTH_SHORT).show();

                AccountFragment accountFragment1 = (AccountFragment) fragmentManager.findFragmentByTag("account");

                String s = "Found fragment: \n";
                for(int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++){
                    s = s+fragmentManager.getBackStackEntryAt(entry).getId()+"\n";
                }
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();


                if (accountFragment1 == null){
                    Toast.makeText(this, "AF is null", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,accountFragment,"account");

                    /*if (accountFragment1 == null){
                        ft.addToBackStack(null);
                    }*/
                    //accountFragment1 = null;
                    ft.commit();

                } else {
                    Toast.makeText(this, "Fragment exists", Toast.LENGTH_SHORT).show();
                }
            }
        }

        else if (id == R.id.nav_nearby){
            getSupportActionBar().show();
                if (mLocation != null){

                    Bundle bundle = new Bundle();
                    mLatitude = mLocation.getLatitude();
                    mLongitude = mLocation.getLongitude();
                    bundle.putDouble("Latitude",mLatitude);
                    bundle.putDouble("Longitude",mLongitude);


                }
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @SuppressLint("RestrictedApi")
    void loadLoginPage(){
        FragmentTransaction ft2 = fragmentManager.beginTransaction();
        LoginFragment loginFragment =  new LoginFragment();
        ft2.replace(R.id.fragment_container_main,loginFragment);
        toolbar.setVisibility(View.GONE);
        toolbarEmpty.setTitle("Login");
        toolbarEmpty.setVisibility(View.VISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //fab.setVisibility(View.GONE);
        ft2.commit();
    }


    //Interface option
    @SuppressLint("RestrictedApi")
    @Override
    public void onAddEvent() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        AddEventFragment addEventFragment = new AddEventFragment();
        //fab.setVisibility(View.GONE);
        ft.replace(R.id.fragment_container_main,addEventFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onEventAdded() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        EventFragment eventFragment = new EventFragment();
        ft.replace(R.id.fragment_container_main,eventFragment);

        //fab.setVisibility(View.VISIBLE);
        //fabWeatherSearch.setVisibility(View.GONE);
        //fabMapSearch.setVisibility(View.GONE);
        //fabAddExpense.setVisibility(View.GONE);

        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onEditOperation(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event",event);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        AddEventFragment addEventFragment = new AddEventFragment();
        addEventFragment.setArguments(bundle);
        //fab.setVisibility(View.GONE);
        ft.replace(R.id.fragment_container_main,addEventFragment);
        ft.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onLoginSuccessfull() {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            EventFragment eventFragment = new EventFragment();
            ft.replace(R.id.fragment_container_main,eventFragment);
            whenUserUpdated();

            toolbarEmpty.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Events");
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ft.commit();
        
    }

    @Override
    public void signUpOperation() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        ft.replace(R.id.fragment_container_main,registerFragment);
        toolbarEmpty.setTitle("Sign Up");
        //ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onListItemClickListener(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event",event);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        EventFunctionFragment functionFragment = new EventFunctionFragment();
        functionFragment.setArguments(bundle);

        //fab.setVisibility(View.GONE);
        /*fabAddExpense.setVisibility(View.GONE);
        fabMapSearch.setVisibility(View.GONE);
        fabWeatherSearch.setVisibility(View.GONE);*/

        ft.replace(R.id.fragment_container_main,functionFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onTakeSnapshot(String eventId) {
        if (checkStoragePermission()){
            Bundle bundle = new Bundle();
            bundle.putString("Event Id",eventId);
            TakePhotoFragment takePhotoFragment = new TakePhotoFragment();
            takePhotoFragment.setArguments(bundle);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container_main,takePhotoFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onGalleryView(String eventId) {
        Bundle bundle = new Bundle();
        bundle.putString("Event Id",eventId);
        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container_main,galleryFragment);
        ft.addToBackStack(null);
        ft.commit();
    }



    private boolean checkStoragePermission(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_WRITE_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onFullScreenImageView(String imagePath) {
        Bundle bundle = new Bundle();
        bundle.putString("Image Path",imagePath);
        PictureFragment pictureFragment = new PictureFragment();
        pictureFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container_main,pictureFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onFullScreenImageViewFromMG(String imagepath) {
        Bundle bundle = new Bundle();
        bundle.putString("Image Path",imagepath);
        bundle.putInt("code",101);
        PictureFragment pictureFragment = new PictureFragment();
        pictureFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container_main,pictureFragment);
        ft.commit();
    }





    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void backToLogin() {
        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new LoginFragment());
        toolbarEmpty.setTitle("Login");
        ft.commit();
    }

    @Override
    public boolean eventInBackListener() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        new AlertDialog.Builder(this).setTitle("Sure to exit?")
                .setNegativeButton("No",null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }).show();
        return false;

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void backAction() {
        fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new EventFragment()).commit();
        /*fabAddExpense.setVisibility(View.GONE);
        fabMapSearch.setVisibility(View.GONE);
        fabWeatherSearch.setVisibility(View.GONE);*/
        //fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickAdd() {
        Bundle bundle = new Bundle();
        bundle.putString("addressline",mAddressLine);
        //bundle.putString("city",mCity);
        AddEventFragment addEventFragment = new AddEventFragment();
        addEventFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,addEventFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onUpdateInfo() {
        whenUserUpdated();
        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new EventFragment(),"event");
        fragmentManager.popBackStack();
        ft.commit();
    }

    private void whenUserUpdated(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mNaveUserEmailTV.setText(mUser.getEmail());
        Toast.makeText(this, "Calling datasnapshot", Toast.LENGTH_SHORT).show();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        //mUserRef.addValueEventListener(new ValueEventListener() {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> userInfo = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){

                    userInfo.add((String) d.getValue());
                }
                Toast.makeText(MainActivity.this, "Getting data from firebase", Toast.LENGTH_SHORT).show();
                mUserImageName = userInfo.get(0);
                mUserImageUrl = userInfo.get(1);
                mUserPhone = userInfo.get(2);
                mUserName = userInfo.get(3);
                if (mUserImageUrl.length() > 3){
                //if (URLUtil.isValidUrl(mUserImageUrl)){
                    final File file = new File(mAppDir,mUserImageName);
                    if (!file.exists()){
                        Toast.makeText(MainActivity.this, "Image File not exists", Toast.LENGTH_SHORT).show();
                        //Picasso.get().load(mUserImageUrl).into(mUserImageIMG);
                        //Picasso.get().load(mUserImageUrl).into(getTarget(mUser.getEmail()+".jpg"));
                        Picasso.get().load(mUserImageUrl).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    FileOutputStream outputStream = new FileOutputStream(file);
                                    mUserImageIMG.setImageBitmap(bitmap);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Loading Bitmap", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        mUserImageIMG.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Length is shorter than 3", Toast.LENGTH_SHORT).show();
                    mUserImageIMG.setImageResource(R.drawable.ic_account_circle);
                }
                mNavUserNameTV.setText(userInfo.get(3));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void openMainGallery() {
        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new MainGalleryFragment());

        ft.commit();
    }

    @Override
    public void onBackPressedFromPictureEvent() {
        Log.e("------------------","onBackPressed called");
        MainGalleryFragment mainGalleryFragment = new MainGalleryFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.fragment_container_main,mainGalleryFragment);
        ft.commit();
    }




    private boolean checkLocPermission(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);
            return false;
        }
        return true;
    }
    
    private boolean checkWriteStoragePermission(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1002);
            return false;
        }
        return true;
    }

    @Override
    public boolean isPermissionGiven() {
        if (checkLocPermission() && checkWriteStoragePermission()){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            fragmentManager.beginTransaction().replace(R.id.fragment_container_main,new TakePhotoFragment()).commit();
            Toast.makeText(this, "Re Run the App", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 1001 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPermission()){
                mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            Toast.makeText(MainActivity.this, "Location Got", Toast.LENGTH_SHORT).show();
                            mLocation = location;
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                            try {
                                mAddresses = mGeocoder.getFromLocation(mLatitude,mLongitude,1);
                                mAddressLine = mAddresses.get(0).getAddressLine(0);
                                mCity = mAddresses.get(0).getLocality();
                                if (mCity != null){
                                    Toast.makeText(MainActivity.this, mCity, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });
            }
        }
    }

    @Override
    public void backFromTakePhotoListener() {
        super.onBackPressed();
    }


}
