package application.activities.elements;

import application.activities.main_tab_fragments.faces.TrackChangeListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.schalarm_android_app.R;
import application.utils.entitys.MusicTrack;

import java.util.List;

/**
 * Created by FFX20413 on 24.08.2014.
 */
public class AvailableTrackFragment extends DialogFragment {

    public static final String AVAILABLE_TRACKS = "Available Tracks";

    private List<MusicTrack> allAvailableMusicTracks;
    private TrackChangeListener trackChangeListener;

    public AvailableTrackFragment(TrackChangeListener trackChangeListener, List<MusicTrack> allAvailableMusicTracks) {
        this.allAvailableMusicTracks = allAvailableMusicTracks;
        this.trackChangeListener = trackChangeListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(AVAILABLE_TRACKS).setAdapter(new TracksAdapter(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                trackChangeListener.trackHasSelected(allAvailableMusicTracks.get(which));
            }
        }).setPositiveButton(R.string.ok_button, null);
        return builder.create();
    }

    private class TracksAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allAvailableMusicTracks.size();
        }

        @Override
        public Object getItem(int position) {
            return allAvailableMusicTracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null) {
                TextView textView = new TextView(getActivity());
                textView.setText(allAvailableMusicTracks.get(position).getTrackName());
                convertView = textView;
            }
            return convertView;
        }
    }
}
