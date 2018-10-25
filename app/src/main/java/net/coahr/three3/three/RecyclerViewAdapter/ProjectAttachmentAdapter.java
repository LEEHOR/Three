package net.coahr.three3.three.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseRecyclerViewAdapter;
import net.coahr.three3.three.BaseAdapter.BaseRecyclerViewAdapter.BaseViewHolder;
import net.coahr.three3.three.DBbase.ImagesDB;
import net.coahr.three3.three.DBbase.RecorderFilesDB;
import net.coahr.three3.three.DBbase.SubjectsDB;
import net.coahr.three3.three.Model.SubjectListModel;
import net.coahr.three3.three.Project.ProjectAttachmentActivity;
import net.coahr.three3.three.R;
import net.coahr.three3.three.Util.AudioRecorder.MediaPlayManage;
import net.coahr.three3.three.Util.GlideCache.GlideApp;
import net.coahr.three3.three.Util.JDBC.DataBaseWork;
import net.coahr.three3.three.Util.OtherUtils.FileUtils;
import net.coahr.three3.three.customView.BrowseImageView;
import net.coahr.three3.three.customView.CustomImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by yuwei on 2018/4/23.
 */

public class ProjectAttachmentAdapter extends BaseRecyclerViewAdapter {

    private  int num = 10;
    private Context  mContext;
    public List<ImageView>     mImgList;
    private LinearLayout mAudio;
    private RelativeLayout mImage;
    private GridLayout   mGridLayout;
    private boolean audioVisiableFlag;
    private boolean imageVisiableFlag = true;
    private boolean grideVisiableFlag;
    private List<ImagesDB> imagesDBList;
    private List<RecorderFilesDB> recorderFiles;
    private ImageView  mPlay;
    private IB_PlayListenerInterFace ib_playListener;
    private Map<String,String> mapR;
    private Map<Integer,List<ImagesDB>> mapI;
    private Map<Integer,Integer> MapN;
    private Map<String,String> mapRn;
    private TextView mlDuration,recorderName;
    private RequestOptions requestOptions;

    //播放录音的回调
    public interface IB_PlayListenerInterFace{
        void OnPlayRecord(int position ,String name,String recorderFile,int PlayPoint);
    }
    public void setIB_PlayListenerInterFace(IB_PlayListenerInterFace listenerInterFace){
        this.ib_playListener=listenerInterFace;

    }

