package org.snake.android.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.snake.android.nytimessearch.R;
import org.snake.android.nytimessearch.adapters.RecycleArticleAdapter;
import org.snake.android.nytimessearch.model.Article;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements AdvSearchDialog.EditNameDialogListener{

    //Bind variables
    @Bind (R.id.toolbar) Toolbar toolbar;


    public static String beginDate;

    ArrayList<Article> articles;
    RecycleArticleAdapter rvAdapter;
    RecyclerView rvArticles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
      //  isNetworkAvailable();
        setupViews();

    }

    //Dialog Menu options selected
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.action_search:
                break;
            case R.id.advanced_search:
                showAdvancedSearchDialog();
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

//checks whether device is connected to internet or not
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
                onArtcileSearched(query, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        ArrayList<Article> moreArtciles = new ArrayList<Article>();
        articles.addAll(moreArtciles);
        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        int curSize = rvAdapter.getItemCount();
        rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
    }


    public void setupViews()
    {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        articles = new ArrayList<>();

        rvArticles = (RecyclerView) findViewById(R.id.recycleGvResults);
        rvAdapter = new RecycleArticleAdapter(articles);
        rvArticles.setAdapter(rvAdapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
//        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setLayoutManager(gridLayoutManager);

       //utlimited scrolling
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {

            public void onLoadMore(int page, int totalItemsCount) {

                customLoadMoreDataFromApi(page);

            }
        });
        rvAdapter.setOnItemClickListener(new RecycleArticleAdapter.OnItemClickListener() {
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

    }

    @Override
    public void onFinishEditDialog(String inputText) {
       // beginDate = "20160101";
        String advBgnDate = "";
        String advSortOrder = "";


        if (inputText.contains("newest"))
        {
            advSortOrder = "newest";
        }
        if (inputText.contains("oldest"))
        {
            advSortOrder = "oldest";
        }
        Log.d("inputtext",inputText);

    }

    public void onArtcileSearched (String query, String beginDate)
    {
        articles.clear();
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
                    for (int i=0; i<articleJsonResults.length();i++)
                    {
                        JSONObject a = articleJsonResults.optJSONObject(i);
                        Article article = new Article(a);
                        articles.add(article);
                        rvAdapter.notifyDataSetChanged();
                    }

                    Log.d("DEBUG", articles.toString());
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }
}
