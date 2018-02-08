package se.miun.dahe1501.bathingsites;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 * Fragment class used for the Main-activity
 */
public class BathingSitesFragment extends Fragment {

    private View myFragmentView;
    private DatabaseCreator myData;
    private int siteCount;
    BathingSitesView sitesView;

    public BathingSitesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myData = new DatabaseCreator(getActivity());


        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sitesView = (BathingSitesView) view.findViewById(R.id.startView);
        siteCount = myData.getRowCount();
        sitesView.setBathingSites(siteCount);

    }

    @Override
    public void onResume() {
        super.onResume();

        //BathingSitesView sitesView = (BathingSitesView) view.findViewById(R.id.startView);
        siteCount = myData.getRowCount();
        sitesView.setBathingSites(siteCount);
    }

}
