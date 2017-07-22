package com.okason.diary.utils;

import android.text.TextUtils;

/**
 * Created by Valentine on 9/4/2015.
 */
public class Constants {

    public static final String REALM_DATABASE = "pronto_notepad.realm";
    public static final String ANONYMOUS_ACCOUNT_USER_ID = "anonymous_account_id";
    public static final String AUTH_METHOD_GOOGLE = "Google";
    public static final String AUTH_METHOD_EMAIL = "Email";
    public static final String AUTH_METHOD_FACEBOOK = "Facebook";
    public static final String FIRST_LOGIN = "first_login";
    public static final String UNREGISTERED_USER = "unregistered_user";
    public static final String NOTE_TITLE = "note_title";
    public static final String RESULT_OK = "ok";
    public static final String SERIALIZED_ATTACHMENT_ID = "serialized_attachment_id";
    public static final String SELECTED_ID = "selected_id";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String STORAGE_RECORD_CLOUD_END_POINT = "/storage_records";
    public static final String FILE_PATH = "file_path";
    public static final String SELECTED_FOLDER_ID = "selected_folder_id";
    public static final String FIRST_RUN = "first_run";
    public static final String FOLDER_ID = "folder_id";
    public static final String EMAIL_ADDRESSS = "email_address";
    public static final String PASSWORD = "password";
    public static final String PRONTO_DIARY_USER_CLOUD_REFERENCE = "pronto_diary_users";
    public static final String REALM_USER_JSON = "realmJson";
    public static final String ACCOUNT_TAG = "account";
    public static final String TAG_ID = "tag_id";
    public static final String DISPLAY_NAME = "display_name";
    public static final String SIGN_IN_METHOD = "sign_in_method";
    public static final String PHOTO_URL = "photo_url";
    public static final String LOGIN_PROVIDER = "login_provider";
    public static final String TAG_FILTER = "TAG_FILTER";
    public static final String ATTACHMENT_ID = "attachment_id";

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    public static final String DATE_FORMAT_SORTABLE = "yyyyMMdd_HHmmss_SSS";
    public static final String DATE_FORMAT_SORTABLE_OLD = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_EXPORT = "yyyy.MM.dd-HH.mm";


    public static final String APP_FOLDER = "ProntoNote";
    public static final String BACKUP_FOLDER = "ProntoNote/Backups";
    public static final String RECORD_FOLDER = "ProntoNote/Records";
    public static final String SORT_PREFERENCE = "sort_PREFERENCE";

    public static final int PRIORITY_NEGATIVE = -1;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_TOP = 3;

    public final static int NOTES = 1;
    public final static int CATEGORIES = 2;
    public final static int SETTINGS = 3;
    public final static int LOGOUT = 4;
    public final static int DELETE = 5;
    public final static int TODOLIST = 6;

    public final static String PREFERENCE_FILE = "preference_file";


    public final static String SHOW_EDITOR_LINE = "show_editor_line";
    public final static String ACTIVE_CATEGORY_ID = "active_category_id";
    public final static String SERIALIZED_TODO_ITEM = "serialized_todo_item";
    public final static String CATEGORY_FILTER = "category_filter";


    public static final String PERMISSION_PROMPT_SHOWN = "permission_prompt_shown";


    public static final String LIST_OF_SORTED_NOTE_ID = "json_list_sorted_note_id";
    public static final String LIST_OF_SORTED_TODO_ID = "json_list_sorted_todo_id";
    public static final String PRONTO_NOTE_FOLDER = "todo_list_id";
    public static final String TODO_LIST_ID = "todo_list_id.txt";
    public static final String AUTO_BACKUP = "auto_backup";
    public static final String MANUAL_BACKUP = "auto_backup";
    public static final String COLUMN_AUTO_BACKUP = AUTO_BACKUP;
    public static final String COLUMN_NUMBER_NOTES = "number_of_notes";
    public static final String SHOULD_START_ADD_TODO = "should_start_add_new_todo_list";
    public static final String TASK_ID = "task_id";
    public static final String SERIALIZED_TODO_LIST = "serialized_tod_list";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_TASK_NOTE = "task_note";
    public static final String SERIALIZED_TASK = "serialized_task";

