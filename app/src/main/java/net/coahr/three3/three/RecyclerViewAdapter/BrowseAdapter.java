package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
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
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.Model.VerifyInfoDetailModel;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.GlideCache.GlideApp;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Verify.BrowseActivity;
import net.coahr.three3.three.customView.BrowseImageView;
import net.coahr.three3.three.customView.CustomImageView;

import java.io.File;
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

public class BrowseAdapter extends BaseRecyclerViewAdapter {
    private String TAG="BrowseAdapter";
    private View customView;
    private boolean  temp = true;
    private int index;
    public Context  mContext;
//    public List<ImageView>     mImgList;
    public List<ImageView>     mDeleteImgList;
//    public List<ImageView>     mDeleteIconList;
    public boolean  mFlag;//长按falg
    private ShowDeleteViewInterface showDeleteViewInterface;
    public void setCustomView(View view) {
        this.customView = view;
    }
    private int num ;//测试, list个人,将来是数据源长度
    private boolean verifyFlag;
    private SeekBar mSeekBar;
    private ImageView  mPlay;
    private BrowseAdapter.IB_PlayListenerInterFace ib_playListener;
    private TextView questionTextView , descriptionTextView,instructionTextView,lDurations,playpoiont;
    private ImageButton answerBtn1 , answerBtn2;
    private TextView answerBtn1_t,answerBtn2_t;
    private Map<String,String> map;
    private RadioButton  RadioButton1,RadioButton2;
    private String recorderPath,RN;
    private boolean aBoolean=true;
    private Map<String,String> mapR;
    private Map<String,String> mapRemark;
    private Map<String,String> mapEx;
    private RequestOptions requestOptions;
    /*录音 seekBar*/
    private List<Integer> playPointList = new ArrayList<>(); //播放标记集合
    private SparseArray map_IB;
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
        void OnPlayRecord(int position,String recorderName,String recoderFile,int Point);
    }
    public void setIB_PlayListenerInterFace(BrowseAdapter.IB_PlayListenerInterFace listenerInterFace){
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
    public BrowseAdapter(Context context, List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext = context;
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        mapR=new HashMap<String, String>();
        map=new HashMap<>();
        mapRemark=new HashMap<>();
        mapEx=new HashMap<>();
        map_IB=new SparseArray();
        if (datas !=null && !datas.isEmpty() && datas.size()>0){
            for (int i = 0; i < datas.size(); i++) {
                SubjectListModel.QuestionListBean model = (SubjectListModel.QuestionListBean) datas.get(i);
                List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", model.getId());
                Log.d(TAG, "BrowseAdapter: +"+subjectsDBList.get(0).getNumber());
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
                        if (answer!=null){
                            map.put(model.getId(),answer);
                        }else {
                            map.put(model.getId(),null);
                        }
                        String remakes = answers.get(0).getRemakes();
                        if (remakes !=null){
                            mapRemark.put(model.getId(),remakes);
                        }else {
                            mapRemark.put(model.getId(),null);
                        }
                    }else {
                        map.put(model.getId(),null);
                        mapRemark.put(model.getId(),null);
                    }
                    if (recorderFiles !=null && recorderFiles.size()>0){
                        String recorderPath = recorderFiles.get(0).getRecorderPath();
                        String recorderName = recorderFiles.get(0).getRecorderName();
                        if (recorderPath !=null){
                            if (new File(recorderPath).isFile()){
                                mapR.put(model.getId(),recorderPath);
                            }else {
                                mapR.put(model.getId(),null);
                            }
                        }else {
                            mapR.put(model.getId(),null);
                        }
                    }

                }else{
                        map.put(model.getId(),null);
                        mapR.put(model.getId(),null);
                        mapEx.put(model.getId(),null);
                }
                Log.d(TAG, "BrowseAdapter:map +"+map.get(model.getId())+mapR.get(model.getId()));
            }
        }


        ((BrowseActivity)this.mContext).setDeleteImageInterface(new BrowseActivity.DeleteImageInterface() {
            @Override
            public void delete(int position , View v , List imageList) {

//                mImgList.removeAll(mDeleteImgList);
                removeImg(mDeleteImgList , position , imageList);
                v.setVisibility(INVISIBLE);
                mFlag = false;
            }
        });

    }

    @Override
    public void bindData(BaseViewHolder holder, Object data, final int position) {
        System.out.println("---------------" + position);
        SubjectListModel.QuestionListBean model = (SubjectListModel.QuestionListBean) datas.get(position);
       // final SubjectListModel.QuestionListBean model = (SubjectListModel.QuestionListBean) data;
        questionTextView = (TextView) holder.getView(R.id.question);
        descriptionTextView = (TextView) holder.getView(R.id.browse_explain);
        instructionTextView = (TextView) holder.getView(R.id.browse_userinstruction);
        answerBtn1 = (ImageButton) holder.getView(R.id.browse_image_select1);
        answerBtn2 = (ImageButton) holder.getView(R.id.browse_image_select2);
        answerBtn1_t = (TextView) holder.getView(R.id.browse_tv_select1);
        answerBtn2_t = (TextView) holder.getView(R.id.browse_tv_select2);
        //  lDurations= (TextView) holder.getView(R.id.lDurations);
        mPlay = (ImageView) holder.getView(R.id.play_recorder);
        /*播放*/
        playpoiont = (TextView) holder.getView(R.id.point_play);
        mSeekBar = (SeekBar) holder.getView(R.id.shenhe_seekBar);
        lDurations = (TextView) holder.getView(R.id.TvDurations);

        ((BrowseActivity) this.mContext).setFcous(new BrowseActivity.EditFcous() {
            @Override
            public void fcous() {

            }
        });
        questionTextView.setText((position + 1) + "." + isEmptyOrNull(model) + model.getTitle());
        if (mapEx != null && mapEx.get(model.getId()) != null) {
            descriptionTextView.setText(mapEx.get(model.getId()));
        } else {
            descriptionTextView.setText("无");
        }


        if (mapRemark != null && mapRemark.get(model.getId()) != null) {
            instructionTextView.setText(mapRemark.get(model.getId()));
        } else {
            instructionTextView.setText(mapRemark.get(model.getId()));
            instructionTextView.setHint("点击编辑");
        }


        List<SubjectsDB> lists = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", model.getId());
        List<ImagesDB> imagesDBList = null;
        if (lists != null && lists.size() > 0) {
            // List<RecorderFilesDB> recorderFiles = lists.get(0).getRecorderFiles();
            imagesDBList = lists.get(0).getImagesDBList();
            if (imagesDBList != null && imagesDBList.size() > 0) {
                for (int i = 0; i <imagesDBList.size() ; i++) {
                    if (!new File(imagesDBList.get(i).getImagePath()).isFile()){
                        imagesDBList.remove(i);
                    }
                }
                num = imagesDBList.size();
            } else {
                num = 0;
            }

        } else {
            num = 0;

        }
        String options = model.getOptions();
        if (options != null) {
            String[] strs = options.split("&");
            for (int i = 0; i < strs.length; i++) {
                if (i == 0) {
                    answerBtn1_t.setText(strs[i]);
                } else if (i == 1) {
                    answerBtn2_t.setText(strs[i]);
                } else {
                    answerBtn1_t.setText("是");
                    answerBtn2_t.setText("否");
                }

            }

        } else {
            answerBtn1_t.setText("是");
            answerBtn2_t.setText("否");
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {

            String key = entry.getKey();

            String value = entry.getValue();

            Log.d(TAG, "bindData: "+model.getId()+"key=" + key + " value=" + value);

        }
        if (map != null && map.get(model.getId()) != null) {
            if (map.get(model.getId()).equals(answerBtn1_t.getText().toString())) {
                answerBtn1.setBackgroundResource(R.drawable.icon_image_select);
                answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);
            } else if (map.get(model.getId()).equals(answerBtn2_t.getText().toString())) {
                answerBtn2.setBackgroundResource(R.drawable.icon_image_select);
                answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
            } else {
                answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
                answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);
            }

        } else {
            // Log.e(TAG, "bindData: "+map.get(model.getId()) );
            answerBtn1.setBackgroundResource(R.drawable.icon_image_un_select);
            answerBtn2.setBackgroundResource(R.drawable.icon_image_un_select);
           /* answerBtn1.setEnabled(false);
            answerBtn2.setEnabled(false);*/
        }


        RelativeLayout source = (RelativeLayout) holder.getView(R.id.imgSource);
        ViewGroup view = (ViewGroup) holder.getView(R.id.reviseContent);
        GridLayout gridLayout = (GridLayout) holder.getView(R.id.gridImage);

        holder.data = model;
