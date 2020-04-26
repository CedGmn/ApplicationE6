package com.example.myapplication.requestRegister;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyRequest {

    private Context context;
    private RequestQueue queue;

    public MyRequest(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void register(final String pseudo, final String email, final String password, final String password2, final RegisterCallback callback){

        String url = "http://192.168.1.26/espaceMembre/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Map<String, String> errors = new HashMap<>();

                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if (!error){
                        //l'inscription s'est bien déroulée
                        callback.onSuccess("Vous etes bien inscrit");
                    }else {

                        JSONObject message = json.getJSONObject("message");
                        if(message.has("pseudo")){
                            errors.put("pseudo", message.getString("pseudo"));
                        }

                        if(message.has("email")){
                            errors.put("email", message.getString("email"));
                        }

                        if(message.has("password")){
                            errors.put("password", message.getString("password"));
                        }

                        callback.inputError(errors);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    callback.onError("impossible de se connecter");
                }else if(error instanceof VolleyError){

                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("pseudo", pseudo);
                map.put("email", email);
                map.put("password", password);
                map.put("password2", password2);

                return map;

            }
        };

        queue.add(request);

    }

    public interface RegisterCallback{
        void onSuccess(String message);
        void inputError(Map<String, String> errors);
        void onError(String message);
    }

}