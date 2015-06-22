package org.twizer.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.twizer.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class NiceLoadTweetLayout extends FrameLayout {

    private Context mContext;
    private View mTweetView;

    @InjectView(R.id.loadError)
    View mErrorView;

    @InjectView(R.id.progressView)
    View mProgressView;

    private IErrorViewListener mErrorListener;

    public NiceLoadTweetLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_niceloadtweetview, this);
        ButterKnife.inject(this);

        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mErrorListener != null)
                    mErrorListener.onErrorViewClick();
            }
        });
    }

    public synchronized void loadTweet(final Long tweetId, final View controlButton) {
        if (mErrorView.getVisibility() != View.GONE)
            mErrorView.setVisibility(View.GONE);
        if (mProgressView.getVisibility() != View.VISIBLE)
            mProgressView.setVisibility(View.VISIBLE);
        TweetUtils.loadTweet(tweetId, new LoadCallback<Tweet>() {
            @Override
            public synchronized void success(final Tweet tweet) {
                NiceLoadTweetLayout.this.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorView.setVisibility(View.GONE);
                        if (mTweetView != null)
                            removeView(mTweetView);
                        NiceLoadTweetLayout.this.addView(mTweetView = new TweetView(mContext, tweet));
                        controlButton.clearAnimation();
                        controlButton.setEnabled(Boolean.TRUE);
                    }
                });
            }

            @Override
            public synchronized void failure(final TwitterException exception) {
                Log.e("ERROR", exception.getMessage());
                if (mTweetView != null)
                    removeView(mTweetView);
                mTweetView = null;
                NiceLoadTweetLayout.this.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressView.setVisibility(View.GONE);
                        mErrorView.setVisibility(View.VISIBLE);
                        controlButton.clearAnimation();
                        controlButton.setEnabled(Boolean.TRUE);
                    }
                });
            }
        });
    }

    public void setErrorListener(IErrorViewListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    public interface IErrorViewListener {
        void onErrorViewClick();
    }
}