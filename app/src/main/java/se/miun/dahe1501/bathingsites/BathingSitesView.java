package se.miun.dahe1501.bathingsites;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Dave on 2017-05-08.
 * Custom View class used in the BathingSites Activity and the landscape
 * mode of NewBathingSites-activity
 */

public class BathingSitesView extends RelativeLayout {

    private int bathingCount;

    public BathingSitesView(Context context) {
        super(context);
        initialViews(context);
    }

    public BathingSitesView(Context context, AttributeSet attrs){
        super(context,attrs);
        initialViews(context);
    }

    public BathingSitesView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        initialViews(context);
    }

    private void initialViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            inflater.inflate(R.layout.bathingsitesview,this);
        } else {
            inflater.inflate(R.layout.bathingsitesview, this);
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /* Sets and displays the current number of bathingsites based on input param */
    public void setBathingSites(int count){
        TextView bathOut = (TextView)findViewById(R.id.txtBathingSites);

        bathOut.setText(count + " " + getResources().getString(R.string.app_name));
    }

    public int getBathingSites(){
        return bathingCount;
    }
}
