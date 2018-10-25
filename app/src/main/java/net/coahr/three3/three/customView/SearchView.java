package net.coahr.three3.three.customView;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.coahr.three3.three.R;


/**
 * Created by yetwish on 2015-05-11
 */

public class SearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;

    /**
     * 删除键
     */
    private ImageView ivDelete;

    /**
     * 确认按钮
     */
    private ImageView ivSubmit;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 弹出列表
     */
    private ListView lvTips;

    /**
     * 提示adapter （推荐adapter）
     */
    private ArrayAdapter<String> mHintAdapter;

    /**
     * 自动补全adapter 只显示名字
     */
    private ArrayAdapter<String> mAutoCompleteAdapter;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    private String TAG="SearchView";
    /**
     * 返回按钮
     */
    private TextView Tv_cancel;
    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_searchview, this);
        initViews();
    }

    private void initViews() {
        etInput = findViewById(R.id.search_et_input);
        ivDelete= findViewById(R.id.search_iv_delete);
        ivSubmit=findViewById(R.id.search_iv_submit);
        lvTips =  findViewById(R.id.search_lv_tips);
        Tv_cancel=  findViewById(R.id.Tv_cancel);


        lvTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //设置输入框文字
                String text = lvTips.getAdapter().getItem(i).toString();
                etInput.setText(text);
                //光标移动到最后
                etInput.setSelection(text.length());
                //当点击自动补全item将会消失，同时结果栏出现
                lvTips.setVisibility(View.GONE);
                //通知监听器开始搜索
                notifyStartSearching(text);
            }
        });

        ivDelete.setOnClickListener(this);
        ivSubmit.setOnClickListener(this);
        Tv_cancel.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    lvTips.setVisibility(GONE);
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     * @param text
     */
    private void notifyStartSearching(String text){
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置热搜版提示 adapter
     */
    public void setTipsHintAdapter(ArrayAdapter<String> adapter) {
        this.mHintAdapter = adapter;
        if (lvTips.getAdapter() == null) {
            lvTips.setAdapter(mHintAdapter);
        }
    }

    /**
     * 设置自动补全adapter
     */
    public void setAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        this.mAutoCompleteAdapter = adapter;
    }

    private class EditChangedListener implements TextWatcher {
        /**
         * 文本变化之前
         * @param charSequence
         * @param i
         * @param i2
         * @param i3
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        /**
         * 文本变化中
         * @param charSequence
         * @param i
         * @param i2
         * @param i3
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
           // mListener.onTipsItemClick(charSequence,lvTips,ivDelete,mListener,mAutoCompleteAdapter,mHintAdapter);
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                lvTips.setVisibility(VISIBLE);
                if (mAutoCompleteAdapter != null && lvTips.getAdapter() != mAutoCompleteAdapter) {
                    lvTips.setAdapter(mAutoCompleteAdapter);
                }
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onRefreshAutoComplete(charSequence + "",lvTips);
                }


            } else {
                ivDelete.setVisibility(GONE);
                if (mHintAdapter != null) {
                    lvTips.setAdapter(mHintAdapter);
                }
                lvTips.setVisibility(GONE);
            }

        }

        /**
         * 文本变化之后
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_et_input:
                lvTips.setVisibility(VISIBLE);
                break;
            case R.id.search_iv_delete:
                etInput.setText("");
              ivDelete.setVisibility(GONE);
                break;
            case R.id.search_iv_submit:
                lvTips.setVisibility(GONE);
                String s = etInput.getText().toString();
                    notifyStartSearching(s);
                break;
            case R.id.Tv_cancel:
                if (mListener != null) {
                    mListener.PreviousPage();
                }
                break;
        }
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {

        /**
         * 更新自动补全内容
         *
         * @param text 传入补全后的文本
         */
        void onRefreshAutoComplete(String text,ListView livTip);

        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);

       /**
         * 提示列表项点击时回调方法 (提示/自动补全)
         */
        //void onTipsItemClick(CharSequence text);


        /**
         * 返回按钮监听
         */
        void   PreviousPage();
    }

}

