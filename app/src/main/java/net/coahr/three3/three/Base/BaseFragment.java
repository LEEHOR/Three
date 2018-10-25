package net.coahr.three3.three.Base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yuwei on 2018/4/3.
 */

public class BaseFragment extends Fragment {
    public Context mContext;
    public BaseRecyclerViewAdapter adapter;
    private Intent mIntent;
    public  CompositeSubscription mCompositeSubscription; //解除订阅, RX
    public PreferencesTool mPreferencesTool;

    public void setmPreferencesTool(PreferencesTool mPreferencesTool) {
        this.mPreferencesTool = mPreferencesTool;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreferencesTool = new PreferencesTool(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    //recyclerView Item点击事件
    public void setAdapter(BaseRecyclerViewAdapter adapter)
    {
        this.adapter = adapter;
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListener(View v, int position , Intent intent,Context context) {
                Toast.makeText(context , "点击了"+position , Toast.LENGTH_LONG).show();

                if(intent != null || mIntent != null)
                    startActivity(intent);
            }
        });
    }

    public Intent getmIntent() {
        return mIntent;
    }

    public void setmIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }

    protected void addSubscription(Subscription s)
    {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
