package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.DBbase.AnswersDB;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Model.VerifyInfoDetailModel;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.GlideCache.GlideApp;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Verify.VerifyReviseActivity;
import net.coahr.three3.three.customView.BrowseImageView;
import net.coahr.three3.three.customView.CustomImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.GridLayout.GONE;
import static android.widget.GridLayout.INVISIBLE;
import static android.widget.GridLayout.OnClickListener;
import static android.widget.GridLayout.VISIBLE;

/**
 * Created by yuwei on 2018/4/10.
 */

public class VerifyReviseAdapter extends BaseRecyclerViewAdapter {
    private View customView;
    private boolean  temp = true;
    private int index;
    public Context  mContext;
    public List<CustomImageView>     mDeleteImgList;

    public boolean  mFlag;//长按falg
    private ShowDeleteViewInterface showDeleteViewInterface;
    public void setCustomView(View view) {
        this.customView = view;
    }
    private int num = 10;//测试, list个人,将来是数据源长度
    private boolean verifyFlag;
    private ImageView  mPlay;
    private String TAG="VerifyReviseAdapter";
    private VerifyReviseAdapter.IB_PlayListenerInterFace ib_playListener;
    private TextView questionTextView , descriptionTextView,TvDurations,playpoiont;
    private TextView instructionTextView;
    private ImageButton answerBtn1 , answerBtn2;
    private LinearLayout revise_yes,revise_no;
    private TextView answerBtn1_t,answerBtn2_t;
    private Map<String,String> map ;
    private VerifyReAdapter mverifyReAdapter;
    private String recorderPath=null;
    private Map<String,String> mapR;
    private Map<String,String> mapRemake;
    private Map<String,String > mapEx;
    private TextView mlDuration,recorderNames,tvs;
    private boolean aBoolean=true;
    private RequestOptions requestOptions;
    private SparseArray map_IB;
    private ImageView showshuoming;
    private int isshuoming =-1;
    //播放
    private List<Integer> playPointList = new ArrayList<>(); //播放标记集合
    private SeekBar seekBar;                  //进度条
    private int playPosition = -1;              //标志
    private boolean isPlaying = true;           //是否可以播放
    private boolean seekBaring = false;          //是否是同一个进度
    private boolean isPause;                    //是否暂停
    private Timer timer;
    private Handler handler;
    private boolean isChanging = false;//是否正在拖拽seekbar
    private MediaPlayer mediaPlayer = new MediaPlayer();

    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }


    //interface------------------------

    public interface ShowDeleteViewInterface
    {
        void show(int position , List imageList);
    }
    public void setShowDeleteViewInterface(ShowDeleteViewInterface showDeleteViewInterface) {
        this.showDeleteViewInterface = showDeleteViewInterface;
    }


    //播放录音的回调
    public interface IB_PlayListenerInterFace{
        void OnPlayRecord(int position ,String name,String recorderFile,int PlayPoint);
    }
    public void setIB_PlayListenerInterFace(VerifyReviseAdapter.IB_PlayListenerInterFace listenerInterFace){
        this.ib_playListener=listenerInterFace;

    }

    //init-----------------------------
    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public VerifyReviseAdapter(Context context, final List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext = context;
        map_IB = new SparseArray();
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        ((VerifyReviseActivity)this.mContext).setDeleteImageInterface(new VerifyReviseActivity.DeleteImageInterface() {
            @Override
            //imageList 要删除的imageview
            public void delete(int position, View v, List imageList) {
                VerifyInfoDetailModel.VerifyDetailBean bean = (VerifyInfoDetailModel.VerifyDetailBean) datas.get(position);
                bean.setFlag(bean.getFlag() == 0 ? 1 : 0);
                removeImg(mDeleteImgList , position , imageList);
                v.setVisibility(INVISIBLE);
                mFlag = false;

            }
        });
        map=new HashMap<>();
        mapR=new HashMap<String, String>();
        mapRemake=new HashMap<String, String>();
        mapEx=new HashMap<String, String>();

        if (datas!=null && datas.size()>0){


        for (int i = 0; i < datas.size(); i++) {

            VerifyInfoDetailModel.VerifyDetailBean model = (VerifyInfoDetailModel.VerifyDetailBean) datas.get(i);
            model.getTitle();
            Log.e(TAG, "VerifyReviseAdapter: "+model.getId() );
            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id = ? ", model.getId());
           if (subjectsDBList!=null && subjectsDBList.size()>0){
                String description = subjectsDBList.get(0).getDescription();
                if (description!=null){
                    mapEx.put(model.getId(),description);
                }else {
                    mapEx.put(model.getId(),null);
                }
                List<AnswersDB> answers = subjectsDBList.get(0).getAnswers();
                List<RecorderFilesDB> recorderFiles = subjectsDBList.get(0).getRecorderFiles();
                if (answers!=null && answers.size()>0){
                    String answer = answers.get(0).getAnswer();
                    if (answer !=null){
                        map.put(model.getId(),answer);
                    }else {
                        map.put(model.getId(),null);
                    }
                    String remakes = answers.get(0).getRemakes();
                    if (remakes !=null){
                        if (remakes.equals("")){
                            mapRemake.put(model.getId(),null);
                        }else {
                            mapRemake.put(model.getId(),remakes);
                        }

                    }else {
                        mapRemake.put(model.getId(),null);
                    }

                }else {
                    map.put(model.getId(),null);
                    mapRemake.put(model.getId(),null);
                }

                if (recorderFiles !=null && recorderFiles.size()>0){
                    String recorderPath = recorderFiles.get(0).getRecorderPath();
                    String recorderName = recorderFiles.get(0).getRecorderName();
                    if (recorderPath!=null){

                        mapR.put(model.getId(),recorderPath);
                    }else {
                        mapR.put(model.getId(),null);
                    }


                }else {
                    mapR.put(model.getId(),null);
                }

            }else{
                map.put(model.getId(),null);
                mapRemake.put(model.getId(),null);
                mapR.put(model.getId(),null);
                mapEx.put(model.getId(),null);

            }
             Log.e(TAG, "BrowseAdapter: "+map.get(model.getId()) );
        }
        }else {


        }
    }
    public void setMap(Map<String,String> map,int position){
        this.map=map;
        notifyItemChanged(position);
    }

    public void setMapRemake(Map<String,String> map,int position){
        this.mapRemake=map;
        notifyItemChanged(position);
    }

    @Override
    public void bindData(BaseViewHolder holder, final Object data, final int position) {
        findUI(holder.itemView);
        System.out.println("---------------"+position);
    //    final EditText editText = holder.itemView.findViewById(R.id.question);
        final VerifyInfoDetailModel.VerifyDetailBean model = (VerifyInfoDetailModel.VerifyDetailBean) datas.get(position);
        final List<SubjectsDB> lists = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class,"ht_id=?",model.getId());
        questionTextView.setText((position+1)+"." + isEmptyOrNull(model)+model.getTitle());
        if (mapEx !=null && mapEx.get(model.getId())!=null){
            descriptionTextView.setText(model.getDescription());

        }else {
            descriptionTextView.setText("无");
        }
        if (mapRemake !=null && mapRemake.get(model.getId())!=null){
            instructionTextView.setText(mapRemake.get(model.getId()));
        }else {
            instructionTextView.setText(mapRemake.get(model.getId()));
            instructionTextView.setHint("点击编辑");
        }

        List<ImagesDB>  imagesDBList = null;
       // List<AnswersDB> answersDBList = null;
        if(lists!=null && lists.size()>0){
            /*图片答案*/
            imagesDBList =  lists.get(0).getImagesDBList();
           // answersDBList = lists.get(0).getAnswers();
            List<RecorderFilesDB> recorderFiles = lists.get(0).getRecorderFiles();
               /* if (answersDBList !=null && answersDBList.size()>0){
                    map.put(model.getId(),answersDBList.get(0).getAnswer());
                }else {
                    map.put(model.getId(),null);
                }*/
              if (imagesDBList!=null && imagesDBList.size()>0){
                  num =  imagesDBList.size();
              } else {
                  num = 0;
              }
          /*录音*/
            /*    if (recorderFiles !=null && recorderFiles.size()>0 ){
                     recorderPath = recorderFiles.get(0).getRecorderPath();
                    recorderName = recorderFiles.get(0).getRecorderName();
                    mapR.put(model.getId(),recorderPath);
                     mapRn.put(model.getId(),recorderName);

                }else {
                    mapR.put(model.getId(),null);
                    mapRn.put(model.getId(),"暂无");
                }*/

            Log.e(TAG, "bindData: "+"ID"+model.getId()+imagesDBList.size()+imagesDBList.toArray() );
        }
        else
        {
            num = 0;
           // mapR.put(model.getId(),null);
           // mapRn.put(model.getId(),"暂无");
        }
       /* //获取录音文件
        List<RecorderFilesDB> recorderFiles = SubjectsDB.getRecorderFiles();
        if (recorderFiles!=null&&!recorderFiles.isEmpty()&& recorderFiles.size()>0){
            RecorderFilesDB recorderFilesDB = recorderFiles.get(0);
            if (recorderFilesDB!=null)
            //得到录音文件地址
            {
                recorderPath = recorderFilesDB.getRecorderPath();
                Log.e(TAG, "bindData:recorderPath "+recorderPath );
            }
        }*/


        mlDuration= (TextView) holder.getView(R.id.lDurations);

        String options = model.getOptions();
        if (options != null) {
            String[] strs = options.split("&");
            if (strs !=null && strs.length>0){

            for (int i = 0 ; i < strs.length ; i ++)
            {
                if (i == 0)
                {
                    answerBtn1_t.setText(strs[i]);

                }
                else if (i == 1)
                {
                    answerBtn2_t.setText(strs[i]);
                }

            }

        }else {
            answerBtn1_t.setText("是");
            answerBtn2_t.setText("否");
        }
        }else {
            answerBtn1_t.setText("是");
            answerBtn2_t.setText("否");
        }

        if (map!=null && map.get(model.getId())!=null){
            if ( map.get(model.getId()).equals(answerBtn1_t.getText().toString())){
                Log.e(TAG, "bindData: "+map.get(model.getId()) );
                answerBtn1.setBackgroundResource(R.drawable.icon_image_select);
                answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);

            } else if ( map.get(model.getId()).equals(answerBtn2_t.getText().toString())){
                answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
                answerBtn2.setBackgroundResource(R.drawable.icon_image_select);
                Log.e(TAG, "bindData: "+map.get(model.getId()) );


            }else {
               // Log.e(TAG, "bindData: "+map.get(position) );
                answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
                answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);
            }
        }else {
            answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
            answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);
        }
        revise_yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mverifyReAdapter !=null){
                    mverifyReAdapter.R_YES(model.getId(),position,map,answerBtn1_t.getText().toString());
                }
            }
        });
        revise_no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mverifyReAdapter !=null){
                    mverifyReAdapter.R_NO(model.getId(),position,map,answerBtn2_t.getText().toString());
                }
            }
        });


        RelativeLayout imgSource = (RelativeLayout) holder.getView(R.id.imgSource);
        ViewGroup view = (ViewGroup) holder.getView(R.id.reviseContent);
         GridLayout gridLayout = (GridLayout) holder.getView(R.id.gridImage);

        holder.data = model;

