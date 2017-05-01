package com.iamchill.chill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Base64;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Qbold on 12.02.2016.
 */
public class WearService extends WearableListenerService {

    private static final String KEY = "com.iamchill.chill.key.";
    private static final String USERS_KEY = "users", ICONS_KEY = "icons", MESSAGE_KEY = "message";
    public static int cnt = 1000;
    public static ConcurrentHashMap<String, String> temp_data;
    private static GoogleApiClient google;
//    public static boolean added;

    static {
        temp_data = new ConcurrentHashMap<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MainActivityWear.service = this;
            startGoogle();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void close() {
        try {
//        if (added) {
            Wearable.DataApi.removeListener(google, MainActivityWear.service);
//            added = false;
//        }
            google.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void restore() {
        try {
            String json_users = WearService.restoreData("users");
            if (!json_users.isEmpty()) {
                if (MainActivityWear.main != null)
                    MainActivityWear.main.refreshUsersList(json_users);
            }
            String json_icons = WearService.restoreData("icons");
            if (!json_icons.isEmpty()) {
                refreshIcons(json_icons);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void refresh() {
//        System.out.println("refresh");
        sendData(USERS_KEY);
        sendData(ICONS_KEY);
    }

    public void startGoogle() {
        try {
            if (google == null) {
                google = new GoogleApiClient.Builder(this)
                        .addApi(Wearable.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
                        .build();
//            try {
//                google.connect();
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivityWear.main.viewError(e.getMessage());
//            }
            }
            if (!google.isConnected())
                google.connect();
//        if (!added) {
            Wearable.DataApi.addListener(google, this);
//            added = true;
//        }
            refresh();
//        MainActivityWear.main.viewError("service started");
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    static int step_users = -1, step_icons = -1;

    @Override
    public void onDataChanged(final DataEventBuffer dataEventBuffer) {
//        System.out.println("on changed");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        try {
//            synchronized (WearService.this) {
            for (DataEvent event : dataEventBuffer) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem item = event.getDataItem();
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
//                        System.out.println("path: " + item.getUri().getPath());
                    if (item.getUri().getPath().compareTo("/" + USERS_KEY) == 0) {
//                        System.out.println("users");
                        try {
                            int si = getStep(map, USERS_KEY);
//                            System.out.println("step users: " + si);
                            if (si == step_users + 1) {
                                step_users = si;
                                final boolean end = isEnd(map, USERS_KEY);
                                if (!map.getBoolean(KEY + USERS_KEY + ".int")) {
//                            System.out.println("without int users");
                                    final String obj = getJSONString(map, USERS_KEY);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (temp_data.containsKey(USERS_KEY)) {
//                                System.out.println("add users " + obj);
                                                    temp_data.replace(USERS_KEY, temp_data.get(USERS_KEY) + obj);
                                                } else {
//                                System.out.println("put users " + obj);
                                                    temp_data.put(USERS_KEY, obj);
                                                }
                                                if (end) {
//                                System.out.println("end users");
                                                    if (MainActivityWear.main != null) {
                                                        MainActivityWear.main.refreshUsersList(temp_data.get(USERS_KEY));
                                                        MainActivityWear.main.stopLoading();
                                                    }
                                                    WearService.storeData("users", temp_data.get(USERS_KEY));
                                                    temp_data.remove(USERS_KEY);
                                                    step_users = -1;
                                                }
                                            } catch (Throwable e) {
                                                Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    }).start();
                                }
                            }
                        } catch (Throwable e) {
                            step_users = -1;
                            e.printStackTrace();
                            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        }
                    } else if (item.getUri().getPath().compareTo("/" + ICONS_KEY) == 0) {
//                        System.out.println("icons");
                        try {
                            int si = getStep(map, ICONS_KEY);
                            if (si == step_icons + 1) {
                                step_icons = si;
                                if (!map.getBoolean(KEY + ICONS_KEY + ".int")) {
//                            System.out.println("without int icons");
                                    final String obj = getJSONString(map, ICONS_KEY);
                                    final boolean end = isEnd(map, ICONS_KEY);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (temp_data.containsKey(ICONS_KEY)) {
                                                    temp_data.replace(ICONS_KEY, temp_data.get(ICONS_KEY) + obj);
//                                        System.out.println("add icons " + temp_data.get(ICONS_KEY).length() + " " + end + " " + si);
//                                    System.out.println(temp_data.get(ICONS_KEY).length());
//                                        MainActivityWear.main.viewError("add icons " + temp_data.get(ICONS_KEY).length());
                                                } else {
                                                    temp_data.put(ICONS_KEY, obj);
//                                        System.out.println("put icons " + temp_data.get(ICONS_KEY).length() + " " + end + " " + si);
//                                        MainActivityWear.main.viewError("put icons " + temp_data.get(ICONS_KEY).length());
                                                }
                                                if (end) {
//                                    System.out.println("end icons " + temp_data.get(ICONS_KEY).length());
//                                        MainActivityWear.main.viewError("end icons " + temp_data.get(ICONS_KEY).length());
//                                        System.out.println("refreshIcons length " + temp_data.get(ICONS_KEY).length());
                                                    refreshIcons(temp_data.get(ICONS_KEY));
                                                    temp_data.remove(ICONS_KEY);
                                                    step_icons = -1;
                                                }
                                            } catch (Throwable e) {
                                                Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    }).start();
                                }
                            }
                        } catch (Throwable e) {
                            step_icons = -1;
                            e.printStackTrace();
                            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
//                        MainActivityWear.main.viewError(e.getMessage());
                        }
                    } else if (item.getUri().getPath().compareTo("/" + MESSAGE_KEY) == 0) {
//                        System.out.println("message");
                        try {
                            if (!map.getBoolean(KEY + MESSAGE_KEY + ".int")) {
//                                    System.out.println("without int msg");
                                MainActivityWear.main.resultSendingMessage(map.getBoolean(KEY + MESSAGE_KEY + ".res"));
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        }
                    } else if (item.getUri().getPath().startsWith("/" + ICONS_KEY)) {
                        try {
                            String wq = item.getUri().getPath().substring(1);
                            if (!map.getBoolean(KEY + wq + ".int")) {
                                final String name = map.getString(KEY + wq + ".name");
                                final String data = map.getString(KEY + wq + "." + name);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            WearService.storeData("icon_" + name, data);
//                                System.out.println("icon " + name + " " + data);
                                            WearService.icons.put(name, new Bitmap[]{getBitmapString(data)});
                                            if (SendActivityWear.send != null) {
                                                SendActivityWear.send.refresh();
                                            }
                                        } catch (Throwable e) {
                                            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                        }
                                    }
                                }).start();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                    dataEventBuffer.release();
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                }
            }
//            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
//            }
//        }).start();
    }

    public static void sendMessage(final String type, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("content", content);
                    obj.put("type", type);
                    obj.put("time", System.currentTimeMillis() + "");
                    obj.put("id_user", MainActivityWear.main.user_id);

//            System.out.println("Send message: " + content + " " + type + " " + obj.toString());

                    sendData(MESSAGE_KEY, obj.toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }).start();
    }


    public static Bitmap getBitmapString(String s) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(Base64.decode(s, Base64.DEFAULT));
            return BitmapFactory.decodeStream(bais);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        } finally {
            try {
                if (bais != null)
                    bais.close();
            } catch (Throwable e) {
                e.printStackTrace();
                Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            }
        }
        return null;
    }

