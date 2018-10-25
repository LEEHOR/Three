package net.coahr.three3.three.customView;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import net.coahr.three3.three.Project.ProjectAttachmentActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.BrowseImageAdapter;
import net.coahr.three3.three.RecyclerViewAdapter.ProjectAttachmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuwei on 2018/4/13.
 */

public class BrowseImageView extends AlertDialog {
    private Button  mDeleteBtn;
    private Button  mBackBtn;
    private DeleteImageInterface    deleteImageInterface;
    private BackImageInterface      backImageInterface;
    private RecyclerView    mRecyclerView;
    private Context     mContext;
    private List mList;
    private int mCurrentPage;
    private int scrollPosition;



    public Button getmDeleteBtn() {
        return mDeleteBtn;
    }

    public int getmCurrentPage() {
        return mCurrentPage;
    }

    public void setDeleteImageInterface(DeleteImageInterface deleteImageInterface ) {

        this.deleteImageInterface = deleteImageInterface;
    }

    public DeleteImageInterface getDeleteImageInterface() {
        return deleteImageInterface;
    }

    public interface DeleteImageInterface
    {
        void delete();
    }

    public void setBackImageInterface(BackImageInterface backImageInterface) {
        this.backImageInterface = backImageInterface;
    }

    public BackImageInterface getBackImageInterface() {
        return backImageInterface;
    }

    public interface BackImageInterface
    {
        void back();
    }

    public BrowseImageView(@NonNull Context context  , List list , int  index) {
        super(context);
        this.mContext = context;
        this.mList = list;
        this.scrollPosition = index;
       supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public BrowseImageView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BrowseImageView(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_browseimage);
        mRecyclerView = findViewById(R.id.browseSubjectImages_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        BrowseImageAdapter adapter = new BrowseImageAdapter(mContext , null , R.layout.item_browseimage , null);
//        setAdapter(new BrowseImageAdapter(mContext , null , R.layout.item_project_attachment));
//        adapter.setWindowWidth(getWindowWidth());
//        adapter.setWindowHeight(getWindowHeight());

        adapter.Update(mList);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.smoothScrollToPosition(this.scrollPosition);


        mBackBtn = findViewById(R.id.browseBack);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backImageInterface.back();
            }
        });

        mDeleteBtn = findViewById(R.id.browseDelete);
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteImageInterface.delete();
            }
        });


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
                        System.out.println("-------------------------page:"+mCurrentPage);
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
