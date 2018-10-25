package net.coahr.three3.three.Module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.coahr.three3.three.Base.BaseFragment;
import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.Model.VerifyDataModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.R;
import net.coahr.three3.three.RecyclerViewAdapter.VerifyAdater;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.Verify.VerifyDetailActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuwei on 2018/4/3.
 */


@SuppressLint("ValidFragment")
public class VerifyFragment extends BaseFragment {
    private Context mContext;
    private View line_left , line_right;
    private RadioButton verifyBtn_left , verifyBtn_right;
    private RadioGroup verifyBtnGroup;
    private RecyclerView mRecyclerView;
    private ChangeListInterface mChangeListInterface;
    private static  Intent mIntent;
    private EditText mSearchView;
    private VerifyAdater verifyAdapter;
    private PreferencesTool mPreferencesTool;
    private  String sessionId;
    private boolean searchFlag = false;


    public void setmChangeListInterface(ChangeListInterface mChangeListInterface) {
        this.mChangeListInterface = mChangeListInterface;
    }

    public ChangeListInterface getmChangeListInterface() {
        return mChangeListInterface;
    }

    public  interface ChangeListInterface
    {
        void change(boolean falg);
    }


    public VerifyFragment(Context context)
    {
        this.mContext = context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify , container ,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreferencesTool = new PreferencesTool(getActivity());
        sessionId = mPreferencesTool.getSessionId("sessionId");
            findUI(view);
            remoteRequest(-1 , null);
    }

    public  void findUI(View view)
    {
        line_left       = view.findViewById(R.id.line_left);
        line_right      = view.findViewById(R.id.line_right);
        verifyBtn_left  = view.findViewById(R.id.verify_left);
        verifyBtn_right = view.findViewById(R.id.verify_right);
        verifyBtnGroup  = view.findViewById(R.id.rg_verify);
        mSearchView     = view.findViewById(R.id.search);
        mIntent = new Intent(mContext , VerifyDetailActivity.class);
        mIntent.putExtra("status" , -1);
        Drawable drawableR = null;
        drawableR = getResources().getDrawable(R.drawable.search);
        drawableR.setBounds(0, 0, 50, 56);
        mSearchView.setCompoundDrawables(null , null , drawableR , null);
        mRecyclerView   = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //verifyAdapter= new VerifyAdater(mContext, null, R.layout.item_recyclerview_verify, this, mIntent);
        setAdapter(new VerifyAdater(mContext, null, R.layout.item_recyclerview_verify, this, mIntent));
            mRecyclerView.setAdapter(adapter);
        setListenner();
        mSearchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Drawable drawable = mSearchView.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                if(event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if(event.getX() > mSearchView.getWidth() - mSearchView.getPaddingRight() - drawable.getIntrinsicWidth())
                {
                    remoteRequest(searchFlag ? 1 : -1 , mSearchView.getText().toString());
                }
                return false;
            }

        });
    }

    public void setListenner()
    {

        verifyBtnGroup.setOnCheckedChangeListener(new VerifyBtnCheckedChangeListenner());


    }

    class VerifyBtnCheckedChangeListenner implements RadioGroup.OnCheckedChangeListener
    {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            boolean flag = false;
            searchFlag = flag;
            if(checkedId == verifyBtn_left.getId())
            {
                line_left.setBackground(new ColorDrawable(Color.WHITE));
                line_right.setBackground(getResources().getDrawable(R.color.colorbg));
                flag = false;
                remoteRequest(-1 , null);
            }
            else
            {
                line_right.setBackground(new ColorDrawable(Color.WHITE));
                line_left.setBackground(getResources().getDrawable(R.color.colorbg));
//                ((VerifyAdater)adapter).setPass(true);
                flag = true;

                remoteRequest(1 , null);
            }
            mIntent.putExtra("status" , flag ? 1 : -1);
            mChangeListInterface.change(flag);

        }
    }

    void remoteRequest(Integer status , String search)
    {

        Map<String, Object> map = new HashMap<>();
        map.put("sessionId" , sessionId);
        map.put("status" , status);
        map.put("search" , search);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getVerifyData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseModel model) {

                            if (model.getResult().equals("1"))
                            {
                                VerifyDataModel dataModel = (VerifyDataModel) model.getData();
                                adapter.Update(dataModel.getList());
                            }

                    }
                });
        addSubscription(subscription);


    }


}