    // Send the request to the handheld in order to get some data
    public static void sendData(String key) {
//        System.out.println("sendData " + key);
        try {
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/get_" + key);
            putDataMapReq.getDataMap().putInt(KEY + "get_" + key, cnt++);
            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".int", true);
            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".end", true);
            putDataMapReq.getDataMap().putLong(KEY + "get_" + key + ".time", System.currentTimeMillis());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(google, putDataReq);
//            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                @Override
//                public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
////                    System.out.println(dataItemResult.getDataItem().getUri());
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            List<Node> connectedNodes =
//                                    Wearable.NodeApi.getConnectedNodes(google).await().getNodes();
//                            System.out.println("Size: " + connectedNodes.size());
//                            for (Node n : connectedNodes) {
//                                System.out.println(n.getDisplayName());
//                            }
//                            Wearable.DataApi.addListener(google, MainActivityWear.this);
//                        }
//                    }).start();
//                }
//            });
//            System.out.println("sendData " + key + " " + google.isConnected());
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

//    private static AtomicBoolean wt = new AtomicBoolean();

    // Send the data to the handheld
    public static void sendData(String key, String data2) {
//        System.out.println("sendData " + key + " " + data2);
//        try {
//            String add = null;
//            if (data2.length() > 90 * 1024) {
//                add = data2.substring(90 * 1024);
//                data2 = data2.substring(0, 90 * 1024);
//            }
////                wt.set(true);
//            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/get_" + key);
//            putDataMapReq.getDataMap().putLong(KEY + "get_" + key + ".time", System.currentTimeMillis());
//            putDataMapReq.getDataMap().putString(KEY + "get_" + key, data2);
//            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".end", add == null);
//            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".int", false);
//            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
//            PendingResult<DataApi.DataItemResult> pendingResult =
//                    Wearable.DataApi.putDataItem(google, putDataReq);
////        System.out.println("Send " + key + " " + data2);
//            if (add != null) {
////                System.out.println("resend");
////                    while (wt.get()) {
////                        try {
////                            Thread.sleep(5L);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                    }
//                sendData(key, add);
////        }
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            MainActivityWear.main.viewError(e.getMessage() + " 1");
//        }

        try {
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/get_" + key);
//            MainActivityWear.main.viewError("SEND MESSAGE " + Math.random());
//            putDataMapReq.getDataMap().putInt(KEY + "get_" + key, cnt++);
            putDataMapReq.getDataMap().putString(KEY + "get_" + key, data2);
            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".int", false);
            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + key + ".end", true);
//            putDataMapReq.getDataMap().putLong(KEY + "get_" + key + ".time", System.currentTimeMillis());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(google, putDataReq);
//            MainActivityWear.main.viewError("sendData " + key + " " + data2);
//            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                @Override
//                public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
////                    System.out.println(dataItemResult.getDataItem().getUri());
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            List<Node> connectedNodes =
//                                    Wearable.NodeApi.getConnectedNodes(google).await().getNodes();
//                            System.out.println("Size: " + connectedNodes.size());
//                            for (Node n : connectedNodes) {
//                                System.out.println(n.getDisplayName());
//                            }
//                            Wearable.DataApi.addListener(google, MainActivityWear.this);
//                        }
//                    }).start();
//                }
//            });
//            System.out.println("sendData " + key + " " + google.isConnected());
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private static SharedPreferences preferences;

