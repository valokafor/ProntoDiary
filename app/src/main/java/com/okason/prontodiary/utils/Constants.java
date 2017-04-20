package com.okason.prontodiary.utils;

/**
 * Created by Valentine on 9/4/2015.
 */
public class Constants {

    public static final String REALM_DATABASE = "pronto_notepad.realm";


    public static final String TAG = "Pronto Notes";
    public static final String PACKAGE = "com.okason.prontonote";
    public static final String DATE_FORMAT_SORTABLE = "yyyyMMdd_HHmmss_SSS";
    public static final String DATE_FORMAT_SORTABLE_OLD = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_EXPORT = "yyyy.MM.dd-HH.mm";


    public static final String APP_FOLDER = "ProntoNote";
    public static final String BACKUP_FOLDER = "ProntoNote/Backups";
    public static final String RECORD_FOLDER = "ProntoNote/Records";
    public static final String SORT_PREFERENCE = "sort_PREFERENCE";

    public static final int PRIORITY_NEGATIVE = -1;
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;
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


    public static final String FIRST_RUN = "first_run";
    public static final String SERIALIZED_CATEGORY = "serialized_category";
    public static final String DEFAULT_CATEGORY = "General";

    public static final String NOTE_CLOUD_END_POINT = "/notes";
    public static final String CATEGORY_CLOUD_END_POINT = "/categories";
    public static final String ATTACHMENT_CLOUD_END_POINT = "/attachments";
    public static final String NOTE_ATTACHMENT_CLOUD_END_POINT = "/note_attachments";
    public static final String USERS_CLOUD_END_POINT = "/users/";


    public final static String CATEGORY_ID = "selected_category_id";
    public static final String ATTACHMENTS_FOLDER = "ProntoNote/Attachments";


    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_AUDIO = "audio/amr";
    public final static String MIME_TYPE_SKETCH = "image/png";


    public final static String MIME_TYPE_IMAGE_EXT = ".jpeg";
    public final static String MIME_TYPE_AUDIO_EXT = ".amr";
    public final static String MIME_TYPE_SKETCH_EXT = ".png";


    public static final String FIREBASE_STORAGE_BUCKET = "gs://prontonote.appspot.com";
    public static final String IS_DUAL_SCREEN = "is_dual_screen";
    public static final String SKETCH_PATH = "sketch_path";
    public static final String WELCOME_MESSAGE_SHOWN = "welcome_message_shown";
    public final static String SORT_TITLE = "title";
    public static final String INTENT_TODO_LIST = "todo_list";
    public static final String ADD_NOTE_BUTTON_CLICKED = "add_to_note_button_clicked";
    public static final String SAVE_NOTE_BUTTON_CLICKED = "save_note_button_clicked";
    public static final String ADD_TODO_BUTTON_CLICKED = "add_to_todo_button_clicked";
    public static final String ADD_TASK_BUTTON_CLICKED = "add_to_task_button_clicked";
}