//        int flag = model.getFlag();
//        if (flag == 0)
//            gridLayout.setVisibility(VISIBLE);
//        else
//            gridLayout.setVisibility(GONE);

        view.removeAllViews();

        for (int i = 0; model.getStageList() != null && i < model.getStageList().size(); i++) {
            System.out.println(position);
            View view1 = addView();
            TextView textView = view1.findViewById(R.id.num);
            TextView verifyType = view1.findViewById(R.id.verifyType);
            TextView content = view1.findViewById(R.id.content);
            TextView name = view1.findViewById(R.id.verifyName);
            VerifyInfoDetailModel.StageBean stageBean = model.getStageList().get(i);
            switch (stageBean.getStage()) {
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
        source.setOnClickListener(new SourceImageClickListenner(gridLayout, holder, position, linearLayout));

        List<ImageView> mImgList = null;
        mImgList = new ArrayList<ImageView>();
        if (mDeleteImgList != null)
            mDeleteImgList.clear();
        mDeleteImgList = null;
        mDeleteImgList = new ArrayList<ImageView>();
        List<CustomImageView> mDeleteIconList = null;
        mDeleteIconList = new ArrayList<CustomImageView>();
        int col = -1;
        int row = -1;
        int maxCol = 3; // 最多3列
        if (num / maxCol != 0) {
            if (num == 4) {
                col = 2;
                row = 2;
            } else if (num == 9) {
                col = 3;
                row = 3;
            } else {
                col = maxCol;
                row = num / maxCol + 1;
            }

        } else if (num / maxCol == 0) {
            col = num;
            row = 1;
        }
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        int margin = 10;
        for (int i = 0; i < num; i++) {

            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            final FrameLayout frameView = new FrameLayout(mContext);

            frameView.setLayoutParams(fp);
            FrameLayout.LayoutParams vlp2 = new FrameLayout.LayoutParams(50, 50);
            FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams((getWindowWidth() - margin * (col * 2)) / col, (getWindowWidth() - margin * (col * 2)) / col);

            final CustomImageView imageView = new CustomImageView(mContext, imagesDBList.get(i).getZibImagePath(), i);
            final CustomImageView deleteView = new CustomImageView(mContext, imagesDBList.get(i).getZibImagePath(), i);
            deleteView.setVisibility(GONE);
            vlp.setMargins(10, 10, 10, 10);
            System.out.println(getWindowWidth());

            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            imageView.setLayoutParams(vlp);
            String imagePath = imagesDBList.get(i).getZibImagePath();
            //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            // Picasso.with(context).load("file://"+imagePath).config(Bitmap.Config.RGB_565).into(imageView);
            GlideApp.with(mContext).asBitmap()
                    .apply(requestOptions)
                    .load(imagePath).into(imageView);
            imageView.setOnClickListener(new BrowseImageListenner(frameView, imageView, position, imagesDBList, mImgList));
            frameView.addView(imageView);
            mDeleteIconList.add(deleteView);
            mImgList.add(imageView);
            if (mDeleteIconList != null && mDeleteIconList.size() > 0) {
                imageView.setOnLongClickListener(new BrowseAdapter.ImageViewLongPressClickListenner(mDeleteIconList, position, mImgList));
            }
            vlp2.gravity = Gravity.RIGHT;
            deleteView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            deleteView.setLayoutParams(vlp2);
            deleteView.setImageResource(R.drawable.awesomeface);
            deleteView.setOnClickListener(new DeleteImageListenner(frameView, imageView, position, mImgList));
            frameView.addView(deleteView);
            gridLayout.addView(frameView);
        }
        holder.position = position;
        //=============设置时长============//
        if (mapR != null && mapR.get(model.getId()) != null) {
            mSeekBar.setProgress(0);
            Uri uri = Uri.parse(mapR.get(model.getId()));
            MediaPlayer player = MediaPlayer.create(mContext, uri);
            int duration = player.getDuration();
            ((TextView) holder.getView(R.id.TvDurations)).setText(" / " + FileUtils.msToss(duration)); //时长");
        } else {
            ((TextView) holder.getView(R.id.TvDurations)).setText(" / " + "00:00:00"); //时长
            mSeekBar.setProgress(0);
        }
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPosition == holder.getLayoutPosition()) {  //当前的item上的IB_play是否已被点击
                    playPosition = -1;
                    notifyItemChanged(holder.getLayoutPosition());
                } else {
                    int oldPlayPosition = playPosition;
                    playPosition = holder.getLayoutPosition();
                    notifyItemChanged(oldPlayPosition);
                    notifyItemChanged(playPosition);
                }
            }
        });
        if (playPosition == position) {  //开始播放录音
            if (mapR != null  && mapR.get(model.getId()) != null ) {

                if (mediaPlayer.isPlaying()) {
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
                    seekBaring = true;
                    Uri uri = Uri.parse(mapR.get(model.getId()));
                    mediaPlayer = MediaPlayer.create(mContext, uri);
                    mSeekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    mSeekBar.setTag(position);

                    if (map_IB.get(position) != null && ((int) map_IB.get(position)) == position) {
                        timer = new Timer();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg != null) {
                                    String s = FileUtils.msToss(msg.arg1);
                                    if (map_IB.get(position) != null && ((int) map_IB.get(position)) == position) {
                                        lDurations.setText(s);
                                    } else {
                                        lDurations.setText("00:00:00");
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
                                                mSeekBar.setProgress(currentPosition);
                                                Message messages = new Message();
                                                messages.arg1 = currentPosition;
                                                handler.sendMessage(messages);
                                            } else {
                                                mSeekBar.setProgress(0);
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
                        playpoiont.setText("00:00:00");
                        mSeekBar.setProgress(0);
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = true;
                            if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                                mSeekBar.setProgress(0);
                                playpoiont.setText("00:00:00");
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
                }
            } else {                                   //没有录音
                if (mediaPlayer.isPlaying()) {
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
            }
        } else {

            if (mediaPlayer.isPlaying()) {
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
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    if (fromUser) {
                        if (mediaPlayer.isPlaying()) {
                        } else {
                        }
                        mediaPlayer.seekTo(progress);
                        mSeekBar.setProgress(progress);

                    } else {

                        if (mediaPlayer.isPlaying()) {
                            mSeekBar.setProgress(progress);
                        } else {
                            mSeekBar.setProgress(0);
                        }
                    }
                } else {
                    mSeekBar.setProgress(0);
                    return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                } else {
                    mSeekBar.setProgress(0);
                    return;
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
                if (map_IB.get(position) != null && (int) map_IB.get(position) == position) {
                    if (mediaPlayer.isPlaying()) {
                    }
                    mediaPlayer.start();
                } else {
                    mSeekBar.setProgress(0);
                    return;
                }

            }

        });


    }

    @Override
    public int getItemCount() {
        if (datas!= null)
            return datas.size();
        else
            return 0;
    }


    public View addView()
    {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.layout_dymic , null);
        return view;
    }

//长按图片
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
    class SourceImageClickListenner implements OnClickListener
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


                SubjectListModel.QuestionListBean bean = (SubjectListModel.QuestionListBean) datas.get(mPosition);
                if (bean.getFlag() == 0)
                {
                    this.mLayout.setVisibility(VISIBLE);
                    bean.setFlag(1);
                }
                else
                {
                    this.mLayout.setVisibility(GONE);
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
                mDeleteImgList.add((ImageView) subView);
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
            final BrowseImageView browseImageView = new BrowseImageView(mContext , imgSources , ((CustomImageView)v).getmIndex());

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
                            removeImg(removeImage , position , imageList);

                            browseImageView.hide();

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
            mImgList.remove(o);
        }
        else if(o instanceof List)
        {
            mImgList.removeAll((Collection<?>) o);
        }
        num = mImgList.size();
        notifyItemChanged(position);

    }

    public String isEmptyOrNull(SubjectListModel.QuestionListBean model)
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

}
