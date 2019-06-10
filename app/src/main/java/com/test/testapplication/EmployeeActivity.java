package com.test.testapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class EmployeeActivity extends AppCompatActivity {

    private ArrayList<String[]> employeeData;
    private RecyclerView employeeList;
    private EmployeeListAdapter adapter;
    private String mSearchString="";
    private int selectedPos=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //receive employee list data
        Intent intent = getIntent();
        employeeData = (ArrayList<String[]>) intent.getSerializableExtra("employee_data");

        Toast.makeText(this, "Total "+employeeData.size()+" employees found.", Toast.LENGTH_LONG).show();
        //sort array in alphabetical order of employee name
        Collections.sort(employeeData, new Comparator<String[]>() {
            @Override
            public int compare(String[] strings, String[] t1) {
                return strings[0].compareToIgnoreCase(t1[0]);
            }
        });

        employeeList = findViewById(R.id.employeeList);
        employeeList.setMotionEventSplittingEnabled(false);
        //employee list adapter
        adapter = new EmployeeListAdapter(this, employeeData);
        employeeList.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        employeeList.setLayoutManager(layoutManager);
        employeeList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_employee_list, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (getSupportActionBar() != null) {
            SearchView mSearchView = new SearchView(getSupportActionBar().getThemedContext());
            mSearchView.setQueryHint(getString(R.string.title_search)); /// ADD HINT MESSAGE
            mSearchView.setMaxWidth(Integer.MAX_VALUE);

            mSearchView = (SearchView) myActionMenuItem.getActionView();

            assert searchManager != null;
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(true);

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    mSearchString = newText;
                    if (mSearchString.length() > 0) {
                        selectedPos = search(mSearchString);
                        if (selectedPos != -1) {
                            employeeList.smoothScrollToPosition(selectedPos);
                            adapter.notify(selectedPos); //notify adapter with new selected position
                        } else {
                            adapter.notify(-1); //notify adapter with new selected position
                        }
                    }else {
                        adapter.notify(-1); //notify adapter with new selected position
                    }
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    mSearchString = query;
                    if (mSearchString.length() > 0) {
                        selectedPos = search(mSearchString);
                        if (selectedPos != -1) {
                            employeeList.smoothScrollToPosition(selectedPos);
                            adapter.notify(selectedPos); //notify adapter with new selected position
                        } else {
                            adapter.notify(-1); //notify adapter with new selected position
                        }
                    }else {
                        adapter.notify(-1); //notify adapter with new selected position
                    }
                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getItemId() == R.id.action_bar_graph){
            //start graph activity by passing employee data list array
            Intent intent = new Intent(this, GraphActivity.class);
            intent.putExtra("employee_list", employeeData);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private int search(String key){
        for (int i=0; i<employeeData.size(); i++){
            //search by character in employee name field
            if (employeeData.get(i)[0].toLowerCase().contains(key)){
                return i; //return the first found result position only
            }

            //search by full employee name
            //case sensitive
            /*if (Arrays.asList(employeeData.get(i)).contains(key)) {
                // found a match to key
                return i;
            }*/
        }
        return -1; //return -1 if no item found for search
    }
}