    public static ConcurrentHashMap<String, ArrayList<Integer>> sections;
    public static ConcurrentHashMap<String, Bitmap[]> icons;
    public static JSONArray available_icons;
    public static ArrayList<String> favorite_ids, favorite_icons;
    public static ConcurrentHashMap<String, JSONObject> users_list;

    public static void ini(Context c) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        icons = new ConcurrentHashMap<>();
        sections = new ConcurrentHashMap<>();
        users_list = new ConcurrentHashMap<>();
        available_icons = new JSONArray();
        favorite_icons = new ArrayList<>();
        favorite_ids = new ArrayList<>();
//        System.out.println("preferences " + (preferences == null));
    }

    public static void storeData(String key, String value) {
        try {
            SharedPreferences.Editor ed = preferences.edit();
            ed.putString(key, value);
            ed.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static String restoreData(String key) {
        try {
            return preferences.getString(key, "");
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
        return "";
    }

    // Get json object from the data item
    public String getJSONString(DataMap dataMap, String k) {
        return dataMap.getString(KEY + k);
    }

    // Get step
    public int getStep(DataMap dataMap, String k) {
        return dataMap.getInt(KEY + k + ".step");
    }

    // Get json object from the data item
    public boolean isEnd(DataMap dataMap, String k) {
        return dataMap.getBoolean(KEY + k + ".end");
    }

//    public void printMultiple(String s) {
//        System.out.println(s.substring(s.length() - 20));
//        System.out.println("String length: " + s.length());
//        for (int i = 0; i < s.length() / 10000; i++) {
//            System.out.println("msg");
//            if ((i + 1) * 10000 >= s.length())
//                System.out.println(s.substring(i * 10000));
//            else
//                System.out.println(s.substring(i * 10000, (i + 1) * 10000));
//        }
//    }

    public static void refreshIcons(final String s) {
//        System.out.println("refreshIcons " + s.length());
//        printMultiple(s);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//            System.gc();
//            System.out.println("refreshIcons2 " + s.length());

//            System.out.println("(\")(\\\")");
                    //s = s.replaceAll("\u005c", "\u005c\u005c").replaceAll("\u0022", "\u005c\u0022");

//            if (s.length() > 92163)
//                System.out.println("symbol: " + s.charAt(92163) + " " + s.length() + " " + s.substring(92150, 92170));
                    JSONObject obj1 = new JSONObject(s);
                    JSONObject sections = obj1.getJSONObject("sections");
//            JSONObject icons = obj1.getJSONObject("icons");
                    JSONArray favorite_icons = obj1.getJSONArray("favorite_icons");
                    JSONArray favorite_ids = obj1.getJSONArray("favorite_ids");

                    ArrayList<String> fav_icons = new ArrayList<>(favorite_icons.length());
                    ArrayList<String> fav_ids = new ArrayList<>(favorite_ids.length());
                    ConcurrentHashMap<String, ArrayList<Integer>> sect = new ConcurrentHashMap<>();
//            ConcurrentHashMap<String, Bitmap[]> icn = new ConcurrentHashMap<>();

                    for (int i = 0; i < favorite_icons.length(); i++) {
                        fav_icons.add(favorite_icons.getString(i));
                    }
                    for (int i = 0; i < favorite_ids.length(); i++) {
                        fav_ids.add(favorite_ids.getString(i));
                    }
                    for (Iterator<String> it = sections.keys(); it.hasNext(); ) {
                        String key = it.next();
                        JSONArray aw = sections.getJSONArray(key);
                        ArrayList<Integer> sw = new ArrayList<>(aw.length());
                        for (int i = 0; i < aw.length(); i++) {
                            sw.add(aw.getInt(i));
                        }
                        sect.put(key, sw);
                    }
//            for (Iterator<String> it = icons.keys(); it.hasNext(); ) {
//                String key = it.next();
//                JSONObject aw = icons.getJSONObject(key);
//                Bitmap btm0 = getBitmapString(aw.getString("source"));
////                Bitmap btm1 = getBitmapString(aw.getString("gray"));
////                Bitmap btm2 = getBitmapString(aw.getString("yellow"));
//                icn.put(key, new Bitmap[]{btm0/*, btm1, btm2*/});
//            }

                    WearService.sections = sect;
//            Storage.icons = icn;
                    WearService.favorite_icons = fav_icons;
                    WearService.favorite_ids = fav_ids;
                    WearService.available_icons = obj1.getJSONArray("available_icons");

                    // восстанавливаем часть иконок из локального хранилища
                    for (int i = 0; i < WearService.available_icons.length(); i++) {
                        String str = WearService.available_icons.getJSONObject(i).getString("name");
                        String data = WearService.restoreData("icon_" + str);
                        // берём иконку из хранилища, считаем контрольную сумму, если не совпадают то загружаем новую с андроида
                        if (data.isEmpty() /*|| не совпадает контрольная сумма*/) {
                            String ns = ICONS_KEY + "_" + Math.random();
                            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/get_" + ns);
                            putDataMapReq.getDataMap().putLong(KEY + "get_" + ns + ".time", System.currentTimeMillis());
                            putDataMapReq.getDataMap().putString(KEY + "get_" + ns, str);
                            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + ns + ".end", true);
                            putDataMapReq.getDataMap().putBoolean(KEY + "get_" + ns + ".int", false);
                            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                            PendingResult<DataApi.DataItemResult> pendingResult =
                                    Wearable.DataApi.putDataItem(google, putDataReq);
                        } else {
                            if (!WearService.icons.containsKey(str))
                                WearService.icons.put(str, new Bitmap[]{getBitmapString(data)});
                        }
                    }

                    SendActivityWear.max.set(WearService.available_icons.length());

//            System.out.println("refreshIcons end");
//            MainActivityWear.main.viewError("refreshIcons end");
                    MainActivityWear.main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                    MainActivityWear.main.viewError(MainActivityWear.available_icons.length() + " " + MainActivityWear.ad.size());
                            if (MainActivityWear.adp != null)
                                MainActivityWear.adp.notifyDataSetChanged();
                            if (SendActivityWear.in) {
                                SendActivityWear.send.refresh();
                            }
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivityWear.service.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }).start();
    }
}