//        int flag = model.getFlag();
//        if (flag == 0)
//            gridLayout.setVisibility(VISIBLE);
//        else
//            gridLayout.setVisibility(GONE);

        view.removeAllViews();
        for (int i = 0 ; i < model.getStageList().size()  ; i++)
        {
            System.out.println(position);
            View view1 = addView();
            TextView textView = view1.findViewById(R.id.num);
            TextView verifyType = view1.findViewById(R.id.verifyType);
            TextView content = view1.findViewById(R.id.content);
            TextView name  = view1.findViewById(R.id.verifyName);
            VerifyInfoDetailModel.StageBean stageBean = model.getStageList().get(i);
            switch (stageBean.getStage())
            {
                case 1:
                    textView.setText("初审");
                    verifyType.setText("初审打回");
                    name.setText(stageBean.getName());
                    break;
                case 2:
                    textView.setText("复审");
                    verifyType.setText("复审打回");
                    name.setText(stageBean.getName());
                    break;
                case 3:
                    textView.setText("终审");
                    verifyType.setText("终审打回");
                    name.setText(stageBean.getName());
                    break;
            }
            content.setText(stageBean.getSuggestion());
            view.addView(view1);
        }
        gridLayout.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) holder.getView(R.id.audio);
        imgSource.setOnClickListener(new SourceImageClickListenner(gridLayout , holder , position , linearLayout));


        List<ImageView> mImgList = null;
        mImgList = new ArrayList<ImageView>();
        if (mDeleteImgList != null)
            mDeleteImgList.clear();
        mDeleteImgList = null;
        mDeleteImgList = new ArrayList<CustomImageView>();

       List<CustomImageView> mDeleteIconList = null;
        mDeleteIconList = new ArrayList<CustomImageView>();
        int col = -1;
        int row = -1;
        int maxCol = 3; // 最多3列
        if (num / maxCol != 0)
        {
            if (num ==4)
            {
                col = 2;
                row = 2;
            }
            else if(num == 9)
            {
                col = 3;
                row = 3;
            }
            else
            {
                col = maxCol;
                row = num / maxCol +1;
            }

        }
        else if(num / maxCol == 0)
        {
            col = num;
            row = 1;
        }
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        int margin =5;
        if (num>0){

            for (int i = 0 ; i < num ; i++)
            {

                FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT , FrameLayout.LayoutParams.WRAP_CONTENT);
                final FrameLayout frameView = new FrameLayout(mContext);

                frameView.setLayoutParams(fp);
                FrameLayout.LayoutParams vlp2 = new FrameLayout.LayoutParams(50 , 50);
                FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams((getWindowWidth()-margin*(col*2))/col ,(getWindowWidth()-margin*(col*2))/col);

              final   CustomImageView imageView = new CustomImageView(mContext ,imagesDBList.get(i).getZibImagePath() , i);
              final   CustomImageView deleteView = new CustomImageView(mContext , imagesDBList.get(i).getZibImagePath() , i);
                deleteView.setVisibility(GONE);
                vlp.setMargins(10 , 10 , 10 , 10);
                System.out.println(getWindowWidth());

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setLayoutParams(vlp);
            if (imagesDBList!=null && imagesDBList.size()>0){
                String imagePath = imagesDBList.get(i).getZibImagePath();
                GlideApp.with(mContext).asBitmap()
                        .apply(requestOptions)
                        .load(imagePath).into(imageView);

            }
            imageView.setOnClickListener(new BrowseImageListenner(frameView , imageView , position , imagesDBList ,mImgList));
            frameView.addView(imageView);

                mDeleteIconList.add(deleteView);
                mImgList.add(imageView);
                if (mDeleteIconList != null && mDeleteIconList.size() > 0) {

                    imageView.setOnLongClickListener(new ImageViewLongPressClickListenner(mDeleteIconList , position , mImgList));
                }



                vlp2.gravity = Gravity.RIGHT;

                deleteView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                deleteView.setLayoutParams(vlp2);
                deleteView.setImageResource(R.drawable.awesomeface);
                deleteView.setOnClickListener(new DeleteImageListenner(frameView , imageView , position , mImgList));

                frameView.addView(deleteView);


                gridLayout.addView(frameView);
            }
        }
        holder.position = position;
      //  mSeekBar = (SeekBar) holder.getView(R.id.seekBar);
        //设置时长
        instructionTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mverifyReAdapter !=null){
                    mverifyReAdapter.R_introductions(position,model.getId(),mapRemake);
                }
            }
        });
        showshuoming.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isshuoming==holder.getAdapterPosition()){
                    isshuoming=-1;
                    notifyItemChanged(holder.getAdapterPosition());
                }else {
                    int oldisshow=isshuoming;
                    isshuoming=holder.getAdapterPosition();
                    notifyItemChanged(oldisshow);
                    notifyItemChanged(isshuoming);
                }
            }
        });
        if (isshuoming==position){
            instructionTextView.setEllipsize(null); // 展开
            instructionTextView.setSingleLine(false);
        }else {
            instructionTextView.setLines(3);
            instructionTextView.setEllipsize(TextUtils.TruncateAt.END);
        }



        //=============设置时长============//
        if (mapR != null  && mapR.get(model.getId()) != null ) {
            seekBar.setProgress(0);
            TvDurations .setText("00:00:00");
            Uri uri = Uri.parse(mapR.get(model.getId()));
            MediaPlayer player = MediaPlayer.create(mContext, uri);
            int duration = player.getDuration();
            ((TextView) holder.getView(R.id.TvDurations)).setText(" / " + FileUtils.msToss(duration)); //时长");
            Log.e(TAG, "bindData: " + "进度---");
        } else {
            TvDurations.setText("00:00:00");
            ((TextView) holder.getView(R.id.TvDurations)).setText(" / " + "00:00:00"); //时长
            Log.e(TAG, "bindData: " + "进度===");
            seekBar.setProgress(0);
        }
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPosition == holder.getLayoutPosition()) {  //当前的item上的IB_play是否已被点击
                    playPosition = -1;
                    Log.e(TAG, "bindData: " + "点击1");
                    notifyItemChanged(holder.getLayoutPosition());
                } else {
                    int oldPlayPosition = playPosition;
                    playPosition = holder.getLayoutPosition();
                    Log.e(TAG, "bindData: " + "点击2");
                    notifyItemChanged(oldPlayPosition);
                    notifyItemChanged(playPosition);
                }
            }
        });

        if (playPosition == position) {  //开始播放录音
            if (mapR != null  && mapR.get(model.getId()) != null ) {
                Log.e(TAG, "bindData: " + "开始播放录音1");

                if (mediaPlayer.isPlaying()) {
                    Log.e(TAG, "bindData: " + "开始播放录音2");
                   /* mediaPlayer.stop();
                    mediaPlayer.release();
                    seekBaring=false;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (handler !=null){
                        handler=null;
                    }*/
                } else {
                    map_IB.put(position, position);
                    Log.e(TAG, "bindData: " + "开始播放录音3");
                    seekBaring = true;
                    Uri uri = Uri.parse(mapR.get(model.getId()));
                    mediaPlayer = MediaPlayer.create(mContext, uri);
                    seekBar.setMax(mediaPlayer.getDuration());
                    Log.e(TAG, "onClick: " + mediaPlayer.getDuration());
                    mediaPlayer.start();
                    seekBar.setTag(model.getId());

                    if (map_IB.get(position) != null && ((int) map_IB.get(position)) == position) {
                        timer = new Timer();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg != null) {
                                    String s = FileUtils.msToss(msg.arg1);
                                    if (map_IB.get(position) != null && ((int) map_IB.get(position)) == position) {
                                        playpoiont.setText(s);
                                        Log.e(TAG, "handleMessage: 1");
                                    } else {
                                        playpoiont.setText("00:00:00");
                                    }

                                }
                            }
                        };
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (seekBaring) {
                                            int currentPosition = mediaPlayer.getCurrentPosition();
                                            if (map_IB.get(position) != null && ((int) map_IB.get(position)) == position) {
                                                Log.e(TAG, "run: " + currentPosition);
                                                seekBar.setProgress(currentPosition);
                                                Log.e(TAG, "run: " + "进度1");
                                                Message messages = new Message();
                                                messages.arg1 = currentPosition;
                                                handler.sendMessage(messages);
                                            } else {
                                                seekBar.setProgress(0);
                                                Message messages = new Message();
                                                messages.arg1 = 0;
                                                handler.sendMessage(messages);
                                            }

                                        }
                                    }
                                });

                            }
                        }, 0, 1000);
                    } else {
                        TvDurations.setText("00:00:00");
                        seekBar.setProgress(0);
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = true;
                            if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                                seekBar.setProgress(0);
                                TvDurations.setText("00:00:00");
                            }
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            if (handler != null) {
                                handler = null;
                            }
                        }
                    });
                    Log.e(TAG, "onClick:isPlaying2 " + isPlaying);
                }
            } else {                                   //没有录音
                if (mediaPlayer.isPlaying()) {
                    Log.e(TAG, "bindData: " + "停止播放1");
                    mediaPlayer.pause();
                    //  mediaPlayer.release();
                    seekBaring = false;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (handler != null) {
                        handler = null;
                    }
                }
                Log.e(TAG, "bindData: " + "停止播放4");
            }
        } else {

            if (mediaPlayer.isPlaying()) {
                Log.e(TAG, "bindData: " + "停止播放2");
                mediaPlayer.stop();
                //  mediaPlayer.release();
                seekBaring = false;
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (handler != null) {
                    handler = null;
                }
            }
            Log.e(TAG, "bindData: " + "停止播放3");
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
                Log.e(TAG, "onProgressChanged: ");
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    if (fromUser) {
                        Log.e("1111", "fromUser: 1");
                        Log.e("1111", "fromUser: 111111");
                        if (mediaPlayer.isPlaying()) {
                            Log.e("1111", "fromUser: isisisisis");
                        } else {
                            Log.e("1111", "fromUser: nonononono");
                        }
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);

                    } else {
                        Log.e("1111", "fromUser: 2");

                        if (mediaPlayer.isPlaying()) {
                            Log.e("1111", "fromUser: 2222");
                            seekBar.setProgress(progress);
                        } else {
                            seekBar.setProgress(0);
                            Log.e("1111", "fromUser: 3333");
                        }
                    }
                } else {
                    seekBar.setProgress(0);
                    return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("1111", "onStartTrackingTouch: ");
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    if (mediaPlayer.isPlaying()) {
                        Log.e("1111", "onStartTrackingTouch: " + "暂停");
                        mediaPlayer.pause();
                    }
                } else {
                    seekBar.setProgress(0);
                    return;
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
                Log.e(TAG, "onStopTrackingTouch: ");
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    Log.e("1111", "onStartTrackingTouch: " + "开始播放1");
                    if (mediaPlayer.isPlaying()) {
                        Log.e("1111", "onStartTrackingTouch: " + "开始播放2");
                        // mediaPlayer.start();
                    }
                    mediaPlayer.start();
                } else {
                    seekBar.setProgress(0);
                    return;
                }

            }

        });
  /*      if (mapR !=null && mapR.get(model.getId())!=null && mapRn !=null && mapRn.get(model.getId()) !=null) {
            MediaPlayManage.BPrepare(mapR.get(model.getId()));
            int duration = MediaPlayManage.getDuration();
            String s = FileUtils.msToss(duration);
            mlDuration.setText(s);

            mPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        ib_playListener.OnPlayRecord(position, mapRn.get(model.getId()), mapR.get(model.getId()), 0);

                }
            });
        }else {
            mlDuration.setText("00:00:00");

            mPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                      //  ib_playListener.OnPlayRecord(position, null, null, 0);

                }
            });
        }*/
    }



   /* @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        System.out.println("--------------------"+position);
//        System.out.println("bool:"+temp);

//        if ()
        findUI(holder.itemView);
        System.out.println("-----------------onBindViewHolder");
            bindData((BaseViewHolder) holder, null , position);
//
        ((VerifyReviseActivity)this.mContext).setFcous(new VerifyReviseActivity.EditFcous() {
            @Override
            public void fcous() {

            }
        });

        final EditText editText = holder.itemView.findViewById(R.id.instruction);





    }*/

   /* @Override
    public int getItemCount() {
        if (datas!= null)
            return datas.size();
        else
            return 0;
    }*/


    public View addView()
    {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.layout_dymic , null);
        return view;
    }



    class ImageViewLongPressClickListenner implements View.OnLongClickListener
    {
        private  List<CustomImageView> list;
        private int position;
        private List imageList;
        public ImageViewLongPressClickListenner(List list , int position , List imageList)
        {
                this.list = list;
                this.position = position;
                this.imageList = imageList;
        }
        @Override
        public boolean onLongClick(View view) {

            for (CustomImageView img:list) {
                img.setVisibility(VISIBLE);
                img.setImageResource(R.drawable.boom);
                mFlag = true;
                showDeleteViewInterface.show(position , imageList);

            }

            return true;
        }
    }

    //查看图片
    class SourceImageClickListenner implements View.OnClickListener
    {
        private GridLayout mLayout;
        private BaseViewHolder mHolder;
        private int            mPosition;
        private LinearLayout    mAudio;
        public SourceImageClickListenner(GridLayout layout , BaseViewHolder holder , int position , LinearLayout audio)
        {
            this.mLayout = layout;
            this.mHolder = holder;
            this.mPosition = position;
            this.mAudio = audio;

        }
        @Override
        public void onClick(View v) {


                VerifyInfoDetailModel.VerifyDetailBean bean = (VerifyInfoDetailModel.VerifyDetailBean) datas.get(mPosition);
                if (bean.getFlag() == 0)
                {
                    this.mLayout.setVisibility(VISIBLE);
//                this.mAudio.setVisibility(VISIBLE);
                    bean.setFlag(1);
                }
                else
                {
                    this.mLayout.setVisibility(GONE);
//                this.mAudio.setVisibility(GONE);
                    bean.setFlag(0);
                }



        }
    }