    /**
     * 实例化具体实现
     *
     * @param context  父activity
     * @param datas    加载的数据
     * @param layoutId
     */
    public ProjectAttachmentAdapter(Context context, List datas, int layoutId , Intent intent) {
        super(context, datas, layoutId , intent);
        this.mContext = context;
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        mapR=new HashMap<String, String>();
        mapI=new HashMap<Integer, List<ImagesDB>>();
        MapN=new HashMap<Integer, Integer>();
        mapRn=new HashMap<String, String>();
        ((ProjectAttachmentActivity)context).setmFilterInterFace(new ProjectAttachmentActivity.FilterInterFace() {
            @Override
            public void filter(boolean flag1, boolean flag2, boolean flag3) {
                setVisiable(flag1 , flag2 , flag3);
//                mAudio.setVisibility(GONE);
            }
        });
        if (datas !=null && datas.size()>0){
            for (int i = 0; i <datas.size() ; i++) {
                SubjectListModel.QuestionListBean model = (SubjectListModel.QuestionListBean) datas.get(i);
                if (model!=null){
                    List<SubjectsDB> lists = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class,"ht_id=?",model.getId());
                    if (lists !=null && lists.size()>0){
                       recorderFiles = lists.get(0).getRecorderFiles();
                        imagesDBList = lists.get(0).getImagesDBList();
                        if (imagesDBList !=null && imagesDBList.size()>0){
                            for (int j = 0; j <imagesDBList.size() ; j++) {
                                if (!new File(imagesDBList.get(j).getImagePath()).isFile()){
                                    imagesDBList.remove(j);
                                }
                            }
                            num =  imagesDBList.size();
                            MapN.put(i,num);
                            mapI.put(i,imagesDBList);
                        }
                        else
                        {
                            num = 0;
                            MapN.put(i,num);
                            mapI.put(i,imagesDBList);
                        }

                        if (recorderFiles !=null && recorderFiles.size()>0 )
                        {
                            String recorderPath = recorderFiles.get(0).getRecorderPath();
                            if (recorderPath!=null){
                                if (new File(recorderPath).isFile()){
                                    mapR.put(model.getId(),recorderPath);
                                    mapRn.put(model.getId(),recorderFiles.get(0).getRecorderName());
                                } else {
                                    mapR.put(model.getId(),null);
                                    mapRn.put(model.getId(),null);
                                }
                            }

                        }else {
                            mapR.put(model.getId(),null);
                            mapRn.put(model.getId(),null);
                        }

                    }else
                    {
                        num = 0;

                        MapN.put(i,num);

                        mapI.put(i,imagesDBList);

                        mapR.put(model.getId(),null);

                        mapRn.put(model.getId(),null);
                    }
                }
                if (mapI.get(model.getId()) !=null && mapI.get(model.getId()).size()>0){

                    Log.e("浏览1", "ProjectAttachmentAdapter: "+mapI.get(i).get(i).getZibImagePath() +MapN.get(i));
                }else {
                    Log.e("浏览2", "ProjectAttachmentAdapter: "+mapI.get(i).size() +MapN.get(i) );
                }
            }
        }
    }

    @Override
    public void bindData(BaseViewHolder holder, Object data, final int position) {
      TextView textView= (TextView) holder.getView(R.id.subjectID);
      textView.setText("题目"+(position+1));

        final SubjectListModel.QuestionListBean model = (SubjectListModel.QuestionListBean) datas.get(position);
       // int nums = MapN.get(model.getId());
       /* List<SubjectsDB> lists = DataBaseWork.DBSelectByTogether_Where(SubjectsDB.class,"ht_id=?",model.getId());
        Log.e("附件", "bindData: "+model.getId() );
        if(lists!=null && lists.size()>0){
            List<RecorderFilesDB> recorderFiles = lists.get(0).getRecorderFiles();
            imagesDBList=  lists.get(0).getImagesDBList();
            if (imagesDBList!=null && imagesDBList.size()>0){
                num =  imagesDBList.size();
            }
            else
            {
                num = 0;
            }
          if (recorderFiles!=null && recorderFiles.size()>0){
              recorderPath = recorderFiles.get(0).getRecorderPath();
              mapR.put(model.getId(),recorderPath);
          }else {

              mapR.put(model.getId(),null);
          }

        }
        else
        {
            num = 0;
            mapR.put(model.getId(),null);
        }*/
if (mapI.get(position) !=null && mapI.get(position).size()>0 ){

    Log.e("浏览", "录音: "+mapR.get(position)+mapI.get(position).get(0).getZibImagePath());
}

//        ImageView source = holder.itemView.findViewById(R.id.sources);
        GridLayout gridLayout = holder.itemView.findViewById(R.id.gridImage);
        mGridLayout = gridLayout;
        mAudio =  holder.itemView.findViewById(R.id.audio);

        mImage =holder.itemView.findViewById(R.id.images);

        mAudio.setVisibility(audioVisiableFlag ? VISIBLE : GONE);
        mImage.setVisibility(imageVisiableFlag ? VISIBLE : GONE);
        gridLayout.setVisibility(grideVisiableFlag ? VISIBLE : GONE);

        if (imageVisiableFlag)
        {
            int flag = model.getFlag();
            if (flag ==1)
                gridLayout.setVisibility(VISIBLE);
            else
                gridLayout.setVisibility(GONE);
        }


        mPlay = (ImageView) holder.getView(R.id.play_xiugai);
        //mSeekBar = (SeekBar) holder.getView(R.id.seekBar);
        mlDuration= (TextView) holder.getView(R.id.lDurations); //时长

        recorderName= (TextView) holder.getView(R.id.item_recorderName);
            if (mapRn !=null && mapRn.get(model.getId())!=null){
                recorderName.setText(mapRn.get(model.getId()));
            }else {
                recorderName.setText("暂无");
            }


        gridLayout.removeAllViews();
        mImage.setOnClickListener(new SourceImageClickListenner(gridLayout, holder, position));
        if (mImgList != null)
            mImgList.clear();
        mImgList = null;
        mImgList = new ArrayList<ImageView>();
        int col = -1;
        int row = -1;
        int maxCol = 3; // 最多3列
        if (MapN.get(position) / maxCol != 0) {
            if (MapN.get(position) == 4) {
                col = 2;
                row = 2;
            } else if (MapN.get(position) == 9) {
                col = 3;
                row = 3;
            } else {
                col = maxCol;
                row = MapN.get(position) / maxCol + 1;
            }

        } else if (MapN.get(position) / maxCol == 0) {
            col = MapN.get(position);
            row = 1;
        }
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        int margin = 0;
        for (int i = 0; i < MapN.get(position); i++) {

            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            final FrameLayout frameView = new FrameLayout(mContext);

            frameView.setLayoutParams(fp);
            FrameLayout.LayoutParams vlp2 = new FrameLayout.LayoutParams(50, 50);
            FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams((getWindowWidth() - margin * (col * 2)) / col, (getWindowWidth() - margin * (col * 2)) / col);


            final   CustomImageView imageView = new CustomImageView(mContext ,mapI.get(position).get(i).getZibImagePath() , i);
            Log.e("浏览", "图片: "+mapI.get(position).get(i).getZibImagePath() );
            final   CustomImageView deleteView = new CustomImageView(mContext  ,mapI.get(position).get(i).getZibImagePath() , i);
            vlp.setMargins(5, 5, 5, 5);
            System.out.println(getWindowWidth());

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setLayoutParams(vlp);
            if (mapI !=null && mapI.get(position)!=null && mapI.get(position).size()>0){
                String imagePath = mapI.get(position).get(i).getZibImagePath();
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                GlideApp.with(context).asBitmap()
                        .apply(requestOptions )
                        .load(imagePath)
                        .into(imageView);
            }
            imageView.setOnClickListener(new BrowseImageListenner(frameView, imageView, position , mapI.get(position)));
            frameView.addView(imageView);

//            mDeleteIconList.add(deleteView);
            mImgList.add(imageView);

            gridLayout.addView(frameView);
        }

        if (mapR !=null && mapR.get(model.getId())!=null && mapRn !=null && mapRn.get(model.getId()) !=null) {
            MediaPlayManage.BPrepare(mapR.get(model.getId()));
            int duration = MediaPlayManage.getDuration();
            String times = FileUtils.msToss(duration);
            mlDuration.setText(times);

            mPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ib_playListener != null) {

                            ib_playListener.OnPlayRecord(position,mapRn.get(model.getId()), mapR.get(model.getId()),0);

                    }
                }
            });

        }else {
            mlDuration.setText("00:00:00");
            mPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ib_playListener != null) {

                        ib_playListener.OnPlayRecord(position,null,null,0);

                    }
                }
            });
        }
               /* mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        System.out.println("---------------onProgressChanged-----------");
                        Log.e("", "OnPlayRecord: 播放录音" + "时长：" + MediaPlayManage.getDuration());
                        if (MediaPlayManage.getmMediaPlayer() != null)
                            MediaPlayManage.getmMediaPlayer().seekTo(progress );

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        System.out.println("---------------onStartTrackingTouch-----------");
                        MediaPlayManage.pauseMedia();
                        ((ProjectAttachmentActivity) mContext).setChanging(true);

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        System.out.println("---------------onStopTrackingTouch-----------");
                        Log.e("", "OnPlayRecord: 播放录音" + "时长：" + MediaPlayManage.getCurrentPosition());
                        ((ProjectAttachmentActivity) mContext).setChanging(false);
                        ib_playListener.OnPlayRecord(position, mSeekBar, mapR.get(position),mptime,MediaPlayManage.getCurrentPosition());


                    }
                });*/


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindData((BaseViewHolder) holder, null , position);
    }

