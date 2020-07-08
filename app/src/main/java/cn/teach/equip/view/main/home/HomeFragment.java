package cn.teach.equip.view.main.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.teach.equip.R;
import cn.teach.equip.api.HttpResultSubscriber;
import cn.teach.equip.api.HttpServerImpl;
import cn.teach.equip.bean.pojo.BannerBO;
import cn.teach.equip.bean.pojo.WenZhangListBo;
import cn.teach.equip.mvp.MVPBaseFragment;
import cn.teach.equip.view.wenzhanglist.WenzhangListActivity;
import cn.teach.equip.weight.lgrecycleadapter.LGRecycleViewAdapter;
import cn.teach.equip.weight.lgrecycleadapter.LGViewHolder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeFragment extends MVPBaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View {

    @BindView(R.id.sousuo)
    LinearLayout sousuo;
    @BindView(R.id.saoma)
    ImageView saoma;
    @BindView(R.id.baner)
    Banner banner;
    @BindView(R.id.banner_recycle)
    RecyclerView bannerRecycle;
    @BindView(R.id.jingxuan_recycle)
    RecyclerView jingxuanRecycle;
    @BindView(R.id.srl_page)
    SmartRefreshLayout srlPage;
    Unbinder unbinder;

    List<BannerBO> bannerBOS;

    private int curinPosition = 0;
    private int pageNum = 1;

    private List<WenZhangListBo.PageListBean> list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_home, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bannerRecycle.setLayoutManager(manager);
        bannerRecycle.setNestedScrollingEnabled(false);
        jingxuanRecycle.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        jingxuanRecycle.setNestedScrollingEnabled(false);
//        onScollViewListener();
        addListener();
        getArticleList(4, 1);
        setJingXuanAdapter();
    }


    private void addListener() {
        srlPage.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                srlPage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNum++;
                        getArticleList(4, pageNum);
                    }
                }, 1000);

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        getBanner();
    }

    /**
     * 获取banner
     */
    private void getBanner() {
        HttpServerImpl.getBannerList().subscribe(new HttpResultSubscriber<List<BannerBO>>() {
            @Override
            public void onSuccess(List<BannerBO> s) {
                bannerBOS = s;
                setBanner();
                setBannerPointAdapter();
            }

            @Override
            public void onFiled(String message) {
                showToast2(message);
            }
        });
    }


    /**
     * 获取文章列表
     */
    private void getArticleList(int type, int page) {
        HttpServerImpl.getArticleList(type, page).subscribe(new HttpResultSubscriber<WenZhangListBo>() {
            @Override
            public void onSuccess(WenZhangListBo s) {
                if (pageNum == 1) {
                    list = s.getPageList();
                } else {
                    if (s.getPageList().isEmpty()) {
                        pageNum--;
                    }
                    list.addAll(s.getPageList());
                }
                srlPage.finishLoadMore();
                setJingXuanAdapter();
            }

            @Override
            public void onFiled(String message) {
                showToast2(message);
                srlPage.finishLoadMore();
            }
        });
    }


    /**
     * 设置精选文章适配器
     */
    private void setJingXuanAdapter() {
        LGRecycleViewAdapter<WenZhangListBo.PageListBean> adapter =
                new LGRecycleViewAdapter<WenZhangListBo.PageListBean>(list) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_home_wenzhang;
                    }

                    @Override
                    public void convert(LGViewHolder holder, WenZhangListBo.PageListBean s, int position) {
                        holder.setText(R.id.wenzhang_title, s.getTitle());
                        holder.setImageUrl(getActivity(), R.id.wenzhang_img, s.getSmallImgUrl());
                    }
                };
        jingxuanRecycle.setAdapter(adapter);
    }


    @OnClick({R.id.zhuangbei_layout, R.id.jiaoxue_layout, R.id.ronghe_layout,
            R.id.guoqi_layout, R.id.yunwei_layout, R.id.zonghe_layout})
    public void clickView(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.zhuangbei_layout:
                bundle.putInt("type", 0);
                gotoActivity(WenzhangListActivity.class, bundle, false);
                break;
            case R.id.jiaoxue_layout:

                break;
            case R.id.ronghe_layout:
                bundle.putInt("type", 1);
                gotoActivity(WenzhangListActivity.class, bundle, false);
                break;
            case R.id.guoqi_layout:
                bundle.putInt("type", 2);
                gotoActivity(WenzhangListActivity.class, bundle, false);
                break;
            case R.id.yunwei_layout:

                break;
            case R.id.zonghe_layout:

                break;
        }
    }


    private void setBanner() {
        List<String> images = new ArrayList<>();
        for (BannerBO banner : bannerBOS) {
            images.add(banner.getImgUrl());
        }
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.NOT_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setOnBannerListener(position -> {
//            if (StringUtils.isEmpty(bannerBOS.get(position).getForwardUrl())) {
//                return;
//            }
//            Bundle bundle = new Bundle();
//            bundle.putString("url", bannerBOS.get(position).getForwardUrl());
//            gotoActivity(WebActivity.class, bundle, false);
        });
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                curinPosition = i;
                setBannerPointAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }


    /**
     * 设置banner点适配器
     */
    private void setBannerPointAdapter() {
        LGRecycleViewAdapter<BannerBO> adapter = new LGRecycleViewAdapter<BannerBO>(bannerBOS) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_banner_point;
            }

            @Override
            public void convert(LGViewHolder holder, BannerBO bannerBO, int position) {
                if (curinPosition == position) {
                    holder.getView(R.id.un_select).setVisibility(View.GONE);
                    holder.getView(R.id.select_point).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.un_select).setVisibility(View.VISIBLE);
                    holder.getView(R.id.select_point).setVisibility(View.GONE);
                }
            }
        };
        bannerRecycle.setAdapter(adapter);
    }


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */
            //Glide 加载图片简单用法
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(context).load(path).into(imageView);
        }

//        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
//        @Override
//        public ImageView createImageView(Context context) {
//            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
//            SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
//            return simpleDraweeView;
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}