//  点击删除图片
    class DeleteImageListenner implements OnClickListener
    {
        private ImageView   subView;
        private FrameLayout parientView;
        private int         position;
        private List<ImageView> mImgList;
        public DeleteImageListenner(FrameLayout parientView , ImageView subView , int position , List imgs)
        {
            this.parientView = parientView;
            this.subView = subView;
            this.position = position;
            this.mImgList = imgs;
        }
        @Override
        public void onClick(View v) {

            if (mFlag)
            {
                ((ImageView)v).setImageResource(R.drawable.awesomeface);
                mDeleteImgList.add((CustomImageView) subView);

                return;
            }

            parientView.removeView(subView);

            removeImg(subView , position , mImgList);
        }
    }

    //点击弹窗浏览image
    class BrowseImageListenner implements OnClickListener
    {
        private ImageView   subView;
        private FrameLayout parientView;
        private int         position;
        private List        imgSources;
        private List        imageList;
        public BrowseImageListenner(FrameLayout parientView , ImageView subView , int position , List imgs , List imageList)
        {
            this.parientView = parientView;
            this.subView = subView;
            this.position = position;
            this.imgSources = imgs;
            this.imageList = imageList;
        }
        @Override
        public void onClick(final View v) {
            if (mFlag)//长按后 , 点击浏览无效
                return;
            final VerifyInfoDetailModel.VerifyDetailBean bean = (VerifyInfoDetailModel.VerifyDetailBean) datas.get(position);

            final BrowseImageView browseImageView = new BrowseImageView(mContext , imgSources ,((CustomImageView)v).getmIndex());

                    browseImageView.show();
// 设置window type
                    Window dialogWindow = browseImageView.getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    dialogWindow.setGravity(Gravity.CENTER);
                    browseImageView.setCanceledOnTouchOutside(true);
                    lp.x = (int) (0);
                    lp.y = (int) (0);
                    lp.width = (int) (getWindowWidth()); // 宽度
                    lp.height = (int) (getWindowHeight()); // 高度
//lp.alpha = 0.7f; // 透明度
                    dialogWindow.setAttributes(lp);

                    browseImageView.getmDeleteBtn().setVisibility(verifyFlag ? INVISIBLE : VISIBLE);
                    browseImageView.setDeleteImageInterface(new BrowseImageView.DeleteImageInterface() {
                        @Override
                        public void delete() {
                            System.out.println("------------------------------");
                            CustomImageView removeImage = (CustomImageView) imageList.get(browseImageView.getmCurrentPage());
                            parientView.removeView(removeImage);
                            bean.setFlag(bean.getFlag() == 0 ? 1 : 0);
                            removeImg(removeImage , position , imageList );

                            browseImageView.hide();
//                            System.out.println(browseImageView.getmCurrentPage());

                        }
                    });


                    browseImageView.setBackImageInterface(new BrowseImageView.BackImageInterface() {
                @Override
                public void back() {
                    browseImageView.hide();
                }
            });

        }
    }



    public void  removeImg(Object o , int position , List mImgList)
    {
        if (o instanceof ImageView)
        {
            Log.e(TAG, "removeImg: "+((CustomImageView) o).getmPath() );
            int i = DataBaseWork.DBDeleteByConditions(ImagesDB.class, "zibimagepath=?", ((CustomImageView) o).getmPath());
            if (i>0){


               /* List<String> list=new ArrayList<>();
                list.add(((CustomImageView) o).getmPath());
                FileDelets.deleteFiles(list);*/
                mImgList.remove(o);

            }

        }
        else if(o instanceof List)
        {
            List<String> list=new ArrayList<>();
            for (Object imgView:(List) o
                 ) {

                DataBaseWork.DBDeleteByConditions(ImagesDB.class, "zibimagepath=?",((CustomImageView)imgView).getmPath() );
              //  list.add(((CustomImageView) o).getmPath());
            }
           // FileDelets.deleteFiles(list);

            mImgList.removeAll((Collection<?>) o);
        }
        num = mImgList.size();
        notifyItemChanged(position);

    }


    public void findUI(View v) {
        questionTextView = v.findViewById(R.id.question);
        descriptionTextView = v.findViewById(R.id.browse_explain);
        instructionTextView = v.findViewById(R.id.browse_userinstruction);
        answerBtn1 = v.findViewById(R.id.browse_image_select1);
        answerBtn2 = v.findViewById(R.id.browse_image_select2);
        answerBtn1_t=v.findViewById(R.id.browse_tv_select1);
        answerBtn2_t=v.findViewById(R.id.browse_tv_select2);
        mPlay = v.findViewById(R.id.play_recorder);
        revise_yes= v.findViewById(R.id.revise_yes);
        revise_no= v.findViewById(R.id.revise_no);
        /*播放*/
        playpoiont= v.findViewById(R.id.point_play);
        seekBar= v.findViewById(R.id.shenhe_seekBar);
        TvDurations=  v.findViewById(R.id.TvDurations);
        showshuoming= v.findViewById(R.id.verify_show_userinstruction);

    }


    public String isEmptyOrNull(VerifyInfoDetailModel.VerifyDetailBean model)
    {


            if(model.getQuota1() == null || model.getQuota1().isEmpty() )
            {
                return "";
            }
            else if( model.getQuota2() == null || model.getQuota2().isEmpty() )
            {
                return "[" + model.getQuota1() + "]";
            }
            else if( model.getQuota3() == null || model.getQuota3().isEmpty() )
            {
                return "[" + model.getQuota1() + "]" + "[" + model.getQuota2() + "]";
            }
            else
            {
                return "[" + model.getQuota1() + "]" + "[" + model.getQuota2() + "]" + "[" + model.getQuota3() + "]";
            }
        }

        public interface VerifyReAdapter{
            void R_YES(String id,int position,Map<String,String> map,String answer);
            void R_NO(String id,int position,Map<String,String> map,String answer);
            void R_introductions(int position,String id,Map map);
        }
        public void setVerifyAdater(VerifyReAdapter listener){
        this.mverifyReAdapter=listener;
        }

}
