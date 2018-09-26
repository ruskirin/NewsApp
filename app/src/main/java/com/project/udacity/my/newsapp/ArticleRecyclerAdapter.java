package com.project.udacity.my.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.MyViewHolder> {

    private List<Article> articles;

    public ArticleRecyclerAdapter(List<Article> articles) {
        this.articles = articles;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title,
                         date,
                         author,
                         body,
                         link;

        private LinearLayout headline,
                             expandView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recycleritem_title);
            date = itemView.findViewById(R.id.recycleritem_date);
            author = itemView.findViewById(R.id.recycleritem_author);
            body = itemView.findViewById(R.id.recycleritem_body);
            link = itemView.findViewById(R.id.recycleritem_link);
            headline = itemView.findViewById(R.id.recycleritem_headline);
            expandView = itemView.findViewById(R.id.recycleritem_info);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View viewHolder = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_recycleritem, viewGroup, false);

        MyViewHolder vh = new MyViewHolder(viewHolder);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int pos) {
        final Article article = articles.get(pos);
        final int position = pos;

        myViewHolder.expandView.setVisibility(
                article.isExpanded() ? View.VISIBLE : View.GONE);

        myViewHolder.title.setText(article.getTitle());
        myViewHolder.date.setText(article.getDate());
        myViewHolder.author.setText(null);
        myViewHolder.link.setText(null);
        myViewHolder.body.setText(null);

        if (article.isExpanded()) {
            myViewHolder.author.setText(article.getAuthor());
            myViewHolder.link.setText(R.string.to_weblink);

            //Received html formatted text, following formats it into regular form
            //Conditional required for different SDK versions
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                myViewHolder.body.setText(Html.fromHtml(article.getBody(), Html.FROM_HTML_MODE_LEGACY));
            else
                myViewHolder.body.setText(Html.fromHtml(article.getBody()));
        }

        myViewHolder.headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    article.setExpanded(!article.isExpanded());
                    notifyItemChanged(position);
            }
        });

        myViewHolder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWebUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getWebUrl()));
                v.getContext().startActivity(toWebUrl);
            }
        });
    }

    @Override
    public int getItemCount() {

        if(articles == null || articles.size() == 0)
            return 0;
        else
            return articles.size();
    }

    public void addList(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void clear() {
        articles = null;
    }
}
