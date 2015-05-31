package org.twizer.android.ui.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.twitter.sdk.android.Twitter;

import org.twizer.android.R;
import org.twizer.android.io.prefs.PreferenceAssistant;
import org.twizer.android.ui.activity.LoginActivity;

import java.util.List;
import java.util.Locale;

/**
 * @author stoyicker.
 */
public final class SettingsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final Activity activity = getActivity();
        final Context context = activity.getApplicationContext();

        final Preference trendLocationPreference = findPreference(context.getString(R.string.pref_key_trend_location));
        trendLocationPreference.setOnPreferenceClickListener(preference -> {
            toggleTwitterTrendLocation(context, preference);
            return Boolean.TRUE;
        });

        trendLocationPreference.setSummary(PreferenceAssistant.readSharedString(context, context.getString(R.string.pref_key_trend_location), context.getString(R.string.trend_location_id_world)).contentEquals(context.getString(R.string.trend_location_id_world)) ? context.getString(R.string.pref_summary_trend_location_world) : context.getString(R.string.pref_summary_trend_location_local));

        findPreference(context.getString(R.string.pref_key_about_the_author))
                .setOnPreferenceClickListener(preference -> {
                    showMyLinkedInProfile(context);
                    return Boolean.TRUE;
                });

        findPreference(context.getString(R.string.pref_key_see_the_source))
                .setOnPreferenceClickListener(preference -> {
                    showGitHubRepository(context);
                    return Boolean.TRUE;
                });


        findPreference(context.getString(R.string.pref_key_log_out)).setOnPreferenceClickListener(preference -> {
            confirmLogOut(activity);
            return Boolean.FALSE;
        });

    }

    private void toggleTwitterTrendLocation(final Context context, final Preference preference) {
        final String prefKey, worldId;
        final Boolean isWorld = PreferenceAssistant.readSharedString(context, prefKey = context.getString(R.string.pref_key_trend_location), worldId = context.getResources().getString(R.string
                .trend_location_id_world)).contentEquals(worldId);
        preference.setSummary(isWorld ? R.string.pref_summary_trend_location_local : R.string.pref_summary_trend_location_world);
        PreferenceAssistant.writeSharedString(context, prefKey, isWorld ? context.getResources().getString(R.string.trend_location_id_local) : worldId);
    }

    private void confirmLogOut(final Activity activity) {
        new MaterialDialog.Builder(activity)
                .content(R.string.confirm_log_out)
                .negativeText(android.R.string.cancel)
                .positiveText(R.string.pref_title_log_out)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        logOut(activity);
                    }
                })
                .autoDismiss(Boolean.TRUE)
                .show();
    }

    private void logOut(final Activity activity) {
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
        final Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityCompat.finishAfterTransition(activity);
        //noinspection unchecked
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    private void showGitHubRepository(final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R
                .string.github_repository_url)));
        startActivity(intent);
    }

    private void showMyLinkedInProfile(final Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.ENGLISH,
                context.getString(R.string.linkedin_intent_pattern), context.getString(R.string
                        .author_linkedin_id))));
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string
                    .about_the_author_link)));
        }
        startActivity(intent);
    }
}
