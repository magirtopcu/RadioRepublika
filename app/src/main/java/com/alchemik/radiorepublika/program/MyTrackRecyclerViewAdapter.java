package com.alchemik.radiorepublika.program;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.model.Track;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Track} and makes a call to the
 * specified {@link TrackListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTrackRecyclerViewAdapter extends RecyclerView.Adapter<MyTrackRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = MyTrackRecyclerViewAdapter.class.getSimpleName();
    private final List<Track> mValues;
    private final TrackListFragment.OnListFragmentInteractionListener mListener;
    private Context mContext;

    public MyTrackRecyclerViewAdapter(Context context, List<Track> items, TrackListFragment.OnListFragmentInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position=" + position + ", track= " + mValues.get(position).toString());

        holder.mItem = mValues.get(position);
        boolean isPremier = holder.mItem.isPremiere();
        boolean isLive = holder.mItem.isLive();
        String subtitle = holder.mItem.getTrackSubtitle();
        boolean hasSubtitle = !TextUtils.isEmpty(subtitle);

        long currentTime = System.currentTimeMillis();
        long currentItemStartTime = holder.mItem.getStartDateTime();
        long currentItemEndTime = holder.mItem.getEndDateTime();
        long nextItemStartTime = 0;
        if (position < mValues.size() - 1) {
            nextItemStartTime = mValues.get(position+1).getStartDateTime();
        }
        Log.d(TAG, "onBindViewHolder: currentTime=" + currentTime + ", currentItemStartTime=" + currentItemStartTime + ", nextItemStartTime=" + nextItemStartTime);

        String type = "";
        int backgroundColor = R.color.schedule_background_neutral;
        int textColor = android.R.color.white;
        if (isPremier) {
            type = "PREMIERA";
            backgroundColor = R.color.schedule_background_premier;
        } else if (isLive) {
            type = "NA ŻYWO";
            backgroundColor = R.color.schedule_background_live_darker;
        }
        int visible = View.VISIBLE;
        if (!isLive && !isPremier && !hasSubtitle) {
            visible = View.GONE;
        }
        if (currentItemStartTime < currentTime && currentTime < currentItemEndTime) {
            textColor = R.color.font_green;
        }

        holder.mTypeView.setBackgroundColor(ContextCompat.getColor(mContext, backgroundColor));
        holder.mStartTimeView.setText(holder.mItem.getStartTime());
        holder.mStartTimeView.setTextColor(ContextCompat.getColor(mContext, textColor));
        holder.mTitleView.setText(holder.mItem.getTrackTitle());
        holder.mTitleView.setTextColor(ContextCompat.getColor(mContext, textColor));
        holder.mTypeView.setText(type);
        holder.mSubtitleView.setText(subtitle);
        holder.mTypeView.setVisibility(visible);
        holder.mSubtitleView.setVisibility(visible);
        holder.mTitleView.setSelected(true);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, holder.mItem.getTrackTitle(), Toast.LENGTH_SHORT).show();
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.showMessage(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mStartTimeView;
        public final TextView mTitleView;
        public final TextView mTypeView;
        public final TextView mSubtitleView;
        public Track mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mStartTimeView = (TextView) view.findViewById(R.id.news_start_time_tv);
            mTitleView = (TextView) view.findViewById(R.id.news_title_tv);
            mTypeView = (TextView) view.findViewById(R.id.news_type_tv);
            mSubtitleView = (TextView) view.findViewById(R.id.news_subtitle_tv);

            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Oswald.ttf");
            mTitleView.setTypeface(font);
            mSubtitleView.setTypeface(font);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mStartTimeView=" + mStartTimeView +
                    ", mTitleView=" + mTitleView +
                    ", mTypeView=" + mTypeView +
                    ", mSubtitleView=" + mSubtitleView +
                    ", mItem=" + mItem +
                    '}';
        }
    }
}
