package in.kesari.chromakey;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Suraj Nirmal on 13/04/17.
 */

public class IOUtils {

    private static final String TAG = "IO_Utils";

    public interface VolleyFailureCallback {
        void onFailure(String result);
    }

    //Volley JSON Object Request
    public void sendCommonJSONObjectRequest(int RequestType, String API_Tag, final Context context, String url, JSONObject jsonObject, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {


        Log.i("Url",url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(RequestType,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //MyLog.d(API_Tag, response.toString());
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    /*String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    MyLog.d("Error", json);

                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    MyLog.i("ERROR####", error.toString());*/

                    failureCallback.onFailure("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, API_Tag);

    }

    //Volley JSON Array Request
    public void sendCommonJSONArrayRequest(int RequestType, String API_Tag, final Context context, String url, JSONArray jsonArray, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                RequestType,
                url,
                jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        callback.onSuccess(String.valueOf(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        failureCallback.onFailure("");
                    }
                }
        );

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonArrayRequest, API_Tag);
    }

    // Volley String Get Request
    public void getCommonStringRequest(int RequestType, String API_Tag, final Context context, String url, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);

        StringRequest stringRequest = new StringRequest(RequestType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());

                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                failureCallback.onFailure("");
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, API_Tag);
    }


    // Volley String Get Request with Header
    public void getGETStringRequestHeader(final Context context, String url, final VolleyCallback callback) {

        try {
            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //dialog.dismiss();
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("ERROR", "error => " + error.toString());
                            //dialog.dismiss();

                            try {
                                String json = null;
                                NetworkResponse response = error.networkResponse;
                                json = new String(response.data);
                                Log.d("Error", json);

                                Toast.makeText(context, json, Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                //Log.d("Error", e.getMessage());
                            }
                        }
                    }
            );

            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(postRequest, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Volley String POST Request with Header
    public void getPOSTStringRequestHeader(final Context context, String url, final Map<String, String> params, final VolleyCallback callback) {

        try {

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //dialog.dismiss();
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("ERROR", "error => " + error.toString());
                            //dialog.dismiss();
                        }
                    }
            ) ;

            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(postRequest, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Volley JSON Object Post Request with header
    public static void sendJSONObjectRequestHeader(final Context context, String url, JSONObject jsonObject, final VolleyCallback callback) {

        try {

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            //dialog.dismiss();
                            callback.onSuccess(response.toString());
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Log.i("ERROR####", error.toString());
                    //dialog.dismiss();
                }
            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(jsonObjReq, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Volley String Get Request
    public void getGETStringRequest(final Context context, String url, final VolleyCallback callback) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("DriverResponse", response.toString());

                callback.onSuccess(response);

                //dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                //dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "");
    }

    // Volley String Delete Request with Header
    public void getDeleteStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, final VolleyCallback callback) {

        StringRequest postRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        //dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        //dialog.dismiss();

                        try {
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                        } catch (Exception e) {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(postRequest, "");
    }

    public interface VolleyCallback {
        void onSuccess(String result);
    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequest(final Context context, String url, JSONObject jsonObject, final VolleyCallback callback) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                //dialog.dismiss();

                /*try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);


                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                }*/
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request for Dialog
    public void sendJSONObjectRequestHeaderDialog(final Context context, final ViewGroup viewGroup, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                //dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                } catch (Exception e) {

                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };
        ;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, "");

    }

    //Volley JSON Object Put Request
    public void sendJSONObjectPutRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                //dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);


                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());

                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };
        ;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, "");

    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Spanned getColoredSpanned(String text, int color) {
        String input = "<font color='" + color + "'>" + text + "</font>";
        Spanned spannedStrinf = Html.fromHtml(input);
        return spannedStrinf;
    }

    public static String parseDateToddMMMyyyy(String time) {

        String str = null;

        if (!time.isEmpty()) {
            String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String outputPattern = "dd/MM/yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            outputFormat.setTimeZone(TimeZone.getDefault());

            Date date = null;
            // String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            str = "";
        }

        return str;
    }

    public static String parseDateFormat(String time, String inputPattern, String outputPattern) {

        String str = null;

        if (!time.isEmpty()) {
            //String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            outputFormat.setTimeZone(TimeZone.getDefault());

            Date date = null;
            // String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                str = time;
            }
        } else {
            str = "";
        }

        return str;
    }

    public static String parseDateToddMMyyyy(String time) {

        String str = null;

        if (!time.isEmpty()) {
            String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String outputPattern = "dd-MM-yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            outputFormat.setTimeZone(TimeZone.getDefault());

            Date date = null;
            // String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            str = "";
        }

        return str;
    }

    public static String parseDateToTime(String time) {

        String str = null;

        if (!time.isEmpty()) {
            String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String outputPattern = "hh:mm aa";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            outputFormat.setTimeZone(TimeZone.getDefault());

            Date date = null;
            // String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            str = "";
        }

        return str;
    }


}
