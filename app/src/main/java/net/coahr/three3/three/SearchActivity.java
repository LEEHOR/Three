package net.coahr.three3.three;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.coahr.three3.three.Base.BaseModel;
import net.coahr.three3.three.Base.BasePara;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.CommonViewHolder;
import net.coahr.three3.three.DBbase.SearchDataDB;
import net.coahr.three3.three.DBbase.UsersDB;
import net.coahr.three3.three.Model.HomeSearchModel;
import net.coahr.three3.three.NetWork.RetrofitManager;
import net.coahr.three3.three.Project.ProjectInfoActivity;
import net.coahr.three3.three.RecyclerViewAdapter.SearchAdapter;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.ToastUtils;
import net.coahr.three3.three.Util.Preferences.PreferencesTool;
import net.coahr.three3.three.customView.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class SearchActivity extends AppCompatActivity implements SearchView.SearchViewListener {

    private String TAG="SearchActivity";
    /**
     * 搜索结果列表view
     */
    private RecyclerView recyclerView;

    public  CompositeSubscription mCompositeSubscription; //解除订阅, RX

    /**
     * 搜索view
     */
    private SearchView searchView;


    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter;

    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter;

    /**
     * 搜索结果列表adapter
     */
    private SearchAdapter resultAdapter;

        /*
            获取数据
         */
    private  List<HomeSearchModel.SearchListBean>  SearchList;

    /**
     * 获取网络数据
     */
    private  HomeSearchModel data;

    /**
     * 热搜版数据
     */
    private List<String> hintData;

    /**
     * 搜索过程中自动补全数据
     */
    private List<String> autoCompleteData;

    /**
     * 搜索结果的数据
     */
    private List<HomeSearchModel.SearchListBean> resultData;

    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;

    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;

    /**
     * 设置提示框显示项的个数
     *
     * @param hintSize 提示框显示个数
     */
    public static void setHintSize(int hintSize) {
        SearchActivity.hintSize = hintSize;
    }

    private PreferencesTool mpreferencesTool;

    private  String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mpreferencesTool=new PreferencesTool(this);
        sessionId = mpreferencesTool.getSessionId("sessionId");
        setContentView(R.layout.activity_search);
        initData();
        initViews();
    }


    /**
     * 初始化视图
     */
    private void initViews() {
        recyclerView =  findViewById(R.id.main_Re_search_results);
        searchView = findViewById(R.id.search_view);
        //设置监听
        searchView.setSearchViewListener(this);
        //设置adapter
        searchView.setTipsHintAdapter(hintAdapter);
       // searchView.setAutoCompleteAdapter(autoCompleteAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

     /*   lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(SearchActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化热搜版数据
        getHintData();
        //初始化搜索结果数据
       // getResultData(null);
        //初始化自动填充
       // getAutoCompleteData(null);
    }

    /**
     * 获取db 数据
     */
    private boolean getDbData() {
       if (data!=null){
           SearchList = data.getSearchList();
          if (SearchList!=null && SearchList.size()>0){
              return true;
          }
       }
       return false;
    }

    /**
     * 获取热搜版data 和adapter
     */
    private void getHintData() {
        hintData = new ArrayList<>();
        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionId=?", sessionId);
        int User_id = usersDBS.get(0).getId();
        List<SearchDataDB> id_descList = DataBaseWork.DBSelectByTogether_limit(SearchDataDB.class, "id desc", hintSize, "usersdb_id=?", String.valueOf(User_id));
        //List<SearchDataDB> searchDataDBList = usersDBS.get(0).getSearchDataDBList();
        if (id_descList!=null && id_descList.size()>0){
            for (int i = 0; i <id_descList.size() ; i++) {
                hintData.add(id_descList.get(i).getMessage());
            }
        }

        hintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hintData);

    }

    /**
     * 获取自动补全data 和adapter
     */
    private void getAutoCompleteData(String text,ListView listView) {
        if (SearchList!=null && SearchList.size()>0){
            if (autoCompleteData==null){
                autoCompleteData = new ArrayList<>(hintSize);
            }
            autoCompleteData.clear();
            for (int i = 0, count = 0; i < SearchList.size() && count < hintSize; i++) {
                if (SearchList.get(i).getPname().contains(text.trim())) {
                    autoCompleteData.add(SearchList.get(i).getPname());
                    count++;
                }
            }
            if (autoCompleteAdapter==null){
                autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteData);
                listView.setAdapter(autoCompleteAdapter);
            }else {
                autoCompleteAdapter.notifyDataSetChanged();
            }
        }



    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData() {
        if (SearchList!=null){
            if (resultAdapter == null) {
                resultAdapter = new SearchAdapter(this,SearchList, R.layout.item_searchview_recycleview,new MyRecycleViewListener());
                recyclerView.setAdapter(resultAdapter);
            } else {
                resultAdapter.Update(SearchList);
            }
            recyclerView.setVisibility(View.VISIBLE);

        }else {

        }
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text
     */
    @Override
    public void onSearch(String text) {
        List<UsersDB> usersDBS = DataBaseWork.DBSelectByTogether_Where(UsersDB.class, "sessionId=?", sessionId);
        if (!text.equals("") && text!=null){
        if (usersDBS!=null && usersDBS.size()>0 && !usersDBS.isEmpty()){
            UsersDB usersDB = usersDBS.get(0);
            if (usersDB!=null){
                SearchDataDB searchDataDB=new SearchDataDB();
                searchDataDB.setMessage(text);
                searchDataDB.setUsersDB(usersDB);
                searchDataDB.save();
            }
        }
        }
        RequestIntenet(sessionId,text,false,null);
    }

    /**
     * 返回
     */
    @Override
    public void PreviousPage() {
        finish();
    }

    /**
     *
     * 更改自动填充
     * @param text 传入补全后的文本
     */
    @Override
    public void onRefreshAutoComplete(String text,ListView listTip) {
        RequestIntenet(sessionId,text.toString(),true,listTip);
        //更新数据

    }

    /**
     *  连接网络
     * @param sessionId
     * @param searchText
     * @param b
     */
    public void RequestIntenet(String sessionId, final String searchText, final boolean b, final ListView listView){
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);
        map.put("search",searchText);
        BasePara para = new BasePara();
        para.setData(map);
        Subscription subscription= RetrofitManager.getInstance()
                .createService(para)
                .getHomeSearchData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isNetworkAvailable(SearchActivity.this)){
                            ToastUtils.showShort(SearchActivity.this,"没有找到您要的数据");
                        }else {
                            ToastUtils.showShort(SearchActivity.this,"服务器忙");
                        }

                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                       data = (HomeSearchModel) baseModel.getData();
                        //从数据库获取数据
                        boolean dbData = getDbData();
                        if (dbData){
                            if (b){  //是否是自动补全
                                //初始化自动补全数据
                                getAutoCompleteData(searchText,listView);
                                searchView.setAutoCompleteAdapter(autoCompleteAdapter);
                            }else {
                               getResultData();

                            }
                        }else {
                            ToastUtils.showLong(SearchActivity.this,"没有相符的结果");
                        }



                    }
                });
        addSubscription(subscription);
    }
    protected void addSubscription(Subscription s)
    {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
//如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
        class MyRecycleViewListener implements CommonViewHolder.onItemCommonClickListener{

            @Override
            public void onItemClickListener(int position) {
                Intent intent=new Intent(SearchActivity.this, ProjectInfoActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("searchActivity", SearchList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(int position) {

            }
        }


}
