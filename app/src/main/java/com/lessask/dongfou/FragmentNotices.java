package com.lessask.dongfou;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.dongfou.model.Notice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNotices.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNotices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNotices extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerViewStatusSupport mRecyclerView;
    private NoticesAdapter mRecyclerViewAdapter;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentNotices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNotices.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNotices newInstance(Context context,String param1, String param2) {
        FragmentNotices fragment = new FragmentNotices();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        mRecyclerViewAdapter.appendToList(getData());
        mRecyclerViewAdapter.notifyDataSetChanged();
        return view;
    }

    private List getData(){
        List<Notice> notices = new ArrayList<>();
        for (int i=0;i<10;i++){
            notices.add(new Notice(i,"动否1.2."+i+"主要更新", 1,0,new Date(),"https://www.pgyer.com/dongfou"));
        }
        return notices;
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
