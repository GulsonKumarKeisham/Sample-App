package com.test.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final Object TAG = "login_request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText usernameEt = findViewById(R.id.usernameEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (validateInput(username) && validateInput(password)){
                    //call method for login
                    login(username, password);
                    usernameEt.setText("");
                    passwordEt.setText("");
                }else {
                    //show alert dialog for input check
                    showAlertDialog("Invalid input!", "Please check your username and password.", true);
                }
            }
        });
    }

    public boolean validateInput(String input){
        if (input == null){
            return false; //validate input null
        }else return !TextUtils.isEmpty(input); //validate input empty
    }

    public void showAlertDialog(String title, String msg, boolean cancellable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(cancellable);
        builder.show();
    }

    private void login(String username, String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.showProgress("Login...", false);
        Map<String, String> postParam= new HashMap<>();
        postParam.put("username", username);
        postParam.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://tvsfit.mytvs.in/reporting/vrm/api/test_new/int/gettabledata.php", new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<String[]> data = new ArrayList<>();
                        try {
                            JSONObject jsonParent = new JSONObject(response.get("TABLE_DATA").toString());
                            JSONArray jsonArray = jsonParent.getJSONArray("data");
                            for (int i=0; i<jsonArray.length(); i++){
                                String[] strArr = new String[jsonArray.getJSONArray(i).length()];
                                for (int j=0; j<jsonArray.getJSONArray(i).length(); j++){
                                    strArr[j] = jsonArray.getJSONArray(i).getString(j);
                                }
                                data.add(strArr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        progressDialog.finishProgress();
                        Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
                        intent.putExtra("employee_data", data);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.finishProgress();
                if (error instanceof ServerError){
                    showAlertDialog("Login failed!", "Username and password not matched.", true);
                }else if (error instanceof NetworkError){
                    showAlertDialog("Login failed!", "Network error.", true);
                }else if (error instanceof TimeoutError){
                    showAlertDialog("Login failed!", "Timeout error.", true);
                }else {
                    showAlertDialog("Login failed!", "Unknown error."+error, true);
                }
            }
        }) {

            /**
             Passing request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);

        jsonObjReq.setTag(TAG);

    }
}
