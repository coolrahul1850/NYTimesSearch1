package org.snake.android.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.snake.android.nytimessearch.R;
import org.snake.android.nytimessearch.model.Article;

import java.util.List;

/**
 * Created by rmukhedkar on 2/9/16.
 */
public class RecycleArticleAdapter extends RecyclerView.Adapter<RecycleArticleAdapter.ViewHolder>{
    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener{
        void onItemClick (View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



    private List<Article> mrecycleArcticles;
    public RecycleArticleAdapter (List<Article> recycleArcticles)
    {
        mrecycleArcticles= recycleArcticles;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView recycleImageView;
        public TextView recycleTvTitle;
        private Context context;



        public ViewHolder (final View itemView)
        {
            super(itemView);
            recycleImageView = (ImageView) itemView.findViewById(R.id.ivImage);
            recycleTvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            this.context = context;
         //   itemView.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("RecycleViewClicked", "Clicked");
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
//                    Intent intent = new Intent (itemView.getContext(), ArticleActivity.class);
//                    intent.putExtra("hey","hey");
//                    startActivity(intent);

                }
            });
        }


//        @Override
//        public void onClick(View v) {
//
//            int position = getLayoutPosition();
//            Log.d("Clicked",position+"");
//
//        }
    }

    @Override
    public RecycleArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleArticleAdapter.ViewHolder viewholder, int position) {

        Article article = mrecycleArcticles.get(position);

        TextView tvtitle = viewholder.recycleTvTitle;
        tvtitle.setText(article.getHeadLine());

        ImageView imageView = (ImageView) viewholder.recycleImageView;
        imageView.setImageResource(0);


            String thumbNail = article.getThumbNail();

            if(!TextUtils.isEmpty(thumbNail))
            {
                Picasso.with(imageView.getContext()).load(thumbNail).into(imageView);
            }

    }

    @Override
    public int getItemCount() {
        return mrecycleArcticles.size();
    }


}

