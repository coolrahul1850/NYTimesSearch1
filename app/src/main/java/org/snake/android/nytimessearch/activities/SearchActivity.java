package org.snake.android.nytimessearch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements AdvSearchDialog.EditNameDialogListener{

    //Bind variables
    @Bind (R.id.toolbar) Toolbar toolbar;

    //static varibales to retain filters
    public static String staticbeginDate;
    public static String staticQuery;
    public static String staticSortOrder;
    public static int staticPageId;

    //array list and adapters
    ArrayList<Article> articles;
    RecycleArticleAdapter rvAdapter;
    RecyclerView rvArticles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if(!isNetworkAvailable())
        {
            alertNetworkDialog();
        }
        setupViews();
    }


    //alert box to check at the start of the app where internet is avaliable or not
    public void alertNetworkDialog()
    {

            AlertDialog.Builder alertDiaglogBuilder = new AlertDialog.Builder(SearchActivity.this);
            alertDiaglogBuilder.setTitle("Internet Needed");
            alertDiaglogBuilder.setMessage("Please connect to the internet to continue");
            alertDiaglogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!isNetworkAvailable())
                    {
                        alertNetworkDialog();
                    }
                }
            });
            alertDiaglogBuilder.show();



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

    //Advanced search window
    private void showAdvancedSearchDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        AdvSearchDialog editNameDialog = AdvSearchDialog.newInstance("Title");
        editNameDialog.show(fm, "item_advanced_search_dialog");
    }

//checks whether device is connected to internet or not
    private Boolean isNetworkAvailable()
    {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                staticQuery = query;
                articles.clear();
                staticPageId =0;
                onArtcileSearched(staticQuery, null, null, null, null, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    //endless scrolling
    public void customLoadMoreDataFromApi(int offset) {

       staticPageId++;
        onArtcileSearched (staticQuery, staticbeginDate, null, null, null, null);

        int curSize = rvAdapter.getItemCount();
        rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
    }


// this is called oncreate when app loads
    public void setupViews()
    {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        articles = new ArrayList<>();

        rvArticles = (RecyclerView) findViewById(R.id.recycleGvResults);
        rvAdapter = new RecycleArticleAdapter(articles);
        rvArticles.setAdapter(rvAdapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);

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

        articles.clear();
        String advBgnDate;
        String advSortOrder;
        String advSports;
        String advFashion;
        String advArts;
        List<String> filter = Arrays.asList(inputText.split(","));
        advBgnDate = filter.get(0).toString().trim();
        staticbeginDate = advBgnDate;


        advSortOrder = filter.get(1).toString().trim();
        staticSortOrder = advSortOrder;

        advArts = filter.get(2).toString().trim();
        advSports = filter.get(3).toString().trim();
        advFashion= filter.get(4).toString().trim();

        Log.d("advBgnDate",advBgnDate);
        Log.d("Sortorder",advSortOrder);
        Log.d("Arts",advArts);
        Log.d("Sports",advSports);
        Log.d("Fashion",advFashion);
        articles.clear();
        onArtcileSearched(staticQuery, staticbeginDate, staticSortOrder, null, null, null);


    }

    public void onArtcileSearched (String query, String beginDate, String sortOrder, String arts, String sports, String fashion)
    {
      //  articles.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key","243e55e75f668c889ac0b0ea783d9169:12:74340175");
        params.put("page",staticPageId);
        params.put("q", query);
        params.put("begin_date",beginDate);
        params.put("sort-order",sortOrder);
        Log.d("Params",params.toString());

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
