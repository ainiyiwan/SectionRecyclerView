package com.zy.xxl.sectionrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.zy.xxl.sectionrecyclerview.adpter.SectionAdapter;
import com.zy.xxl.sectionrecyclerview.data.DataServer;
import com.zy.xxl.sectionrecyclerview.data.MySection;
import com.zy.xxl.sectionrecyclerview.data.Video;
import com.zy.xxl.sectionrecyclerview.loader.GlideImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnBannerListener {

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeLayout;

    private int[] imageId = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap
            .ic_launcher, R.mipmap.ic_launcher}; // 定义并初始化保存图片id的数组

    static final int REFRESH_COMPLETE = 0X1112;
    private List<MySection> list = new ArrayList<>();
    private List<MySection> newList = new ArrayList<>();
    private Banner banner;
    private GridView gridView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    String[] urls = getResources().getStringArray(R.array.url4);
                    List list = Arrays.asList(urls);
                    List arrayList = new ArrayList(list);
                    banner.update(arrayList);
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };
    private SectionAdapter sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSwipeLayout.setOnRefreshListener(this);
//        getData();
//        newList = DataServer.getSampleData(list);
//        initRecycler();

        //以下代码纯属装逼 请忽略
        Observable.create(new ObservableOnSubscribe<List<MySection>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MySection>> e) throws Exception {
                getData();
                newList = DataServer.getSampleData(list);
                e.onNext(newList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MySection>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MySection> list) {
                        Gson gson = new Gson();
                        String s = gson.toJson(list);
                        Logger.t("TAG").json(s);
                        initRecycler();
                        sectionAdapter.addHeaderView(getHeader());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private View getHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.header, null);
        banner = (Banner) header.findViewById(R.id.banner);
        banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, MyApplication.H
                / 4));

        String[] urls = getResources().getStringArray(R.array.url4);
        List list = Arrays.asList(urls);
        List arrayList = new ArrayList(list);
        //简单使用
        banner.setImages(arrayList)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener(this)
                .start();
        banner.startAutoPlay();

        gridView = (GridView) header.findViewById(R.id.gridView);
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new gridViewListener());

        return header;
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        sectionAdapter = new SectionAdapter(R.layout.item_section_content, R.layout.def_section_head,
                newList);

        sectionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MySection mySection = newList.get(position);
                if (mySection.isHeader) {
                    Toast.makeText(MainActivity.this, mySection.header, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, mySection.t.getName(), Toast.LENGTH_LONG).show();
                }
            }
        });
        sectionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "onItemChildClick" + position, Toast.LENGTH_LONG).show();
            }
        });
        mRecyclerView.setAdapter(sectionAdapter);
    }

    private void getData() {
        int size = 6;
        for (int i = 0; i < size; i++) {
            list.add(new MySection(new Video(DataServer.HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, DataServer
                    .CYM_CHAD + "(2017)", "2017")));
        }

        for (int i = 0; i < size - 1; i++) {
            list.add(new MySection(new Video(DataServer.HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, DataServer
                    .CYM_CHAD + "(2016)", "2016")));
        }

        for (int i = 0; i < size - 2; i++) {
            list.add(new MySection(new Video(DataServer.HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, DataServer
                    .CYM_CHAD + "(2015)", "2015")));
        }

        for (int i = 0; i < size + 1; i++) {
            list.add(new MySection(new Video(DataServer.HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, DataServer
                    .CYM_CHAD + "(2014)", "2014")));
        }

        for (int i = 0; i < size - 3; i++) {
            list.add(new MySection(new Video(DataServer.HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, DataServer
                    .CYM_CHAD + "(2013)", "2013")));
        }
    }


    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(getApplicationContext(), "你点击了：" + position, Toast.LENGTH_SHORT).show();
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview; // 声明ImageView的对象
            if (convertView == null) {
                imageview = new ImageView(MainActivity.this); // 实例化ImageView的对象
                imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // 设置缩放方式
                imageview.setPadding(5, 0, 5, 0); // 设置ImageView的内边距
            } else {
                imageview = (ImageView) convertView;
            }
            imageview.setImageResource(imageId[position]); // 为ImageView设置要显示的图片
            return imageview; // 返回ImageView
        }

        /*
         * 功能：获得当前选项的ID
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            //System.out.println("getItemId = " + position);
            return position;
        }

        /*
         * 功能：获得当前选项
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return position;
        }

        /*
         * 获得数量
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return imageId.length;
        }
    }

    private class gridViewListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, "你点击了" + position, Toast.LENGTH_SHORT).show();
        }
    }
}
