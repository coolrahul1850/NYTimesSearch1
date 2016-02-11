package org.snake.android.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.snake.android.nytimessearch.R;
import org.snake.android.nytimessearch.adapters.ArticleArrayAdapter;
import org.snake.android.nytimessearch.adapters.RecycleArticleAdapter;
import org.snake.android.nytimessearch.model.Article;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements AdvSearchDialog.EditNameDialogListener{

    @Bind (R.id.etQuery) EditText etQuery;
    @Bind (R.id.gvResults) GridView gvResults;
    @Bind (R.id.btnSearch) Button btnSearch;
    @Bind (R.id.toolbar) Toolbar toolbar;

    public static String beginDate;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
      //  isNetworkAvailable();
        setupViews();

    }



    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.action_search:
             //   Toast.makeText(SearchActivity.this, "Action search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.advanced_search:
                showAdvancedSearchDialog();
               // Toast.makeText(SearchActivity.this, "Advanced search", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
        return true;
    }

    private void showAdvancedSearchDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        AdvSearchDialog editNameDialog = AdvSearchDialog.newInstance("Title");

        editNameDialog.show(fm, "item_advanced_search_dialog");

    }


    private Boolean isNetworkAvailable()
    {
        Boolean isConnected = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        Log.d("Network", isConnected.toString());
        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onArtcileSearched(query,null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }




    public void setupViews()
    {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);


        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.recycleGvResults);
        RecycleArticleAdapter rvAdapter = new RecycleArticleAdapter(articles);
        rvArticles.setAdapter(rvAdapter);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));

        rvAdapter.setOnItemClickListener(new RecycleArticleAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View itemView, int position) {
                // create an intent to display the arcticle
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                //lauch the activity
                i.putExtra("article", article);
                //start activity
                startActivity(i);
            }
        });

        //on item click listener
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the arcticle
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                //lauch the activity
                i.putExtra("article", article);
                //start activity
                startActivity(i);
            }
        });

    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        onArtcileSearched (query,null);
    }


    @Override
    public void onFinishEditDialog(String inputText) {
        beginDate = inputText;
        //Toast.makeText(this, "Hi, " + beginDate, Toast.LENGTH_SHORT).show();
        onArtcileSearched("india",beginDate);
    }

    public void onArtcileSearched (String query, String beginDate)
    {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key","243e55e75f668c889ac0b0ea783d9169:12:74340175");
        params.put("page",0);
        params.put("q", query);
        params.put("begin_date",beginDate);

        client.get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try{
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }
}
