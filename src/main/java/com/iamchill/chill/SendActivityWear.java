package com.iamchill.chill;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SendActivityWear extends Activity {

    public static boolean more;
    public static boolean in;
    public static SendActivityWear send;

    @Override
    protected void onResume() {
        super.onResume();
        in = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        in = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        send = this;
        setContentView(R.layout.activity_send);
        GridViewPager pg = (GridViewPager) findViewById(R.id.gridpager);
        pg.setAdapter(new PageAdapter(this));
        DotsPageIndicator ind = (DotsPageIndicator) findViewById(R.id.page_indicator);
        ind.setDotColor(0xff979797);
        ind.setDotColorSelected(0xff00cea0);
        ind.setPager(pg);
    }

    public static AtomicInteger refresh = new AtomicInteger();
    public static AtomicInteger max = new AtomicInteger();

//    public static AtomicLong al = new AtomicLong(System.currentTimeMillis());

    public void refresh() {
//        MainActivityWear.main.stopLoading();
        refresh.set(refresh.get() + 1);
//        if (refresh.get() < 10 && refresh.get() < max.get()) return;
//        MainActivityWear.main.viewError("refresh " + refresh.get() + " " + max.get());
        if (/*System.currentTimeMillis() - al.get() - 10000 > 0 || */refresh.get() == max.get()) {
//            MainActivityWear.main.viewError("invalidate");
//            System.out.println("refresh");
//            max.set(max.get() - refresh.get());
            refresh.set(0);
//            al.set(System.currentTimeMillis());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GridViewPager pg = (GridViewPager) findViewById(R.id.gridpager);
//                    pg.refreshDrawableState();
                    pg.getChildAt(1).findViewById(R.id.more_list).invalidate();
//                    pg.getChildAt(0).invalidate();
                    setImages(pg.getChildAt(0));
                }
            });
        }
    }

    public void setImages(View w) {
        if (w == null) return;
        ImageView i0 = (ImageView) w.findViewById(R.id.imageButton9);
        ImageView i1 = (ImageView) w.findViewById(R.id.imageButton10);
        ImageView i2 = (ImageView) w.findViewById(R.id.imageButton7);
        ImageView i3 = (ImageView) w.findViewById(R.id.imageButton8);
        ImageView i4 = (ImageView) w.findViewById(R.id.imageButton6);
        ImageView i5 = (ImageView) w.findViewById(R.id.imageButton5);

        try {
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(0))) {
                i0.setAnimation(null);
                i0.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(0))[0]);
            } else {
                i0.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i0.setImageResource(R.drawable.ic_loading_spinner);
            }
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(1))) {
                i1.setAnimation(null);
                i1.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(1))[0]);
            } else {
                i1.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i1.setImageResource(R.drawable.ic_loading_spinner);
            }
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(2))) {
                i2.setAnimation(null);
                i2.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(2))[0]);
            } else {
                i2.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i2.setImageResource(R.drawable.ic_loading_spinner);
            }
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(3))) {
                i3.setAnimation(null);
                i3.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(3))[0]);
            } else {
                i3.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i3.setImageResource(R.drawable.ic_loading_spinner);
            }
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(4))) {
                i4.setAnimation(null);
                i4.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(4))[0]);
            } else {
                i4.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i4.setImageResource(R.drawable.ic_loading_spinner);
            }
            if (WearService.icons != null && WearService.favorite_icons.size() > 0 && WearService.icons.containsKey(WearService.favorite_icons.get(5))) {
                i5.setAnimation(null);
                i5.setImageBitmap(WearService.icons.get(WearService.favorite_icons.get(5))[0]);
            } else {
                i5.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                i5.setImageResource(R.drawable.ic_loading_spinner);
            }

            if (WearService.favorite_icons.size() > 0) {
                i0.setTag(WearService.favorite_icons.get(0));
                i1.setTag(WearService.favorite_icons.get(1));
                i2.setTag(WearService.favorite_icons.get(2));
                i3.setTag(WearService.favorite_icons.get(3));
                i4.setTag(WearService.favorite_icons.get(4));
                i5.setTag(WearService.favorite_icons.get(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public int getCountIcons() {
        if (!WearService.sections.containsKey("main") || WearService.sections.get("main").isEmpty())
            return 0;
        return WearService.sections.get("main").size();
    }

    public int dp2px(int dip) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    class PageAdapter extends GridPagerAdapter {

        private Context mContext;

        public PageAdapter(Context c) {
            mContext = c.getApplicationContext();
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
            View w = null;
            more = false;
//            System.out.println("instantiateItem");
            if (col == 0) {
                w = View.inflate(mContext, R.layout.short_send, null);
                View.OnClickListener ls = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        System.out.println("click " + v.getTag());
                        MainActivityWear.service.sendMessage("icon", v.getTag().toString());
                        finish();
                    }
                };
                ImageView i0 = (ImageView) w.findViewById(R.id.imageButton9);
                ImageView i1 = (ImageView) w.findViewById(R.id.imageButton10);
                ImageView i2 = (ImageView) w.findViewById(R.id.imageButton7);
                ImageView i3 = (ImageView) w.findViewById(R.id.imageButton8);
                ImageView i4 = (ImageView) w.findViewById(R.id.imageButton6);
                ImageView i5 = (ImageView) w.findViewById(R.id.imageButton5);

                i1.setOnClickListener(ls);
                i2.setOnClickListener(ls);
                i3.setOnClickListener(ls);
                i4.setOnClickListener(ls);
                i0.setOnClickListener(ls);
                i5.setOnClickListener(ls);

                setImages(w);
            } else if (col == 1) {
                w = View.inflate(mContext, R.layout.more, null);
                WearableListView listView = ((WearableListView) w.findViewById(R.id.more_list));
                listView.setAdapter(new AppAdapter(SendActivityWear.this));
                listView.setGreedyTouchMode(true);
                listView.setHasFixedSize(true);
                more = true;
//                listView.getAdapter().notifyDataSetChanged();
            } else if (col == 2) {
                w = View.inflate(mContext, R.layout.favourite, null);
            }
            viewGroup.addView(w);
            return w;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, int i1, Object o) {
            viewGroup.removeView((View) o);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }
    }


    class AppAdapter extends WearableListView.Adapter {

        //        private final Context mContext;
        private final LayoutInflater mInflater;

        public AppAdapter(Context context) {
//            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        public class ItemViewHolder extends WearableListView.ViewHolder implements View.OnClickListener {

            LinearLayout lin;
            ImageView view;
            ImageView a, b, c;

            public ItemViewHolder(View itemView) {
                super(itemView);
                lin = (LinearLayout) itemView.findViewById(R.id.lin_layout2);
                view = (ImageView) itemView.findViewById(R.id.imageView);
                a = (ImageView) itemView.findViewById(R.id.imageButton1);
                b = (ImageView) itemView.findViewById(R.id.imageButton2);
                c = (ImageView) itemView.findViewById(R.id.imageButton3);
                a.setOnClickListener(this);
                b.setOnClickListener(this);
                c.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                try {
                    int index_ = Integer.valueOf(v.getTag().toString()) * 3;
                    String type = "icon", content = null;
                    if (more) {
                        if (index_ == 0) {
                            type = "location";
                            content = "";
                        } else {
                            index_ -= 3;
                        }
                    }
                    if (content == null) {
                        if (v.equals(b)) {
                            index_++;
                        } else if (v.equals(c)) {
                            index_ += 2;
                        }
                        try {
                            content = WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_)).getString("name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                System.out.println("Send msg: " + type + " " + content);
                    MainActivityWear.service.sendMessage(type, content);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
//            System.out.println("onCreateViewHolder");
            return new ItemViewHolder(mInflater.inflate(R.layout.button_location, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
//            System.out.println("onBindViewHolder");
            try {
                ItemViewHolder itemHolder = (ItemViewHolder) holder;
                ImageView img = itemHolder.view;
//                WearableListView listView = ((WearableListView) findViewById(R.id.more_list));
//                if (((ItemViewHolder) holder).itemView.getTag() == null) {
                if (position != 0 || !more) {
                    img.setVisibility(View.INVISIBLE);
                    img.setMaxHeight(1);
                    img.setMaxWidth(1);
                    itemHolder.a.setVisibility(View.VISIBLE);
                    itemHolder.c.setVisibility(View.VISIBLE);
                    itemHolder.a.setMaxHeight(dp2px(30));
                    itemHolder.a.setMaxWidth(dp2px(30));
                    itemHolder.c.setMaxHeight(dp2px(30));
                    itemHolder.c.setMaxWidth(dp2px(30));
                    itemHolder.a.setClickable(true);
                    itemHolder.c.setClickable(true);
//                    img.setEnabled(false);
                } else {
                    itemHolder.a.setVisibility(View.INVISIBLE);
                    itemHolder.c.setVisibility(View.INVISIBLE);
                    itemHolder.a.setMaxHeight(0);
                    itemHolder.a.setMaxWidth(0);
                    itemHolder.c.setMaxHeight(0);
                    itemHolder.c.setMaxWidth(0);
                    itemHolder.a.setClickable(false);
                    itemHolder.c.setClickable(false);
                    img.setVisibility(View.VISIBLE);
                    img.setMaxHeight(dp2px(8));
                    img.setMaxWidth(dp2px(150));
//                    itemHolder.a.setEnabled(false);
//                    itemHolder.c.setEnabled(false);
                }
                holder.itemView.setTag(position);
                ((ItemViewHolder) holder).a.setTag(position);
                ((ItemViewHolder) holder).b.setTag(position);
                ((ItemViewHolder) holder).c.setTag(position);
                int index_ = position * 3;
                if (more) {
                    index_ -= 3;
                    itemHolder.b.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location));
                }

                if (WearService.sections.containsKey("main") && !WearService.sections.get("main").isEmpty() && index_ >= 0) {
                    int size = getCountIcons();

                    if (WearService.icons.containsKey(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_)).getString("name"))) {
                        itemHolder.a.setAnimation(null);
                        itemHolder.a.setImageBitmap(WearService.icons.get(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_)).getString("name"))[0]);
                    } else {
                        itemHolder.a.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                        itemHolder.a.setImageResource(R.drawable.ic_loading_spinner);
                    }

                    if (index_ + 1 < size) {
                        if (WearService.icons.containsKey(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_ + 1)).getString("name"))) {
                            itemHolder.b.setAnimation(null);
                            itemHolder.b.setImageBitmap(WearService.icons.get(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_ + 1)).getString("name"))[0]);
                        } else {
                            itemHolder.b.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                            itemHolder.b.setImageResource(R.drawable.ic_loading_spinner);
                        }
                    } else {
                        itemHolder.b.setVisibility(View.INVISIBLE);
                        itemHolder.b.setMaxHeight(0);
                        itemHolder.b.setMaxWidth(0);
                        itemHolder.b.setClickable(false);
                    }

                    if (index_ + 2 < size) {
                        if (WearService.icons.containsKey(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_ + 2)).getString("name"))) {
                            itemHolder.c.setAnimation(null);
                            itemHolder.c.setImageBitmap(WearService.icons.get(WearService.available_icons.getJSONObject(WearService.sections.get("main").get(index_ + 2)).getString("name"))[0]);
                        } else {
                            itemHolder.c.setAnimation(AnimationUtils.loadAnimation(SendActivityWear.this, R.anim.rotate));
                            itemHolder.c.setImageResource(R.drawable.ic_loading_spinner);
                        }
                    } else {
                        itemHolder.c.setVisibility(View.INVISIBLE);
                        itemHolder.c.setMaxHeight(0);
                        itemHolder.c.setMaxWidth(0);
                        itemHolder.c.setClickable(false);
                    }
                }
//                } else {
//                    System.out.println(((ItemViewHolder) holder).itemView.getTag() + " " + position);
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        @Override
        public int getItemCount() {
            int cnt = (getCountIcons() / 3) + (getCountIcons() % 3 == 0 ? 0 : 1) + (more ? 1 : 0);
//            System.out.println("Count: " + cnt);
            return cnt;
        }
    }
}
