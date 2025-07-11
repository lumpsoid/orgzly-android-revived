package com.orgzly.android.prefs;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.orgzly.R;
import com.orgzly.android.App;
import com.orgzly.android.LocalStorage;
import com.orgzly.org.OrgStatesWorkflow;

import org.eclipse.jgit.transport.URIish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Shared preferences utility class.
 */
public class AppPreferences {
    /* Static members for quick access. */
    private static Set<String> todoKeywords;
    private static Set<String> doneKeywords;

    /* Shared Preferences for states. */
    public static SharedPreferences getStateSharedPreferences(Context context) {
        return context.getSharedPreferences("state", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getReposSharedPreferences(Context context) {
        return context.getSharedPreferences("repos", Context.MODE_PRIVATE);
    }

    public static boolean isDoneKeyword(Context context, String state) {
        return state != null && AppPreferences.doneKeywordsSet(context).contains(state);
    }

    public static AppPreferencesValues getAllValues(Context context) {
        AppPreferencesValues values = new AppPreferencesValues();

        values.defaultPrefsValues = getDefaultSharedPreferences(context).getAll();
        values.statePrefsValues = getStateSharedPreferences(context).getAll();
        values.reposPrefsValues = getReposSharedPreferences(context).getAll();

        return values;
    }

    public static void setDefaultPrefsFromJsonMap(Context context, Map<String, ?> parsedMap) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        setPrefsFromValues(prefs, parsedMap);
    }

    public static JsonElement getDefaultPrefsAsJsonObject(Context context) {
        Gson gson = new Gson();
        return gson.toJsonTree(getDefaultSharedPreferences(context).getAll());
    }

    public static void setAllFromValues(Context context, AppPreferencesValues values) {
        AppPreferences.clearAllSharedPreferences(context);

        setPrefsFromValues(getDefaultSharedPreferences(context), values.defaultPrefsValues);
        setPrefsFromValues(getStateSharedPreferences(context), values.statePrefsValues);
        setPrefsFromValues(getReposSharedPreferences(context), values.reposPrefsValues);
    }

    @SuppressWarnings("unchecked")
    private static void setPrefsFromValues(SharedPreferences prefs, Map<String, ?> values) {
        SharedPreferences.Editor edit = prefs.edit();

        for (String key: values.keySet()) {
            Object value = values.get(key);

            if (value instanceof Boolean) {
                edit.putBoolean(key, (boolean) value);

            } else if (value instanceof Float) {
                edit.putFloat(key, (float) value);

            } else if (value instanceof Integer) {
                edit.putInt(key, (int) value);

            } else if (value instanceof Long) {
                edit.putLong(key, (long) value);

            } else if (value instanceof String) {
                edit.putString(key, (String) value);

            } else if (value instanceof Set) {
                edit.putStringSet(key, (Set) value);

            } else if (value instanceof ArrayList) {
                HashSet<String> set = new HashSet<>((Collection<? extends String>) value);
                edit.putStringSet(key, set);
            }
        }

        edit.apply();
    }

    /**
     * Clears all preferences and sets them to default values.
     */
    public static void setToDefaults(Context context) {
        clearAllSharedPreferences(context);

        App.setDefaultPreferences(context, true);
    }

    private static void clearAllSharedPreferences(Context context) {
        /* Clear default preferences. */
        getDefaultSharedPreferences(context).edit().clear().apply();

        /* Clear state preferences. */
        getStateSharedPreferences(context).edit().clear().apply();
    }


    private static String getS(Context context, @StringRes int keyId, @StringRes int defaultValueId) {
        return getS(context, context.getResources().getString(keyId), defaultValueId);
    }

    private static String getS(Context context, String key, @StringRes int defaultValueId) {
        return getDefaultSharedPreferences(context).getString(
                key,
                context.getResources().getString(defaultValueId));
    }


    private static void setS(Context context, @StringRes int keyId, String value) {
        setS(context, context.getResources().getString(keyId), value);
    }

    private static void setS(Context context, String key, String value) {
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }


    /*
     * User preferences.
     * Default values are taken from string resources (also used by preferences.xml)
     */

    public static boolean isNotesContentDisplayedInList(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_notes_content_displayed_in_list),
                context.getResources().getBoolean(R.bool.pref_default_is_notes_content_displayed_in_list));
    }

    public static boolean isNotesContentDisplayedInSearch(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_notes_content_displayed_in_search),
                context.getResources().getBoolean(R.bool.pref_default_is_notes_content_displayed_in_search));
    }

    public static void isNotesContentDisplayedInSearch(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_is_notes_content_displayed_in_search);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean isNotesContentFoldable(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_notes_content_foldable),
                context.getResources().getBoolean(R.bool.pref_default_is_notes_content_foldable));
    }

    public static boolean contentLineCountDisplayed(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_content_line_count_displayed),
                context.getResources().getBoolean(R.bool.pref_default_content_line_count_displayed));
    }

    public static String prefaceDisplay(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_preface_in_book),
                context.getResources().getString(R.string.pref_default_preface_in_book));
    }

    public static void prefaceDisplay(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_preface_in_book);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static boolean isFontMonospaced(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_font_monospaced),
                context.getResources().getBoolean(R.bool.pref_default_is_font_monospaced));
    }

    public static boolean styleText(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_style_text),
                context.getResources().getBoolean(R.bool.pref_default_style_text));
    }

    public static boolean styledTextWithMarks(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_styled_text_with_marks),
                context.getResources().getBoolean(R.bool.pref_default_styled_text_with_marks));
    }

    public static void styledTextWithMarks(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_styled_text_with_marks);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String notebooksSortOrder(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_notebooks_sort_order),
                context.getResources().getString(R.string.pref_default_notebooks_sort_order));
    }

    public static String notesListDensity(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_notes_list_density),
                context.getResources().getString(R.string.pref_default_notes_list_density));
    }

    public static boolean displayPlanning(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_display_planning),
                context.getResources().getBoolean(R.bool.pref_default_display_planning));
    }

    public static boolean createdAt(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_created_at_added),
                context.getResources().getBoolean(R.bool.pref_default_is_created_at_added));
    }

    public static void createdAt(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_is_created_at_added);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean addIdToNewNotes(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_new_note_add_id),
                context.getResources().getBoolean(R.bool.pref_default_new_note_add_id));
    }

    public static void addIdToNewNotes(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_new_note_add_id);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String createdAtProperty(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_created_at_property),
                context.getResources().getString(R.string.pref_default_created_at_property));
    }

    public static void createdAtProperty(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_created_at_property);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String shareNotebook(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_share_notebook),
                context.getResources().getString(R.string.pref_default_share_notebook));
    }

    public static boolean forceUtf8(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_force_utf8),
                context.getResources().getBoolean(R.bool.pref_default_force_utf8));
    }

    public static boolean notebooksStartFolded(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_notebooks_start_folded),
                context.getResources().getBoolean(R.bool.pref_default_notebooks_start_folded));
    }

    public static boolean newNoteNotification(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_ongoing_notification),
                context.getResources().getBoolean(R.bool.pref_default_ongoing_notification));
    }

    public static String ongoingNotificationPriority(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_ongoing_notification_priority),
                context.getResources().getString(R.string.pref_default_ongoing_notification_priority));
    }

    public static boolean remindersForScheduledEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_use_reminders_for_scheduled_times),
                context.getResources().getBoolean(R.bool.pref_default_use_reminders_for_scheduled_times));
    }

    public static void remindersForScheduledEnabled(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_use_reminders_for_scheduled_times);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean remindersForDeadlineEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_use_reminders_for_deadline_times),
                context.getResources().getBoolean(R.bool.pref_default_use_reminders_for_deadline_times));
    }

    public static void remindersForDeadlineEnabled(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_use_reminders_for_deadline_times);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean remindersForEventsEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_use_reminders_for_event_times),
                context.getResources().getBoolean(R.bool.pref_default_use_reminders_for_event_times));
    }

    public static void remindersForEventsEnabled(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_use_reminders_for_event_times);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean anyNotificationsEnabled(Context context) {
        return (
                showSyncNotifications(context) ||
                newNoteNotification(context) ||
                remindersForScheduledEnabled(context) ||
                remindersForDeadlineEnabled(context) ||
                remindersForEventsEnabled(context)
        );
    }

    public static boolean remindersSound(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_reminders_sound),
                context.getResources().getBoolean(R.bool.pref_default_reminders_sound));
    }

    public static boolean remindersAlarm(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_reminders_alarm),
                context.getResources().getBoolean(R.bool.pref_default_reminders_alarm));
    }

    public static String remindersAlarmTags(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_reminders_alarm_tags),
                context.getResources().getString(R.string.pref_default_reminders_alarm_tags));
    }

    public static boolean remindersLed(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_reminders_led),
                context.getResources().getBoolean(R.bool.pref_default_reminders_led));
    }

    public static boolean remindersVibrate(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_reminders_vibrate),
                context.getResources().getBoolean(R.bool.pref_default_reminders_vibrate));
    }

    public static int remindersSnoozeTime(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_snooze_time),
                context.getResources().getString(R.string.pref_default_snooze_time)));
    }

    public static String remindersSnoozeRelativeTo(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_snooze_type),
                context.getResources().getString(R.string.pref_default_snooze_type));
    }

    public static int reminderDailyTime(Context context) {
        String key = context.getResources().getString(R.string.pref_key_daily_reminder_time);
        return getDefaultSharedPreferences(context).getInt(key,
                context.getResources().getInteger(R.integer.pref_default_daily_reminder_time));
    }

    public static void reminderDailyTime(Context context, int value) {
        String key = context.getResources().getString(R.string.pref_key_daily_reminder_time);
        getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static boolean remindersUseAlarmClockForTodReminders(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_reminders_use_alarm_clock_for_tod_reminders),
                context.getResources().getBoolean(R.bool.pref_default_reminders_use_alarm_clock_for_tod_reminders));
    }

    public static boolean logMajorEvents(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_log_major_events),
                context.getResources().getBoolean(R.bool.pref_default_log_major_events));
    }

    public static void logMajorEvents(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_log_major_events);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean showSyncNotifications(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_show_sync_notifications),
                context.getResources().getBoolean(R.bool.pref_default_show_sync_notifications));
    }

    public static String colorTheme(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_color_theme),
                context.getResources().getString(R.string.pref_default_color_theme));
    }

    public static void colorTheme(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_color_theme);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String lightColorScheme(Context context){
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_light_color_scheme),
                context.getResources().getString(R.string.pref_default_light_color_scheme));
    }

    public static void lightColorScheme(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_light_color_scheme);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String darkColorScheme(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_dark_color_scheme),
                context.getResources().getString(R.string.pref_default_dark_color_scheme));
    }

    public static void darkColorScheme(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_dark_color_scheme);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static boolean ignoreSystemLocale(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_ignore_system_locale),
                context.getResources().getBoolean(R.bool.pref_default_ignore_system_locale));
    }

    public static String bookNameInSearchResults(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_book_name_in_search),
                context.getResources().getString(R.string.pref_default_book_name_in_search));
    }

    public static boolean inheritedTagsInSearchResults(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_display_inherited_tags_in_search_results),
                context.getResources().getBoolean(R.bool.pref_default_display_inherited_tags_in_search_results));
    }

    public static void inheritedTagsInSearchResults(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_display_inherited_tags_in_search_results);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String fontSize(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_font_size),
                context.getResources().getString(R.string.pref_default_font_size));
    }

    public static Set<String> displayedBookDetails(Context context) {
        return getDefaultSharedPreferences(context).getStringSet(
                context.getResources().getString(R.string.pref_key_displayed_book_details),
                new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.displayed_book_details_default))));
    }

    public static void displayedBookDetails(Context context, List<String> value) {
        String key = context.getResources().getString(R.string.pref_key_displayed_book_details);
        getDefaultSharedPreferences(context).edit().putStringSet(key, new HashSet<>(value)).apply();
    }

    public static String separateNotesWithNewLine(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_separate_notes_with_new_line),
                context.getResources().getString(R.string.pref_default_separate_notes_with_new_line));
    }

    public static boolean separateHeaderAndContentWithNewLine(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_separate_header_and_content_with_new_line),
                context.getResources().getBoolean(R.bool.pref_default_separate_header_and_content_with_new_line));
    }

    public static Integer tagsColumn(Context context) {
        return Integer.valueOf(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_tags_column),
                context.getResources().getString(R.string.pref_default_tags_column)));
    }

    public static boolean orgIndentMode(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_org_indent_mode),
                context.getResources().getBoolean(R.bool.pref_default_org_indent_mode));
    }

    public static Integer orgIndentIndentationPerLevel(Context context) {
        return Integer.valueOf(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_org_indent_indentation_per_level),
                context.getResources().getString(R.string.pref_default_org_indent_indentation_per_level)));
    }

    /*
     * Orgzlyignore file
     */

    public static String orgzlyignoreFile(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_orgzlyignore_file),
                context.getResources().getString(R.string.pref_default_orgzlyignore_file));
    }

    /*
     * Click action.
     */

    public static boolean isReverseNoteClickAction(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_reverse_click_action),
                context.getResources().getBoolean(R.bool.pref_default_is_reverse_click_action));
    }

    public static void isReverseNoteClickAction(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_is_reverse_click_action);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Schedule new note.
     */

    public static boolean isNewNoteScheduled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_new_note_scheduled),
                context.getResources().getBoolean(R.bool.pref_default_is_new_note_scheduled));
    }

    /*
     * Schedule time for new note.
     */
    public static boolean isNewNoteTimeScheduled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_new_note_time_scheduled),
                context.getResources().getBoolean(R.bool.pref_default_is_new_note_time_scheduled));
    }

    /*
     * Prepend new note.
     */

    public static boolean isNewNotePrepend(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_new_note_prepend),
                context.getResources().getBoolean(R.bool.pref_default_is_new_note_prepend));
    }

    /*
     * Set to-do for new note.
     */

    public static String newNoteState(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_new_note_state),
                context.getResources().getString(R.string.pref_default_new_note_state));
    }

    public static void newNoteState(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_new_note_state);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * State keywords
     */

    public static String states(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_states),
                context.getResources().getString(R.string.pref_default_states));
    }

    public static void states(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_states);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
        updateStaticKeywords(context);
    }

    /*
     * Note popup buttons
     */

    public static String notePopupActions(Context context, String key) {
        if (key.equals(context.getString(R.string.pref_key_note_popup_buttons_in_book_left))) {
            return getS(context, key, R.string.pref_default_note_popup_buttons_in_book_left);

        } else if (key.equals(context.getString(R.string.pref_key_note_popup_buttons_in_book_right))) {
            return getS(context, key, R.string.pref_default_note_popup_buttons_in_book_right);

        } else if (key.equals(context.getString(R.string.pref_key_note_popup_buttons_in_query_left))) {
            return getS(context, key, R.string.pref_default_note_popup_buttons_in_query_left);

        } else if (key.equals(context.getString(R.string.pref_key_note_popup_buttons_in_query_right))) {
            return getS(context, key, R.string.pref_default_note_popup_buttons_in_query_right);

        } else {
            return "";
        }
    }

    public static void notePopupActions(Context context, String key, String value) {
        setS(context, key, value);
    }

    /** Get first to-do state. */
    public static String getFirstTodoState(Context context) {
        return getFirstState(AppPreferences.todoKeywordsSet(context));
    }

    /** Get first done state. */
    public static String getFirstDoneState(Context context) {
        return getFirstState(AppPreferences.doneKeywordsSet(context));
    }

    private static String getFirstState(Set<String> states) {
        return states.iterator().hasNext() ? states.iterator().next() : null;
    }

    /** Get all to-do states. */
    public static Set<String> todoKeywordsSet(Context context) {
        synchronized (AppPreferences.class) {
            if (todoKeywords == null) {
                updateStaticKeywords(context);
            }

            return todoKeywords;
        }
    }

    /** Get all done states. */
    public static Set<String> doneKeywordsSet(Context context) {
        synchronized (AppPreferences.class) {
            if (doneKeywords == null) {
                updateStaticKeywords(context);
            }
            return doneKeywords;
        }
    }

    /** Parses states preference, which can be slow. */
    public static void updateStaticKeywords(Context context) {
        synchronized (AppPreferences.class) {
            todoKeywords = new LinkedHashSet<>();
            doneKeywords = new LinkedHashSet<>();

            for (OrgStatesWorkflow workflow: new StateWorkflows(states(context))) {
                todoKeywords.addAll(workflow.getTodoKeywords());
                doneKeywords.addAll(workflow.getDoneKeywords());
            }
        }
    }

    /*
     * Lowest priority.
     */

    public static String minPriority(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_min_priority),
                context.getResources().getString(R.string.pref_default_min_priority));
    }

    public static void minPriority(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_min_priority);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * Default priority.
     */

    public static String defaultPriority(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_default_priority),
                context.getResources().getString(R.string.pref_default_default_priority));
    }

    public static void defaultPriority(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_default_priority);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * Fold drawers
     */

    public static boolean drawersFolded(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_drawers_folded),
                context.getResources().getBoolean(R.bool.pref_default_drawers_folded));
    }

    public static void drawersFolded(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_drawers_folded);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Log on time shift
     */

    public static boolean logOnTimeShift(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_log_on_time_shift),
                context.getResources().getBoolean(R.bool.pref_default_log_on_time_shift));
    }

    public static void logOnTimeShift(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_log_on_time_shift);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Set LAST_REPEAT property on time shift
     */

    public static boolean setLastRepeatOnTimeShift(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_set_last_repeat_on_time_shift),
                context.getResources().getBoolean(R.bool.pref_default_set_last_repeat_on_time_shift));
    }

    public static void setLastRepeatOnTimeShift(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_set_last_repeat_on_time_shift);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Open note or book on link/breadcrumbs follow.
     */

    public static String breadcrumbsTarget(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_breadcrumbs_target),
                context.getResources().getString(R.string.pref_default_breadcrumbs_target));
    }

    public static String linkTarget(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_link_target),
                context.getResources().getString(R.string.pref_default_link_target));
    }

    /*
     * Allow inlining images
     */
    public static boolean imagesEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_images_enabled),
                context.getResources().getBoolean(R.bool.pref_default_images_enabled));
    }

    public static boolean imagesScaleDownToWidth(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_images_scale_down_to_width),
                context.getResources().getBoolean(R.bool.pref_default_images_scale_down_to_width));
    }

    public static int imagesScaleDownToWidthValue(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_images_scale_down_to_width_value),
                context.getResources().getString(R.string.pref_default_images_scale_down_to_width_value)));
    }

    /*
     * File relative path.
     */

    /** Root for file:/ links */
    public static String fileAbsoluteRoot(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_file_absolute_root),
                context.getResources().getString(R.string.pref_default_file_absolute_root));
    }

    /** Root for file: links */
    public static String fileRelativeRoot(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_file_relative_root),
                LocalStorage.storage(context)
        );
    }

    /*
     * Note's metadata visibility
     */

    public static boolean noteMetadataFolded(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_note_metadata_folded),
                context.getResources().getBoolean(R.bool.pref_default_note_metadata_folded));
    }

    public static void noteMetadataFolded(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_note_metadata_folded);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String noteMetadataVisibility(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_note_metadata_visibility),
                context.getResources().getString(R.string.pref_default_note_metadata_visibility));
    }

    public static void noteMetadataVisibility(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_note_metadata_visibility);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static boolean alwaysShowSetNoteMetadata(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_always_show_set_note_metadata),
                context.getResources().getBoolean(R.bool.pref_default_always_show_set_note_metadata));
    }

    public static void alwaysShowSetNoteMetadata(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_always_show_set_note_metadata);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static Set<String> selectedNoteMetadata(Context context) {
        return getDefaultSharedPreferences(context).getStringSet(
                context.getResources().getString(R.string.pref_key_selected_note_metadata),
                new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.pref_default_selected_note_metadata))));
    }

    /*
     * Content folding state in note details
     */

    public static boolean isNoteContentFolded(Context context) {
        return getStateSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_note_content_folded),
                context.getResources().getBoolean(R.bool.pref_default_is_note_content_folded));
    }

    public static void isNoteContentFolded(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_is_note_content_folded);
        getStateSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Folding in search
     */

    public static boolean isSearchFoldable(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_is_search_foldable),
                context.getResources().getBoolean(R.bool.pref_default_is_search_foldable));
    }

    /*
     * Keep screen on menu item
     */

    public static boolean keepScreenOnMenuItem(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_keep_screen_on_menu_item),
                context.getResources().getBoolean(R.bool.pref_default_keep_screen_on_menu_item));
    }

    /*
     * Hide empty days in agenda
     */

    public static boolean hideEmptyDaysInAgenda(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_hide_empty_days_in_agenda),
                context.getResources().getBoolean(R.bool.pref_default_hide_empty_days_in_agenda));
    }

    /*
     * Widget
     */

    public static String widgetColorScheme(Context context) {
        String scheme = getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_widget_color_scheme),
                context.getResources().getString(R.string.pref_default_widget_color_scheme));

        // FIXME: Return scheme found in preferences if it's supported, or the first one if not.
        String[] schemes = context.getResources().getStringArray(R.array.widget_color_scheme_values);
        for (String s : schemes) {
            if (s.equals(scheme)) {
                return scheme;
            }
        }

        return schemes[0];
    }

    public static int widgetOpacity(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_widget_opacity),
                context.getResources().getString(R.string.pref_default_widget_opacity)));
    }

    public static int widgetFontSize(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_widget_font_size),
                context.getResources().getString(R.string.pref_default_widget_font_size)));
    }

    public static boolean widgetDisplayCheckmarks(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_widget_display_checkmarks),
                context.getResources().getBoolean(R.bool.pref_default_widget_display_checkmarks));
    }

    public static int widgetUpdateFrequency(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_widget_update_frequency),
                context.getResources().getString(R.string.pref_default_widget_update_frequency)));
    }

    public static boolean widgetDisplayBookName(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_widget_display_book_name),
                context.getResources().getBoolean(R.bool.pref_default_widget_display_book_name));
    }

    /*
     * RichTextView
     */

    public static boolean highlightEditedRichText(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_highlight_edited_rich_text),
                context.getResources().getBoolean(R.bool.pref_default_highlight_edited_rich_text));
    }

    /*
     * State flags and values.
     * They have no default values, they are not set by user.
     */

    /*
     * Dropbox token.
     */

    public static String dropboxSerializedCredential(Context context) {
        String key = context.getResources().getString(R.string.pref_key_dropbox_credential);
        return getStateSharedPreferences(context).getString(key, null);
    }

    public static void dropboxSerializedCredential(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_dropbox_credential);
        SharedPreferences.Editor editor = getStateSharedPreferences(context).edit();
        if (value == null) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.apply();
    }

    /*
     * Git Sync
     */

    public static String gitSshKeyType(Context context) {
        return getDefaultSharedPreferences(context).getString(
                "pref_key_git_ssh_key_type", null);
    }

    public static void gitSshKeyType(Context context, String value) {
        getDefaultSharedPreferences(context).edit().putString("pref_key_git_ssh_key_type", value).apply();
    }

    public static boolean gitIsEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_git_is_enabled),
                context.getResources().getBoolean(R.bool.pref_default_git_is_enabled));
    }

    public static void gitIsEnabled(Context context, Boolean value) {
        getDefaultSharedPreferences(context).edit().putBoolean(context.getResources().getString(R.string.pref_key_git_is_enabled), value).apply();
    }
    
    public static String defaultRepositoryStorageDirectory(Context context) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return getStringFromSelector(
                context, R.string.pref_key_git_default_repository_directory, path.toString());
    }

    public static String repositoryStoragePathForUri(Context context, Uri repoUri)  {
        String directoryFilename = repoUri.toString();
        try {
            directoryFilename = new URIish(directoryFilename).getPath();
        } catch (URISyntaxException e) {
            directoryFilename = directoryFilename.replaceAll("/[^A-Za-z0-9 ]/", "");
        }
        Uri baseUri = Uri.parse(defaultRepositoryStorageDirectory(context));
        return baseUri.buildUpon().appendPath(directoryFilename).build().getPath();
    }

    private static String getStringFromSelector(Context context, int selector, String def) {
        return getStateSharedPreferences(context).getString(getSelector(context, selector), def);
    }

    private static String getSelector(Context context, int selector) {
        return context.getResources().getString(selector);
    }

    /*
     * Last used version.
     */

    public static int lastUsedVersionCode(Context context) {
        String key = context.getResources().getString(R.string.pref_key_last_used_version_code);
        return getStateSharedPreferences(context).getInt(key, 0);
    }

    public static void lastUsedVersionCode(Context context, int value) {
        String key = context.getResources().getString(R.string.pref_key_last_used_version_code);
        getStateSharedPreferences(context).edit().putInt(key, value).apply();
    }

    /*
     * Getting started notebook loaded.
     */

    public static boolean isGettingStartedNotebookLoaded(Context context) {
        String key = context.getResources().getString(R.string.pref_key_is_getting_started_notebook_loaded);
        return getStateSharedPreferences(context).getBoolean(key, false);
    }

    public static void isGettingStartedNotebookLoaded(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_is_getting_started_notebook_loaded);
        getStateSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Last sync.
     */

    public static void lastSuccessfulSyncTime(Context context, Long value) {
        String key = context.getResources().getString(R.string.pref_key_last_successful_sync_time);
        getStateSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long lastSuccessfulSyncTime(Context context) {
        String key = context.getResources().getString(R.string.pref_key_last_successful_sync_time);
        return getStateSharedPreferences(context).getLong(key, 0L);
    }

    /*
     * ReminderWorker
     */

    public static void reminderLastRunForScheduled(Context context, long value) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_scheduled);
        getStateSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long reminderLastRunForScheduled(Context context) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_scheduled);
        return getStateSharedPreferences(context).getLong(key, 0L);
    }

    public static void reminderLastRunForDeadline(Context context, long value) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_deadline);
        getStateSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long reminderLastRunForDeadline(Context context) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_deadline);
        return getStateSharedPreferences(context).getLong(key, 0L);
    }

    public static void reminderLastRunForEvents(Context context, long value) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_event);
        getStateSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long reminderLastRunForEvents(Context context) {
        String key = context.getResources().getString(R.string.pref_key_reminder_service_last_run_for_event);
        return getStateSharedPreferences(context).getLong(key, 0L);
    }


    /*
     * Auto Sync
     */

    public static boolean autoSync(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_auto_sync),
                context.getResources().getBoolean(R.bool.pref_default_auto_sync));
    }

    public static boolean syncOnNoteCreate(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_auto_sync_on_note_create),
                context.getResources().getBoolean(R.bool.pref_default_auto_sync_on_note_create));
    }

    public static boolean syncOnNoteUpdate(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_auto_sync_on_note_update),
                context.getResources().getBoolean(R.bool.pref_default_auto_sync_on_note_update));
    }

    public static boolean syncOnResume(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_auto_sync_on_resume),
                context.getResources().getBoolean(R.bool.pref_default_auto_sync_on_resume));
    }

    public static boolean syncOnSuspend(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_auto_sync_on_suspend),
                context.getResources().getBoolean(R.bool.pref_default_auto_sync_on_suspend));
    }

    /*
     * Notes clipboard
     */

    public static String notesClipboard(Context context) {
        String key = context.getResources().getString(R.string.pref_key_notes_clipboard);
        return getStateSharedPreferences(context).getString(key, null);
    }

    public static void notesClipboard(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_notes_clipboard);
        getStateSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * Refile history
     */

    @Nullable
    public static String refileLastLocation(Context context) {
        String key = context.getResources().getString(R.string.pref_key_refile_last_location);
        return getStateSharedPreferences(context).getString(key, null);
    }

    public static void refileLastLocation(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_refile_last_location);
        getStateSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * Subfolder support
     */
    public static boolean subfolderSupport(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.pref_key_enable_repo_subfolders),
                context.getResources().getBoolean(R.bool.pref_default_enable_repo_subfolders));
    }

    public static void subfolderSupport(Context context, boolean value) {
        String key = context.getResources().getString(R.string.pref_key_enable_repo_subfolders);
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    /*
     * Export and import of user settings
     */

    public static String settingsExportAndImportNoteId(Context context) {
        return getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.pref_key_note_id_for_settings_export_and_import), "");
    }

    public static void settingsExportAndImportNoteId(Context context, String value) {
        String key = context.getResources().getString(R.string.pref_key_note_id_for_settings_export_and_import);
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    /*
     * Repository properties map
     */

    public static void repoPropsMap(Context context, long id, Map<String, String> map) {
        SharedPreferences prefs = getReposSharedPreferences(context);

        repoPropsMapDelete(context, id);

        SharedPreferences.Editor edit = prefs.edit();

        for (String name: map.keySet()) {
            String key = repoPropsMapKeyPrefix(id) + name;
            edit.putString(key, map.get(name));
        }

        edit.apply();
    }

    @NotNull
    public static Map<String, String> repoPropsMap(Context context, long id) {
        Map<String, String> map = new HashMap<>();

        SharedPreferences prefs = getReposSharedPreferences(context);

        for (String key: repoPropsMapKeys(context, id)) {
            String name = key.replace(repoPropsMapKeyPrefix(id), "");
            String value = prefs.getString(key, null);
            if (value != null) {
                map.put(name, value);
            }
        }

        return map;
    }

    /**
     * Delete all preferences belonging to the repository with specified ID.
     */
    public static void repoPropsMapDelete(Context context, long id) {
        SharedPreferences prefs = getReposSharedPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        for (String key: repoPropsMapKeys(context, id)) {
            edit.remove(key);
        }

        edit.apply();
    }

    private static Set<String> repoPropsMapKeys(Context context, long id) {
        Set<String> keys = new HashSet<>();

        SharedPreferences prefs = getReposSharedPreferences(context);

        for (String key: prefs.getAll().keySet()) {
            if (key.startsWith(repoPropsMapKeyPrefix(id))) {
                keys.add(key);
            }
        }

        return keys;
    }

    public static void repoPropsMapDelete(Context context) {
        SharedPreferences prefs = getReposSharedPreferences(context);
        prefs.edit().clear().apply();
    }

    /**
     * We're using the same SharedPreferences file to easily clean up values of deleted
     * repositories. Deleting individual per-repository files might not be trivial as preferences
     * are stored in memory and it's not clear (?) when the backing file is written to.
     */
    private static String repoPropsMapKeyPrefix(long id) {
        return "id-" + id + "-";
    }
}