    public static final String COLUMN_AUDIO_PATH = "audio_path";

    public final static int NOTE_TYPE_TEXT = 1;
    public final static int NOTE_TYPE_IMAGE = 2;
    public final static int NOTE_TYPE_AUDIO = 3;
    public final static int NOTE_TYPE_REMINDER = 4;
    public final static int NOTE_TYPE_SKETCH = 5;


    public static final String NOTE_ID = "note_id";



    public static final String ANONYMOUS_USER = "anonymous_user";
    public static final String SERIALIZED_FOLDER = "serialized_folder";
    public static final String SERIALIZED_TAG = "serialized_tag";
    public static final String DEFAULT_CATEGORY = "General";

    public static final String NOTE_CLOUD_END_POINT = "/notes";
    public static final String FOLDER_CLOUD_END_POINT = "/folders";
    public static final String TAG_CLOUD_END_POINT = "/tags";
    public static final String ATTACHMENT_CLOUD_END_POINT = "/attachments";
    public static final String USERS_CLOUD_END_POINT = "/users/";


    public final static String CATEGORY_ID = "selected_category_id";
    public static final String ATTACHMENTS_FOLDER = "ProntoNote/Attachments";


    public static final String MIME_TYPE_IMAGE = "image/jpeg";
    public static final String MIME_TYPE_AUDIO = "audio/amr";
    public static final String MIME_TYPE_VIDEO = "video/mp4";
    public static final String MIME_TYPE_SKETCH = "image/png";
    public static final String MIME_TYPE_FILES = "file/*";

    public static final String MIME_TYPE_IMAGE_EXT = ".jpeg";
    public static final String MIME_TYPE_AUDIO_EXT = ".amr";
    public static final String MIME_TYPE_VIDEO_EXT = ".mp4";
    public static final String MIME_TYPE_SKETCH_EXT = ".png";
    public static final String MIME_TYPE_CONTACT_EXT = ".vcf";


    public static final String FIREBASE_STORAGE_BUCKET = "gs://prontodiary-bee92.appspot.com";
    public static final String IS_DUAL_SCREEN = "is_dual_screen";
    public static final String SKETCH_PATH = "sketch_path";
    public static final String WELCOME_MESSAGE_SHOWN = "welcome_message_shown";
    public final static String SORT_TITLE = "title";
    public static final String INTENT_TODO_LIST = "todo_list";
    public static final String ADD_NOTE_BUTTON_CLICKED = "add_to_note_button_clicked";
    public static final String SAVE_NOTE_BUTTON_CLICKED = "save_note_button_clicked";
    public static final String ADD_TODO_BUTTON_CLICKED = "add_to_todo_button_clicked";
    public static final String ADD_TASK_BUTTON_CLICKED = "add_to_task_button_clicked";
    public static final String FRAGMENT_TAG = "tag";
    public static final String FRAGMENT_TITLE = "title";
    public static final String NOTE_LIST_FRAGMENT_TAG = "note_list_fragment";
    public static final String TODO_LIST_FRAGMENT_TAG = "todo_list_fragment";
    public static final String SYNC_FRAGMENT_TAG = "sync_fragment";
    public static final String TAG_FRAGMENT_TAG = "tag_fragment";
    public static final String FOLDER_FRAGMENT_TAG = "folder_fragment";


    public static final String TAG = "Pronto Diary";
    public static final String EXTERNAL_STORAGE_FOLDER = "Pronto_Diary";
    public static final String PACKAGE = "com.okason.diary";
    public static final String PREFS_NAME = PACKAGE + "_preferences";

    public static final String REMINDER_NO_REMINDER = "no_reminder";
    public static final String REMINDER_HOURLY = "hourly";
    public static final String REMINDER_DAILY = "daily";
    public static final String REMINDER_WEEKLY= "weekly";
    public static final String REMINDER_WEEK_DAYS= "week_days";
    public static final String REMINDER_MONTHLY = "monthly";
    public static final String REMINDER_YEARLY = "yearly";

}
