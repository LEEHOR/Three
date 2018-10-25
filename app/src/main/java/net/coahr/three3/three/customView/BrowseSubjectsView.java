package net.coahr.three3.three.customView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.BrowseSubjectAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by 李浩
 * 2018/4/27
 */
public class  BrowseSubjectsView<T> extends Dialog {
    private RecyclerView recyclerView;
    private Context mcontext;
    private ImageView imageView;
    private List subjectsDBLists;
    private BrowseSubjectDialogListener subjectDialogListener;
    public  BrowseSubjectsView(@NonNull Context context,List list) {
        super(context);
        this.mcontext=context;
        this.subjectsDBLists =list;

    }

    public BrowseSubjectsView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mcontext=context;
    }

    public BrowseSubjectsView(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public void setBrowseSubjectDialogListener(BrowseSubjectDialogListener listener){
        this.subjectDialogListener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//需要在设置内容之前定义
        setContentView(R.layout.layout_browse_subject);
        //设置window背景，默认的背景颜色会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
      //  getWindow().setBackgroundDrawable(new ColorDrawable(0xffcccccc));
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(false);
        imageView=findViewById(R.id.browseSubject_back);
        recyclerView=findViewById(R.id.browseSubject_recycle);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectDialogListener.OnImageViewBack();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mcontext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
       // List<ImagesDB> all = DataSupport.findAll(ImagesDB.class);
        BrowseSubjectAdapter browseSubjectAdapter=new BrowseSubjectAdapter(mcontext,subjectsDBLists,R.layout.item_browse_subject_list,new mySubject());
        recyclerView.setAdapter(browseSubjectAdapter);

    }
    class mySubject implements BrowseSubjectAdapter.clickSubjectListener{

        @Override
        public void SubjectListener(int position, Map map,String qu3,int id) {
            subjectDialogListener.OnItemClick(position,map,qu3,id);
        }
    }
    public interface BrowseSubjectDialogListener{
        void OnImageViewBack();
        void OnItemClick(int position,Map map,String qu3,int id);
    }
}
