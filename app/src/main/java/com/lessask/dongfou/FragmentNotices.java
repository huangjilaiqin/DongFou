package com.lessask.dongfou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.dongfou.dialog.LoadingDialog;
import com.lessask.dongfou.model.Notice;
import com.lessask.dongfou.net.GsonRequest;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.DbHelper;
import com.lessask.dongfou.util.TimeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNotices.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNotices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNotices extends Fragment {
    private String TAG = FragmentNotices.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private RecyclerViewStatusSupport mRecyclerView;
    private NoticesAdapter mRecyclerViewAdapter;
    private Context context;
    private LoadingDialog loadingDialog;

    // TODO: Rename and change types of parameters
    private int versionCode;

    private OnFragmentInteractionListener mListener;

    public FragmentNotices() {
        // Required empty public constructor
    }

    private final int LOAD_NOTICES = 1;
    private Handler handler = new Handler(){
        SportRecord sportRecord;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_NOTICES:
                    List<Notice> notices = (List<Notice>) msg.obj;
                    for(int i=0;i<notices.size();i++) {
                        mRecyclerViewAdapter.appendToTop(notices.get(i));
                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    loadingDialog.dismiss();
                    break;
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentNotices.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNotices newInstance(Context context,int versionCode) {
        FragmentNotices fragment = new FragmentNotices();
        Bundle args = new Bundle();
        args.putInt("versionCode", versionCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            versionCode = getArguments().getInt("versionCode");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_notices, container, false);
        mRecyclerView = (RecyclerViewStatusSupport) view.findViewById(R.id.show_list);
        mRecyclerView.setStatusViews(view.findViewById(R.id.loading_view), view.findViewById(R.id.empty_view), view.findViewById(R.id.error_view));
        //用线性的方式显示listview
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerViewAdapter = new NoticesAdapter(getContext());
        mRecyclerViewAdapter.setCurrentVersionCode(versionCode);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                /*
                Sport sport = mRecyclerViewAdapter.getItem(position);
                intent.putExtra("sportid", sport.getId());
                setResult(RESULT_OK, intent);
                finish();
                */
            }
        });
        mRecyclerView.showLoadingView();
        List<Notice> notices = getNotices();
        int lastNoticeId = 0;
        if(notices.size()>0){
            lastNoticeId  = notices.get(0).getId();
        }
        loadNotices(lastNoticeId);
        mRecyclerViewAdapter.appendToList(notices);
        mRecyclerViewAdapter.notifyDataSetChanged();
        return view;
    }

    private List getNotices(){
        List<Notice> notices = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        Cursor cr = db.rawQuery("select * from t_notice order by id desc", null);
        while (cr.moveToNext()) {
            // table t_notice(id integer primary key,kind integer not null,title text not null,status integer not null default 0,time integer not null,url text not null,arg1 integer,arg2 text)");
            int id = cr.getInt(0);
            int kind = cr.getInt(1);
            String title = cr.getString(2);
            int status = cr.getInt(3);
            Date time = new Date(cr.getInt(4)*1000l);
            String url = cr.getString(5);
            int arg1 = cr.getInt(6);
            String arg2 = cr.getString(7);
            Notice notice = new Notice(id,title,kind,status,time,url,arg1,arg2);
            notices.add(notice);
        }
        cr.close();
        return notices;
    }

    private void loadNotices(final int lastId){
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        Type type = new TypeToken<ArrayListResponse<Notice>>() {}.getType();
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, Config.loadNotices, type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {
                loadingDialog.show();
            }
            @Override
            public void onResponse(ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    Toast.makeText(FragmentNotices.this.getContext(), response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    ArrayList<Notice> datas = response.getDatas();
                    //更新
                    Message msg = new Message();
                    msg.what = LOAD_NOTICES;
                    msg.obj = datas;
                    handler.sendMessage(msg);
                    //入库
                    for (int i=0;i<datas.size();i++) {
                        ContentValues values = new ContentValues();
                        Notice notice = datas.get(i);

                        values.put("id", ""+notice.getId());
                        values.put("kind", ""+notice.getKind());
                        values.put("status", ""+notice.getStatus());
                        values.put("title", notice.getTitle());
                        values.put("time", ""+notice.getTime().getTime()/1000);
                        values.put("url", notice.getUrl());
                        values.put("arg1", notice.getArg1()+"");
                        values.put("arg2", notice.getArg2());
                        //long id = dbInstance.insert("t_sport_record", null, values);
                        DbHelper.getInstance(context).insert("t_notice", null, values);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, ""+error.getMessage());
                Log.e(TAG, ""+error.getCause());
                Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("id", lastId+"");
                return datas;
            }
        });
        gsonRequest.setGson(TimeHelper.gsonWithNodeDate());
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
