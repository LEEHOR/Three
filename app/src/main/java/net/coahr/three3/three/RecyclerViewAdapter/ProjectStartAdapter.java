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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.DBbase.AnswersDB;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.Util.getDrawable;
import net.coahr.three3.three.customView.NineView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.GridLayout.GONE;
import static android.widget.GridLayout.OnClickListener;
import static android.widget.GridLayout.VISIBLE;


/**
 * Created by yuwei on 2018/4/10.
 */

public class ProjectStartAdapter extends BaseRecyclerViewAdapter<SubjectsDB> {

    private static final String TAG = "ProjectStartAdapter";

    public Context mContext;

    private ImageView IB_play, IB_recorder;

    public List<String> list = new ArrayList<>();
    ;     //展示图片

    private TextView Duration, project_recorderTime, start_recorder_time;//播放进度和总时间


    private ImageView source, explain_hide;     //伸展图

    private int isHide = -1;     //题目说明显示隐藏

    private NineView nineView;  //九宫格

    private TextView explain, subjectsTitle;   //题目说明/题目标题

    private ImageButton R_YES, R_NO;  //单选按钮
    private LinearLayout project_Line_no,project_Line_yes;
    private TextView R_yes_t, R_no_t;
    private String recorderPath;

    private SparseArray< String> map;

    private SparseArray< String> mapN;

    private SparseArray< String> mapR;

    private SparseArray< String> mapRemark;


    private SparseArray< String> mapEx;

    private SparseArray map_IB;

    private LinearLayout NlinearLayout, Lineaexplain, project_player_Linea, project_recorder_Linea;

    private ImageView showshuoming;

    private int  isshownshuoming=-1;

    private boolean aBoolean = true;

    private String recorderName;
    //编辑
    private TextView editeTextViw;
    private int isEditText = -1;
    /*录音 seekBar*/
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
    private SubjectsDB mDate;
    private getDrawable drawable;
    private InputMethodManager im;
    public List<BaseViewHolder> myViewHolderList = new ArrayList<>();
    //=================InterFace=========================

    private projectStartOnclickInterFace startOnclickInterFace;
    //private CustomTextWatcher customTextWatcher;

    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public ProjectStartAdapter(Context context, List<SubjectsDB> datas, int layoutId, Intent intent) {
        super(context, datas, layoutId, intent);
        this.mContext = context;
        drawable = new getDrawable();
        map_IB = new SparseArray();
        mapN = new SparseArray();
        mapR = new SparseArray();
        map = new SparseArray();
        mapRemark = new SparseArray();
        mapEx = new SparseArray();
        //  customTextWatcher = new CustomTextWatcher();
        im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        for (int i = 0; i < datas.size(); i++) {

            List<SubjectsDB> subjectsDBList = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class, "ht_id=?", datas.get(i).getHt_id());
            if (subjectsDBList != null && subjectsDBList.size() > 0) {

                String description = subjectsDBList.get(0).getDescription();
             //   Log.e(TAG, "ProjectStartAdapter:ID "+datas.get(i).getId()+description );
                if (description != null) {
                    mapEx.put(datas.get(i).getId(), description);
                } else {
                    mapEx.put(datas.get(i).getId(), null);

                }
                List<AnswersDB> answers = subjectsDBList.get(0).getAnswers();
                if (answers != null && answers.size() > 0) {

                    String answer = answers.get(0).getAnswer();
                    if (answer != null) {
                        map.put(datas.get(i).getId(), answer);
                    } else {
                        map.put(datas.get(i).getId(), null);
                    }
                    String remakes = answers.get(0).getRemakes();
                    if (remakes != null) {
                        if (remakes.equals("")) {
                            mapRemark.put(datas.get(i).getId(), null);
                        } else {

                            mapRemark.put(datas.get(i).getId(), remakes);
                        }
                    } else {
                        mapRemark.put(datas.get(i).getId(), null);
                    }
                } else {
                    map.put(datas.get(i).getId(), null);
                    mapRemark.put(datas.get(i).getId(), null);
                }
            } else {
                mapEx.put(datas.get(i).getId(), null);
                map.put(datas.get(i).getId(), null);
                mapRemark.put(datas.get(i).getId(), null);
            }
          //  Log.e(TAG, "ProjectStartAdapter:题目ID "+datas.get(i).getId() + map.get(datas.get(i).getId()) + "/备注" + mapRemark.get(datas.get(i).getId())+"问题说明"+mapEx.get(datas.get(i).getId()));
        }

