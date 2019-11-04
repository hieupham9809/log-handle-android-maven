package com.zingtv.logshowjava.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.zingtv.logshowjava.utility.LogHelper;
import com.zingtv.logshowjava.R;
import com.zingtv.logshowjava.view.DragLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebviewFragment extends Fragment {

    private DragLayout dragLayout;

    private WebView webView;
    private Button loadBtn;

    private String logFilePath = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WebviewFragment() {
        // Required empty public constructor
    }


    public static WebviewFragment newInstance() {
        WebviewFragment fragment = new WebviewFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        dragLayout = view.findViewById(R.id.drag_layout);
        webView = view.findViewById(R.id.webview);
        loadBtn = view.findViewById(R.id.load_btn);
//        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("file://" + LogHelper.getLogsFile(getContext()).getAbsolutePath());
        webView.scrollBy(0, webView.getContentHeight() * 5);

        setWriteNewLogListener();


        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LogHelper.hasListener()){
                    Log.d("ZINGLOGSHOW", "unset listener");
                    Toast.makeText(getContext(),"unset listener",Toast.LENGTH_SHORT);
                    LogHelper.unSetWriteNewLogListener();
                } else {
                    if (logFilePath != null){
                        Log.d("ZINGLOGSHOW", "log file available, load html "+logFilePath);
                        Toast.makeText(getContext(),"log file available, load html",Toast.LENGTH_SHORT);

                        webView.loadUrl("file://"+logFilePath);
                        webView.scrollBy(0, webView.getContentHeight() * 5);

                        setWriteNewLogListener();
                    } else {
                        Log.d("ZINGLOGSHOW", "log file null");
                    }
                }
            }
        });
        return view;
    }


    private void setWriteNewLogListener(){
        LogHelper.setWriteNewLogListener(new LogHelper.WriteNewLogListener() {
            @Override
            public void onWriteNewLog(String path) {
                logFilePath = path;
                Log.d("ZINGLOGSHOW", "content height "+ webView.getContentHeight());
                try {
                    webView.loadUrl("file://"+path);

                    webView.scrollBy(0, webView.getContentHeight() * 5);
//                    webView.scrollBy(0, 1);
//                    webView.setWebViewClient(new WebViewClient() {
//                        @Override
//                        public void onPageFinished(WebView view, String url) {
//                            Log.d("ZINGLOGSHOW", "view height"+view.getHeight());
////                            h.postDelayed(scrollRunnable, 17L);
//
//                            //use the param "view", and call getContentHeight in scrollTo
////                            view.scrollTo(0, 500);
//                        }
//                    });

                } catch (Exception e){
                    Log.d("ZINGLOGSHOW", e.getMessage());
                }
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
