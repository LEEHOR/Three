package net.coahr.three3.three.customView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.BrowseImageAdapter;
import net.coahr.three3.three.RecyclerViewAdapter.ProStartImageBrowse;

import java.util.List;

public class BrowseSubjectImagesView extends AppCompatDialog{
    private String TAG="BrowseSubjectImagesView";
    private Button mDeleteBtn;
    private Button mBackBtn;
    private List<ImagesDB> mlist;
    private Context mContext;
    private int position;
    private String url;
    private RecyclerView mRecyclerView;
    private int mCurrentPage;
    private BrowseSubjectImagesInteface browseSubjectIntefaces;
    private ProStartImageBrowse adapter;

    public BrowseSubjectImagesView(Context context, List<ImagesDB> list, int position, String url) {
        super(context);
        this.mlist = list;
        this.mContext = context;
        this.position = position;
        this.url = url;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public BrowseSubjectImagesView(Context context) {
        super(context);
    }

    public BrowseSubjectImagesView(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_browsesubjectimage);
        //设置window背景，默认的背景颜色会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        //getWindow().setBackgroundDrawable(new ColorDrawable(0xffcccccc));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(true);
        mRecyclerView = findViewById(R.id.SubjectImages_recyclerView);
        mBackBtn = findViewById(R.id.subject_browseBack);
        mDeleteBtn = findViewById(R.id.subject_browseDelete);
       // mDeleteBtn.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProStartImageBrowse(mContext, mlist, R.layout.item_browseimage, null);
        mRecyclerView.setAdapter(adapter);
        if (position != 0) {
            mRecyclerView.smoothScrollToPosition(position);
        }
        recycleViewScrolling();


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseSubjectIntefaces.DialogDismiss();
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  browseSubjectIntefaces.delete();
            }
        });

    }

    public interface BrowseSubjectImagesInteface {
        void DialogDismiss();
        void delete(int id, String ImageName);
    }
    public void setBrowseSubjectImageInteface(BrowseSubjectImagesInteface browseSubjectInteface){
        this.browseSubjectIntefaces=browseSubjectInteface;
    }

    private void recycleViewScrolling(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:


                        break;

                    case RecyclerView.SCROLL_STATE_IDLE:


                        int width = recyclerView.getWidth();
                        mCurrentPage = totalDy / width;
//                        System.out.println("=============::::::"+page);
//                        System.out.println(Math.abs(totalDy % width));
                        if (width * 0.5 <= Math.abs(totalDy % width)) {

                            mCurrentPage += 1;
//                            System.out.println("---------------page:" + page);
                        }
                        System.out.println("-------------------------page:" + mCurrentPage);
                        recyclerView.smoothScrollToPosition(mCurrentPage);

                        break;

                }

            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                System.out.println("onScrolled"+dx);
                totalDy += dx;

            }
        });

    }

}