        // Log.e(TAG, "ProjectStartAdapter: "+datas.get(0).getId()+"/备注"+mapRemark.get(0) );
    }

    public void setImageList(List<String> list) {

        this.list = list;
    }

    public List<BaseViewHolder> getMyViewHolderList(){
        return  this.myViewHolderList;
    }

    public void setMap(SparseArray< String> map, int position) {
        this.map = map;
        notifyItemChanged(position);
    }

    public void setMapRemark(SparseArray< String> map, int position) {
        this.mapRemark = map;
        notifyItemChanged(position);
    }

    public void setIsEditText(int isEditText, int position) {
        this.isEditText = isEditText;
        notifyItemChanged(position);
    }

    public SparseArray< String> getRemarkMap() {
        return mapRemark;
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

   /* public void setMapImage(Map<Integer,Boolean> mapImage,int position){
        this.mapImage=mapImage;

        notifyItemChanged(position);
    }*/

    @Override
    public void bindData(final BaseViewHolder holder, final SubjectsDB data, final int position) {
        //判断list里面是否含有该holder，没有就增加
        //因为list已经持有holder的引用，所有数据自动会改变
        if(!(myViewHolderList.contains(holder))){
            myViewHolderList.add(holder);
        }
        /*题目选择类布局*/
        this.mDate = data;
        String resourcesUri = drawable.getResourcesUri(mContext, R.drawable.projectcamera);
        subjectsTitle = (TextView) holder.getView(R.id.project_start_subjectTitle); //题目标题
        R_YES = (ImageButton) holder.getView(R.id.project_image_select1);  //单选按钮YES
        R_NO = (ImageButton) holder.getView(R.id.project_image_select2);     //单选按钮NO
        project_Line_yes= (LinearLayout) holder.getView(R.id.project_Line_yes); //单选按钮YES
        project_Line_no= (LinearLayout) holder.getView(R.id.project_Line_no); //单选按钮no
        R_yes_t = (TextView) holder.getView(R.id.project_tv_select1);//单选按钮YES文字
        R_no_t = (TextView) holder.getView(R.id.project_tv_select2);//单选按钮NO文字
        /*说明类布局*/
        explain = (TextView) holder.getView(R.id.project_start_explain);//题目说明
       // explain_hide = (ImageView) holder.getView(R.id.project_start_iv); //控制题目说明显示隐藏
        Lineaexplain = (LinearLayout) holder.getView(R.id.project_start_Lineaexplain);  //题目说明父类
        editeTextViw = (TextView) holder.getView(R.id.project_start_userinstruction);//访问员说明
        showshuoming= (ImageView) holder.getView(R.id.project_show_userinstruction);
        editeTextViw.setTag(data.getId());
        //控制录音布局
        //播放Linea
        project_player_Linea = (LinearLayout) holder.getView(R.id.project_player_Linea);
        //录音Linea
        project_recorder_Linea = (LinearLayout) holder.getView(R.id.project_recorder_Linea);
        /*录音布局*/
        IB_play = (ImageView) holder.getView(R.id.project_playrecord);  //录音播放按钮
        seekBar = (SeekBar) holder.getView(R.id.projectStartAdapter_seekBar);
        project_recorderTime = (TextView) holder.getView(R.id.project_recorderTime);//播放计时时间
        Duration = (TextView) holder.getView(R.id.project_Duration);   //播放总时长
        /*录音*/
        IB_recorder = (ImageView) holder.getView(R.id.start_recorder); //开始录音按钮
        start_recorder_time = (TextView) holder.getView(R.id.start_recorder_time);  //录音计时
        start_recorder_time.setTag(data.getId());

        /*图片布局*/
       // control_Nlinear = (RelativeLayout) holder.getView(R.id.control_NineLinearLayout); //控制图片显示隐藏
        source = (ImageView) holder.getView(R.id.project_start_sources); //箭头按钮图片
        NlinearLayout = (LinearLayout) holder.getView(R.id.NineLinearLayout);//九宫格图片父布局
        nineView = (NineView) holder.getView(R.id.project_start_NineView); //九宫格图片

        /*数据注入*/

        //得到题目列
        final SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, (data.getId()));
        //得到选项
        String options = SubjectsDB.getOptions();

        //得到图片
        final List<ImagesDB> imagesDBList = SubjectsDB.getImagesDBList();

        if (imagesDBList != null && imagesDBList.size() > 0 && !imagesDBList.isEmpty()) {
            list.clear();
            list.add(resourcesUri);
            for (int i = 0; i < imagesDBList.size(); i++) {
                File file=new File(imagesDBList.get(i).getZibImagePath());
                if (file.isFile()){
                    Log.d(TAG, "bindData:图片路径 "+imagesDBList.get(i).getZibImagePath());
                    list.add(imagesDBList.get(i).getZibImagePath());
                }
            }
        } else {
            list.clear();
            list.add(resourcesUri);
        }

        //获取录音文件
        List<RecorderFilesDB> recorderFiles = SubjectsDB.getRecorderFiles();
        if (recorderFiles != null && !recorderFiles.isEmpty() && recorderFiles.size() > 0) {
            RecorderFilesDB recorderFilesDB = recorderFiles.get(0);
            if (recorderFilesDB != null)
            //得到录音文件地址
            {
                recorderPath = recorderFilesDB.getRecorderPath();
                recorderName = recorderFilesDB.getRecorderName();
                if (recorderPath !=null && recorderName !=null){
                    File file=new File(recorderPath);
                    if(file.isFile()){
                        mapR.put(data.getId(), recorderPath);
                        mapN.put(data.getId(), recorderName);
                    } else {
                        mapN.put(data.getId(), null);
                        mapR.put(data.getId(), null);
                    }
                } else {
                    mapN.put(data.getId(), null);
                    mapR.put(data.getId(), null);
                }

            } else {
                // recorderPath=null;
                mapN.put(data.getId(), null);
                mapR.put(data.getId(), null);
            }
        } else {
            mapN.put(data.getId(), null);
            mapR.put(data.getId(), null);
        }

        // Rg= (RadioGroup) holder.getView(R.id.projectstartadapter_Rg);
        if (mapR !=null && mapR.get(data.getId())!=null){
            project_player_Linea.setVisibility(VISIBLE);
            project_recorder_Linea.setVisibility(GONE);
        }else {
            project_player_Linea.setVisibility(GONE);
            project_recorder_Linea.setVisibility(VISIBLE);
        }
        //录音文件名
       /* if (mapN!=null && mapN.get(data.getId())!=null){
            project_recorderTime.setText(mapN.get(data.getId()));
        }else {
            project_recorderTime.setText("00:00");
        }*/
        /*指标*/
        String Title = (position + 1) + ". ";
        String quota1 = data.getQuota1();
        if (quota1 != null) {
            Title = Title + " [" + quota1 + "]";
            String quota2 = data.getQuota2();
            if (quota2 != null) {
                Title = Title + " [" + quota2 + "]";
                String quota3 = data.getQuota3();
                if (quota3 != null) {
                    Title = Title + " [" + quota3 + "]";
                }

            }
        }
        subjectsTitle.setText(Title + " " + data.getTitle());

        /*选择*/
        if (options != null) {
            String[] strs = options.split("&");

            if (strs != null && strs.length > 0) {
                for (int i = 0; i < strs.length; i++) {
                    if (i == 0) {
                        R_yes_t.setText(strs[i]);

                    } else if (i == 1) {
                        R_no_t.setText(strs[i]);
                    }

                }
            } else {
                R_yes_t.setText("是");
                R_no_t.setText("否");
            }
        } else {
            R_yes_t.setText("是");
            R_no_t.setText("否");
        }

        //设置答案

        if (map != null && map.get(data.getId()) != null) {
            if (map.get(data.getId()).equals(R_yes_t.getText())) {

                R_YES.setBackgroundResource(R.drawable.icon_image_select);
                R_NO.setBackgroundResource(R.drawable.icon_image_un_select);
                // R_NO.setChecked(false);
               // Log.e(TAG, "题目答案/:" + "是");
            }
            if (map.get(data.getId()).equals(R_no_t.getText())) {
                // R_YES.setChecked(false);
                R_NO.setBackgroundResource(R.drawable.icon_image_select);
                R_YES.setBackgroundResource(R.drawable.icon_image_un_select);
               // Log.e(TAG, "题目答案" + "否");
            }
        } else {

            R_YES.setBackgroundResource(R.drawable.icon_image_un_select);
            R_NO.setBackgroundResource(R.drawable.icon_image_un_select);
        }

        //设置问题说明

        if (mapEx != null && mapEx.get(data.getId()) != null) {
           // Log.e(TAG, "bindData1: "+mapEx.get(data.getId()) );
            Lineaexplain.setVisibility(VISIBLE);
            explain.setVisibility(VISIBLE);
            explain.setText(mapEx.get(data.getId()));
        } else {
           // Log.e(TAG, "bindData2: "+mapEx.get(data.getId()) );
            Lineaexplain.setVisibility(GONE);
            explain.setVisibility(GONE);
            explain.setText("无");
        }

        /*  设置备注    */
        if (mapRemark != null) {
            if (mapRemark.get(data.getId()) != null) {
                editeTextViw.setText(mapRemark.get(data.getId()));
            } else {
                editeTextViw.setText(mapRemark.get(data.getId()));
                editeTextViw.setHint("点击编辑");
            }
          //  Log.e(TAG, "bindData:editeTextViw " + editeTextViw.getText());
        } else {
            editeTextViw.setHint("点击编辑");
        }
        if (isshownshuoming==position){
            editeTextViw.setEllipsize(null); // 展开
            editeTextViw.setSingleLine(false);
        }else {
            editeTextViw.setLines(3);
            editeTextViw.setEllipsize(TextUtils.TruncateAt.END);
        }
        showshuoming.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isshownshuoming==holder.getAdapterPosition()){
                    isshownshuoming=-1;
                    notifyItemChanged(holder.getAdapterPosition());
                }else {
                    int oldshowshuoming=isshownshuoming;
                    isshownshuoming=holder.getAdapterPosition();
                    notifyItemChanged(oldshowshuoming);
                    notifyItemChanged(isshownshuoming);
                }
            }
        });

        //设置图片
     /*  control_Nlinear.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (startOnclickInterFace!=null) {
                   startOnclickInterFace.control_N(NlinearLayout,mapImage, data.getId(), position);
               }

           }
       });*/

        if (list != null && list.size() > 0) {
            nineView.setIsShowAll(true);
            nineView.setSpacing(6);
            nineView.setUrlList(list);

        } else {
            // list.clear();
            nineView.setUrlList(list);
        }


     /*   if ( mapImage !=null && mapImage.get(data.getId())){
            NlinearLayout.setVisibility(VISIBLE);
            Log.d(TAG, "bindData: 显示/"+mapImage.get(data.getId()));
        }else {
            Log.d(TAG, "bindData: 隐藏/"+mapImage.get(data.getId()));
            NlinearLayout.setVisibility(GONE);
           
        }*/
     //播放录音


        nineView.setOnClickNineView(new NineView.OnClickNineView() {
            @Override
            public void OnClickImages(int mposition, String url, List<String> urlList, ImageView imageView) {
                if (startOnclickInterFace != null) {
                    startOnclickInterFace.NineViewOnClick(mposition, position, url, urlList, imageView, imagesDBList, mContext);
                }

            };

            @Override
            public void OnLongClickImages(int mposition, String url, List<String> urlList, ImageView imageView) {
                if (startOnclickInterFace != null) {
                    startOnclickInterFace.NineViewOnLongClick(mposition, position, url, urlList, imageView, imagesDBList, mContext);
                }
            }
        });
        //设置题目


        project_Line_yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                R_NO.setBackgroundResource(R.drawable.icon_image_un_select);
                if (startOnclickInterFace != null) {
                    startOnclickInterFace.R_YES(data.getId(), map, R_yes_t.getText().toString(), position);
                }
            }
        });
        project_Line_no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                R_YES.setBackgroundResource(R.drawable.icon_image_un_select);
                if (startOnclickInterFace != null) {
                    startOnclickInterFace.R_NO(data.getId(), map, R_no_t.getText().toString(), position);
                }
            }
        });

        editeTextViw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startOnclickInterFace !=null){
                    startOnclickInterFace.EditeText(position,mapRemark,data.getId());
                }
            }
        });


        //录音按钮
        IB_recorder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                   if (startOnclickInterFace !=null){
                       startOnclickInterFace.startRecorder(position,IB_recorder);
                   }

            }
        });


        //=============设置时长============//
        if (mapR != null && mapN != null && mapR.get(data.getId()) != null && mapN.get(data.getId()) != null) {
            seekBar.setProgress(0);
            project_recorderTime.setText("00:00:00");
            Uri uri = Uri.parse(mapR.get(data.getId()));
            MediaPlayer player = MediaPlayer.create(mContext, uri);
            int duration = player.getDuration();
            ((TextView) holder.getView(R.id.project_Duration)).setText(" / " + FileUtils.msToss(duration)); //时长");
          //  Log.e(TAG, "bindData: " + "进度---");
        } else {
            project_recorderTime.setText("00:00:00");
            ((TextView) holder.getView(R.id.project_Duration)).setText(" / " + "00:00:00"); //时长
          //  Log.e(TAG, "bindData: " + "进度===");
            seekBar.setProgress(0);
        }
            IB_play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playPosition == holder.getLayoutPosition()) {  //当前的item上的IB_play是否已被点击
                        playPosition = -1;
                      //  Log.e(TAG, "bindData: " + "点击1");
                        notifyItemChanged(holder.getLayoutPosition());
                    } else {
                        int oldPlayPosition = playPosition;
                        playPosition = holder.getLayoutPosition();
                      //  Log.e(TAG, "bindData: " + "点击2");
                        notifyItemChanged(oldPlayPosition);
                        notifyItemChanged(playPosition);
                    }
                }
            });

        if (playPosition == position) {  //开始播放录音
            if (mapR != null && mapN != null && mapR.get(data.getId()) != null && mapN.get(data.getId()) != null) {
             //   Log.e(TAG, "bindData: " + "开始播放录音1");

                if (mediaPlayer.isPlaying()) {
                 //   Log.e(TAG, "bindData: " + "开始播放录音2");
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
                    map_IB.put(data.getId(), position);
                   // Log.e(TAG, "bindData: " + "开始播放录音3");
                    seekBaring = true;
                    Uri uri = Uri.parse(mapR.get(data.getId()));
                    mediaPlayer = MediaPlayer.create(mContext, uri);
                    seekBar.setMax(mediaPlayer.getDuration());
                  //  Log.e(TAG, "onClick: " + mediaPlayer.getDuration());
                    mediaPlayer.start();
                    seekBar.setTag(data.getId());

                    if (map_IB.get(data.getId()) != null && ((int) map_IB.get(mDate.getId())) == position) {
                        timer = new Timer();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg != null) {
                                    String s = FileUtils.msToss(msg.arg1);
                                    if (map_IB.get(data.getId()) != null && ((int) map_IB.get(mDate.getId())) == position) {
                                        project_recorderTime.setText(s);
                                    //    Log.e(TAG, "handleMessage: 1");
                                    } else {
                                        project_recorderTime.setText("00:00:00");
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
                                            if (map_IB.get(data.getId()) != null && ((int) map_IB.get(mDate.getId())) == position) {
                                              //  Log.e(TAG, "run: " + currentPosition);
                                                seekBar.setProgress(currentPosition);
                                              //  Log.e(TAG, "run: " + "进度1");
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
                        project_recorderTime.setText("00:00:00");
                        seekBar.setProgress(0);
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = true;
                            if (map_IB.get(data.getId()) != null && (int) map_IB.get(data.getId()) == position) {
                                seekBar.setProgress(0);
                                project_recorderTime.setText("00:00:00");
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
                  //  Log.e(TAG, "onClick:isPlaying2 " + isPlaying);
                }
            } else {                                   //没有录音
                if (mediaPlayer.isPlaying()) {
                  //  Log.e(TAG, "bindData: " + "停止播放1");
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
            //    Log.e(TAG, "bindData: " + "停止播放4");
            }
        } else {

            if (mediaPlayer.isPlaying()) {
               // Log.e(TAG, "bindData: " + "停止播放2");
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
          //  Log.e(TAG, "bindData: " + "停止播放3");
        }
        // seekBar.setOnSeekBarChangeListener(new seekBarListener());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
               // Log.e(TAG, "onProgressChanged: ");
                if (map_IB.get(data.getId()) != null && (int) map_IB.get(data.getId()) == position) {
                    if (fromUser) {
                      //  Log.e("1111", "fromUser: 1");
                      //  Log.e("1111", "fromUser: 111111");
                        if (mediaPlayer.isPlaying()) {
                         //   Log.e("1111", "fromUser: isisisisis");
                        } else {
                         //   Log.e("1111", "fromUser: nonononono");
                        }
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);

                    } else {
                     //   Log.e("1111", "fromUser: 2");

                        if (mediaPlayer.isPlaying()) {
                          //  Log.e("1111", "fromUser: 2222");
                            seekBar.setProgress(progress);
                        } else {
                            seekBar.setProgress(0);
                         //   Log.e("1111", "fromUser: 3333");
                        }
                    }
                } else {
                    seekBar.setProgress(0);
                    return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
               // Log.e("1111", "onStartTrackingTouch: ");
                if (map_IB.get(data.getId()) != null && (int) map_IB.get(data.getId()) == position) {
                    if (mediaPlayer.isPlaying()) {
                      //  Log.e("1111", "onStartTrackingTouch: " + "暂停");
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
            //    Log.e(TAG, "onStopTrackingTouch: ");
                if (map_IB.get(data.getId()) != null && (int) map_IB.get(data.getId()) == position) {
                //    Log.e("1111", "onStartTrackingTouch: " + "开始播放1");
                    if (mediaPlayer.isPlaying()) {
                    //    Log.e("1111", "onStartTrackingTouch: " + "开始播放2");
                        // mediaPlayer.start();
                    }
                    mediaPlayer.start();
                } else {
                    seekBar.setProgress(0);
                    return;
                }

            }

        });


    }

  /*  private class CustomTextWatcher implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.e(TAG, "afterTextChanged: " + s.toString());
            SubjectsDB SubjectsDB = DataBaseWork.DBSelectById(SubjectsDB.class, datas.get(position).getId());
            List<AnswersDB> answersdb = SubjectsDB.getAnswers();
            if (answersdb != null && answersdb.size() > 0 && !answersdb.isEmpty()) {  //如果存在就更新
                answersdb.get(0).setSubjectsDB(SubjectsDB);
                answersdb.get(0).setRemakes(s.toString());
                answersdb.get(0).update(answersdb.get(0).getId());
                mapRemark.put(datas.get(position).getId(), s.toString());

            } else {                                            //不存在就保存
                AnswersDB answersDB = new AnswersDB();
                answersDB.setRemakes(s.toString());
                answersDB.setSubjectsDB(SubjectsDB);
                answersDB.save();
                mapRemark.put(datas.get(position).getId(), s.toString());
            }
            // notifyItemChanged(position);
        }
    }*/

   /* class seekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
            Log.e(TAG, "onProgressChanged: "+mDate.getId()+  "   /   "+ IB_play.getTag());
            if (fromUser){
                Log.e(TAG, "fromUser: 1");
                    Log.e(TAG, "fromUser: 111111");
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);

            }else {
                Log.e(TAG, "fromUser: 2");
            if (((int)IB_play.getTag())==mDate.getId()) {
                if (mediaPlayer.isPlaying()) {
                    if (((int) seekBar.getTag()) == mDate.getId()) {
                        Log.e(TAG, "onProgressChanged:1:////"+progress);
                        seekBar.setProgress(progress);
                    } else {
                        seekBar.setProgress(0);
                        Log.e(TAG, "onProgressChanged: 2");
                    }
                } else {
                    seekBar.setProgress(0);
                }
            }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e(TAG, "onStartTrackingTouch: " );

                if (mediaPlayer.isPlaying()){
                    Log.e(TAG, "onStartTrackingTouch: "+"暂停" );
                    mediaPlayer.pause();
                }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e("1111", "onStopTrackingTouch: ");

                Log.e("1111", "onStartTrackingTouch: "+"开始播放1" );
                if (mediaPlayer.isPlaying()){
                    Log.e("1111", "onStartTrackingTouch: "+"开始播放2" );
                    mediaPlayer.start();
                }
              //  mediaPlayer.start();



          //  mediaPlayer.start();

        }
    }*/

    //按钮回调事件
    public  interface  projectStartOnclickInterFace{
        void R_YES(int id,SparseArray<String> sparseArray,String answer,int position);
        void R_NO(int id,SparseArray<String> sparseArray,String answer,int position);
        void NineViewOnClick(int NinePosition,int position, String url, List<String> urlList, ImageView imageView,List<ImagesDB> imagesDBList,Context context);
        void NineViewOnLongClick(int NinePosition,int position, String url, List<String> urlList, ImageView imageView,List<ImagesDB> imagesDBList,Context context);
        void startRecorder(int nowposition,ImageView iv);
        void EditeText(int position,SparseArray<String> sparseArray,int id);
    }

    public void setProjectStartOnclick(projectStartOnclickInterFace startOnclick){
        this.startOnclickInterFace=startOnclick;
    }
}