//    @Override
//    public int getItemCount() {
//        return datas.size();
//    }


        //查看图片
        class SourceImageClickListenner implements View.OnClickListener
        {
            private GridLayout mLayout;
            private BaseViewHolder mHolder;
            private int            mPosition;
            public SourceImageClickListenner(GridLayout layout , BaseViewHolder holder , int position)
            {
                this.mLayout = layout;
                this.mHolder = holder;
                this.mPosition = position;
            }
            @Override
            public void onClick(View v) {

                SubjectListModel.QuestionListBean bean = (SubjectListModel.QuestionListBean) datas.get(mPosition);
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


    //点击弹窗浏览image
    class BrowseImageListenner implements View.OnClickListener
    {
        private ImageView   subView;
        private FrameLayout parientView;
        private int         position;
        private List        imgSources;
        public BrowseImageListenner(FrameLayout parientView , ImageView subView , int position ,List imgs)
        {
            this.parientView = parientView;
            this.subView = subView;
            this.position = position;
            this.imgSources=imgs;

        }
        @Override
        public void onClick(final View v) {
            final BrowseImageView browseImageView = new BrowseImageView(mContext,imgSources,((CustomImageView)v).getmIndex());
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
//            browseImageView.setDeleteImageInterface(new BrowseImageView.DeleteImageInterface() {
//                @Override
//                public void delete() {
//                    System.out.println("------------------------------");
//                    parientView.removeView(v);
//                    removeImg(v , position);
//
//                    browseImageView.hide();
//
//                }
//            });

            browseImageView.setBackImageInterface(new BrowseImageView.BackImageInterface() {
                @Override
                public void back() {
                    browseImageView.hide();
                }
            });


        }
    }



    public void setVisiable(boolean flag1 , boolean flag2 ,boolean flag3)
    {
        imageVisiableFlag = flag1;
        audioVisiableFlag = flag2;
        grideVisiableFlag = flag3;
        notifyDataSetChanged();
//        mImage.setVisibility(flag1? View.VISIBLE:View.GONE);
//        mAudio.setVisibility(flag2 ? View.VISIBLE:View.GONE);
//        mGridLayout.setVisibility(flag3 ? View.VISIBLE:View.GONE);
    }

    }
