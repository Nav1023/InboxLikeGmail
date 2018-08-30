package com.example.soc_macmini_15.inboxlikegmail.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soc_macmini_15.inboxlikegmail.Helper.CircularTransform;
import com.example.soc_macmini_15.inboxlikegmail.Helper.FlipAnimator;
import com.example.soc_macmini_15.inboxlikegmail.Model.Message;
import com.example.soc_macmini_15.inboxlikegmail.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private Context mContext;
    private List<Message> messages;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // Array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // Index is used to animate only the selected row
    private static int currentSelectedIndex = -1;

    public MessageAdapter(Context mContext, List<Message> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        this.selectedItems = new SparseBooleanArray();
        this.animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;


        public MyViewHolder(View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.tv_from);
            subject = (TextView) itemView.findViewById(R.id.tv_primary);
            message = (TextView) itemView.findViewById(R.id.tv_secondary);
            iconText = (TextView) itemView.findViewById(R.id.tv_icon_text);
            timestamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            iconBack = (RelativeLayout) itemView.findViewById(R.id.rl_icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.rl_icon_front);
            iconImp = (ImageView) itemView.findViewById(R.id.iv_icon_star);
            imgProfile = (ImageView) itemView.findViewById(R.id.iv_icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.ll_message_container);
            iconContainer = (RelativeLayout) itemView.findViewById(R.id.rl_icon_container);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onRowLongClicked(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messages.get(position);

        //displaying textView data
        holder.from.setText(message.getFrom());
        holder.subject.setText(message.getSubject());
        holder.message.setText(message.getMessage());
        holder.timestamp.setText(message.getTimestamp());

        // Displaying the first letter of from in icon Text
        holder.iconText.setText(message.getFrom().substring(0, 1));

        //change the row state to activited
        holder.itemView.setActivated(selectedItems.get(position, false));

        //change the font style depending on message read status
        applyReadStatus(holder, message);

        // Handle Message Star
        applyImportant(holder, message);

        //handle icon animation
        applyIconAnimation(holder, position);

        //display profile image
        applyProfilePicture(holder, message);

        //apply click events
        applyClickEvents(holder, position);
    }


    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIconClicked(position);
            }
        });
        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIconImportantClicked(position);
            }
        });
        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });
        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onRowLongClicked(position);
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });

    }

    private void applyProfilePicture(MyViewHolder holder, Message message) {
        if (!TextUtils.isEmpty(message.getPicture())) {
            Glide.with(mContext).load(message.getPicture())
                    .thumbnail(0.5f)
                    .transform(new CircularTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.iconText.setVisibility(View.VISIBLE);
            holder.imgProfile.setColorFilter(message.getColor());
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            } else {
                holder.iconBack.setVisibility(View.GONE);
                resetIconYAxis(holder.iconFront);
                holder.iconFront.setVisibility(View.VISIBLE);
                holder.iconFront.setAlpha(1);
                if ((reverseAllAnimations && animationItemsIndex.get(position, false))) {
                    FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                    resetCurrentIndex();
                }
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is resused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    private void applyImportant(MyViewHolder holder, Message message) {
        if (message.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }


    private void applyReadStatus(MyViewHolder holder, Message message) {
        if (message.isImportant()) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelection() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }

}
