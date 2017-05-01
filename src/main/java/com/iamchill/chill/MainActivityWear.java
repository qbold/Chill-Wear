package com.iamchill.chill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivityWear extends WearableActivity //implements DataApi.DataListener,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener
{

    public static List<String> ad;
    private WearableListView listView;
    public static AppAdapter adp;

    public static String user_name, user_id;

    private static int position;

    public static Typeface font, font_bold;

    public static WearService service;

    public static MainActivityWear main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = this;
//        setAmbientEnabled();
        try {
            WearService.ini(getApplicationContext());
//        System.out.println("on create");
            setContentView(R.layout.activity_main);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });
//            WatchViewStub s = (WatchViewStub) findViewById(R.id.watch_view_stub);
//            System.out.println("WatchViewStub");
//            System.out.println(s == null);
            ad = Collections.synchronizedList(new ArrayList<String>());

            ImageView im = (ImageView) findViewById(R.id.loading);
            im.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            im.setImageResource(R.drawable.ic_loading_spinner);
            im.setVisibility(View.VISIBLE);

            listView = (WearableListView) findViewById(R.id.dialogs_list);
//            System.out.println(listView == null);
            adp = new AppAdapter(this);
            listView.setAdapter(adp);
            listView.setHasFixedSize(false);
            listView.setClickListener(new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    user_name = ad.get(position);
                    try {
                        user_id = WearService.users_list.get(user_name).getString("id_contact");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    startActivity(new Intent(MainActivityWear.this, SendActivityWear.class));
                }

                @Override
                public void onTopEmptyRegionClick() {

                }
            });
            listView.addOnCentralPositionChangedListener(new WearableListView.OnCentralPositionChangedListener() {
                @Override
                public void onCentralPositionChanged(int i) {
                    position = i;
                    setColors();
                }
            });

            if (font == null) {
                font = Typeface.createFromAsset(getAssets(), "helvetica.ttf");
            }

            if (font_bold == null) {
                font_bold = Typeface.createFromAsset(getAssets(), "helvetica_bold.ttf");
            }

//            if (service == null) {
            startService(new Intent(getApplicationContext(), WearService.class));
//            }
            WearService.restore();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void stopLoading() {
        if (WearService.icons.keySet().size() != WearService.available_icons.length()) {
//            viewError(WearService.icons.keySet().size() + " " + WearService.available_icons.length());
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView im = (ImageView) findViewById(R.id.loading);
                im.setAnimation(null);
                im.setVisibility(View.INVISIBLE);
                adp.notifyDataSetChanged();
//                System.out.println("invis");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        System.out.println("on resume");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (service == null) {
                        Thread.sleep(1000L);
                    }
                    service.refresh();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }).start();
        adp.notifyDataSetChanged();
//        service.startGoogle();
    }

    public void resultSendingMessage(boolean s) {
//        System.out.println("Send: " + s);
        Intent intent = new Intent(MainActivityWear.this, ConfirmationActivity.class);
        if (s) {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    getString(R.string.message_sent));
        } else {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    getString(R.string.message_not_sent));
        }
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        System.out.println("on pause");
//        try {
//            WearService.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
//            System.out.println("on destroy");
            WearService.close();
            stopService(new Intent(this, WearService.class));

//            available_icons = null;
//            sections = null;
//            favorite_icons = null;
//            favorite_ids = null;
//            icons = null;
//            ad.clear();
//            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            viewError(e.getMessage());
        }
    }

//    @Override
//    public void onConnected(Bundle bundle) {
////        System.out.println("on connected");
//        try {
////            System.out.println("sending");
////            if (ad.isEmpty())
////                sendData(USERS_KEY);
//////            else System.out.println("The size of ad is " + ad.size());
////            if (available_icons == null || available_icons.length() == 0)
////                sendData(ICONS_KEY);
////            else System.out.println("We already have " + available_icons.length() + " icons");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void setColors() {
        for (int j = 0; j < listView.getChildCount(); j++) {
            View v = listView.getChildAt(j);
            if (position != listView.getChildAdapterPosition(v)) {
                ((TextView) v.findViewById(R.id.txt)).setTextColor(0xff979797);
            } else {
                ((TextView) v.findViewById(R.id.txt)).setTextColor(0xff000000);
            }
        }
    }

//    @Override
//    public void onConnectionSuspended(int i) {
////        System.out.println("connection suspended");
//    }

    public void refreshUsersList(String s) {
        try {
//            System.out.println(s);
            ConcurrentHashMap<String, JSONObject> lst = new ConcurrentHashMap<>();
            JSONObject ob = new JSONObject(s);
            JSONArray ar = ob.getJSONArray("users");
            final int sz = ad.size();
            ad.clear();
//            System.out.println("USERS");
//            viewError("Users length: " + ar.length());
            for (int i = 0; i < ar.length(); i++) {
                JSONObject obh = ar.getJSONObject(i);
                String login = obh.getString("login");
                lst.put(login, obh);
                ad.add(login);
//                System.out.println(login);
            }
//            System.out.println("END USERS");
            WearService.users_list = lst;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ad.size() < sz && position >= ad.size()) {
                        listView.scrollToPosition(ad.size() - 1);
                    }
                    adp.notifyDataSetChanged();
//                    listView.refreshDrawableState();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            viewError(e.getMessage());
        }
    }

    public void viewError(final String err) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//    }

    class AppAdapter extends WearableListView.Adapter {

        //        private final Context mContext;
        private final LayoutInflater mInflater;

        public AppAdapter(Context context) {
//            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        public class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.txt);
                textView.setTypeface(font);
            }
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            return new ItemViewHolder(mInflater.inflate(R.layout.button_layout, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            try {
                ItemViewHolder itemHolder = (ItemViewHolder) holder;
                TextView view = itemHolder.textView;
                view.setText(ad.get(position));
                holder.itemView.setTag(position);

                if (MainActivityWear.position != position) {
                    view.setTextColor(0xff979797);
                } else {
                    view.setTextColor(0xff000000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                viewError(e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            if (ad == null || WearService.available_icons.length() == 0 /*|| WearService.icons.keySet().size() != WearService.available_icons.length()|| available_icons == null || icons == null || sections == null*/)
                return 0;
//            System.out.println("Size: " + ad.size());
            return ad.size();
        }
    }
}
